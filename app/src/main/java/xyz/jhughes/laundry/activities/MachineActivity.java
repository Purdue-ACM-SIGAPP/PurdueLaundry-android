package xyz.jhughes.laundry.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.jhughes.laundry.LaundryParser.Constants;
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

    @Bind(R.id.main_content)
    CoordinatorLayout mMainContent;

    Snackbar filterWarningBar;

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
        updateFilteringWarning();
    }

    private void updateFilteringWarning() {
        final SharedPreferences p = SharedPrefsHelper.getSharedPrefs(MachineActivity.this);
        boolean filtering = p.getBoolean("filter", false);

        if(filterWarningBar == null) {
            filterWarningBar = Snackbar.make(mMainContent, "Only showing available machines.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Show all", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setFilter(false);
                        }
                    })
                    .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            filterWarningBar = null;
                        }
                    });
        }

        if(filtering) {
            filterWarningBar.show();
        }
        else {
            filterWarningBar.dismiss();
        }
//        findViewById(R.id.machine_activity_filtering_textview).setVisibility(
//                filtering ? View.VISIBLE : View.GONE);
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

    private void setFilter(boolean filterState) {
        final SharedPreferences p = SharedPrefsHelper.getSharedPrefs(MachineActivity.this);
        SharedPreferences.Editor e = p.edit();
        e.putBoolean("filter", filterState);
        e.apply();
        appSectionsPagerAdapter.notifyFilterChanged();
        updateFilteringWarning();
        AnalyticsHelper.sendEventHit("Filters", AnalyticsHelper.CLICK, filterState ? "Available only" : "All machines");
    }

    public void createDialog() {
        final SharedPreferences p = SharedPrefsHelper.getSharedPrefs(MachineActivity.this);
        boolean filtering = p.getBoolean("filter", false);
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_filter, null);
        final Switch sw = ((Switch) layout.findViewById(R.id.filter_dialog_switch));
        sw.setChecked(filtering);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle(R.string.select_options)
                .setView(layout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        setFilter(sw.isChecked());
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