package xyz.jhughes.laundry.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.adapters.AppSectionsPagerAdapter;
import xyz.jhughes.laundry.fragments.MachineFragment;
import xyz.jhughes.laundry.storage.SharedPrefsHelper;

public class MachineActivity extends AppCompatActivity {

    /**
     * The current room
     */
    private static String currentRoom;

    private AppSectionsPagerAdapter appSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentRoom = SharedPrefsHelper.getSharedPrefs(this).getString("lastRoom", "Cary Hall West");

        initToolbar();

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        appSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager(), currentRoom);
        viewPager.setAdapter(appSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(currentRoom);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    public void createDialog() {
        final boolean[] tempOptions = transformOptions(MachineFragment.options);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle(R.string.select_options)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(R.array.options, tempOptions,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                tempOptions[which] = isChecked;
                            }
                        })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String options = transformOptions(tempOptions);
                        MachineFragment.options = options;
                        appSectionsPagerAdapter.notifyDataSetChanged();

                        SharedPreferences.Editor e = SharedPrefsHelper.getSharedPrefs(MachineActivity.this).edit();
                        e.putString("options", options);
                        e.apply();
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

    private String transformOptions(boolean[] options) {
        String result = "";
        boolean hasFirst = false;

        if (options[0]) {
            result += "Available";
            hasFirst = true;
        }

        if (options[1]) {
            result += hasFirst ? "|In use" : "In use";
            hasFirst = true;
        }

        if (options[2]) {
            result += hasFirst ? "|Almost done" : "Almost done";
            hasFirst = true;
        }

        if (options[3]) {
            result += hasFirst ? "|End of cycle" : "End of cycle";
        }

        return result;
    }

    private boolean[] transformOptions(String options) {
        return new boolean[]{
                options.contains("Available"),
                options.contains("In use"),
                options.contains("Almost done"),
                options.contains("End of cycle")
        };
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
            case R.id.display_parameters:
                createDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static String getSelected() {
        return currentRoom;
    }


}