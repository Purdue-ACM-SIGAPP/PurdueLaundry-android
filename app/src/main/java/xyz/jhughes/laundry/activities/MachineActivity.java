package xyz.jhughes.laundry.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.MachineFilter;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.adapters.AppSectionsPagerAdapter;
import xyz.jhughes.laundry.analytics.AnalyticsHelper;
import xyz.jhughes.laundry.analytics.ScreenTrackedActivity;
import xyz.jhughes.laundry.storage.SharedPrefsHelper;

/**
 * This activity tracks screen views. The fragments ALSO track screen views.
 */
public class MachineActivity extends ScreenTrackedActivity {

    private String currentRoom;
    private AppSectionsPagerAdapter appSectionsPagerAdapter;

    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.sliding_tabs)
    TabLayout tabLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor e = SharedPrefsHelper.getSharedPrefs(this).edit();
        e.putString("lastScreenViewed", currentRoom);
        e.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine);
        ButterKnife.bind(this);

        if (getIntent().getExtras() == null ||
                getIntent().getExtras().get("locationName") == null) {
            throw new IllegalStateException("This activity cannot be opened without a " +
                    "locationName String stored in the intent.");
        }

        currentRoom = getIntent().getExtras().getString("locationName");

        initToolbar();

        setUpViewPager();
        setScreenName(Constants.getApiLocation(currentRoom));
    }

    private void setUpViewPager() {
        appSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager(), currentRoom);
        viewPager.setAdapter(appSectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initToolbar() {
        toolbar.setTitle(currentRoom);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    public void createDialog() {
        final SharedPreferences p = SharedPrefsHelper.getSharedPrefs(MachineActivity.this);
        final Gson gson = new Gson();
        MachineFilter filter;
        filter = gson.fromJson(p.getString("filter", null), MachineFilter.class);
        if(filter == null) filter = new MachineFilter();

        final List<MachineFilter.State> states = new ArrayList<>();
        states.addAll(filter.getStates());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle(R.string.select_options)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(R.array.options, filter.getStateBoolean(
                        getResources().getStringArray(R.array.options)),
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                MachineFilter.State option = MachineFilter.State.getState(
                                        getResources().getStringArray(R.array.options)[which]);
                                if(isChecked && !states.contains(option)) states.add(option);
                                else states.remove(option);
                            }
                        })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        MachineFilter filter = new MachineFilter(states);
                        SharedPreferences.Editor e = p.edit();
                        e.putString("filter", gson.toJson(filter));
                        e.apply();
                        appSectionsPagerAdapter.notifyDataSetChanged();
                        AnalyticsHelper.sendEventHit("Filters", AnalyticsHelper.CLICK, MachineFilter.State.toString(states));

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing
                    }
                });
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = getParentActivityIntent().putExtra("forceMainMenu", true);
                NavUtils.navigateUpTo(this, i);
                return true;
            case R.id.display_parameters:
                AnalyticsHelper.sendEventHit("Filters", AnalyticsHelper.CLICK, "YES");
                createDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}