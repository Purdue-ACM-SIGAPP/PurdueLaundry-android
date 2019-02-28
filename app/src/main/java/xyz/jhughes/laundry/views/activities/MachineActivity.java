package xyz.jhughes.laundry.views.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import xyz.jhughes.laundry.laundryparser.Constants;
import xyz.jhughes.laundry.AnalyticsApplication;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.views.adapters.AppSectionsPagerAdapter;
import xyz.jhughes.laundry.analytics.AnalyticsHelper;
import xyz.jhughes.laundry.analytics.ScreenTrackedActivity;
import xyz.jhughes.laundry.databinding.ActivityMachineBinding;
import xyz.jhughes.laundry.views.storage.SharedPrefsHelper;

/**
 * This activity tracks screen views. The fragments ALSO track screen views.
 */
public class MachineActivity extends ScreenTrackedActivity {
    private static final String SHOW_ONBOARDING_COUNTDOWN = "show_onboarding_countdown";

    /**
     * The number of times to show the snackbar explaining how to start a timer
     * unless the user manually dismisses it.
     */
    private static final int ONBOARDING_COUNTDOWN = 5;

    private ActivityMachineBinding binding;

    private String currentRoom;
    private AppSectionsPagerAdapter appSectionsPagerAdapter;

    Snackbar filterWarningBar;

    Snackbar mOnboardingSnackbar;

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
        ((AnalyticsApplication)getApplication()).getAppComponent().inject(MachineActivity.this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_machine);

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

        showOnboardingIfNecessary();
    }

    /**
     * When the user first accesses the Machines Activity, we should show
     * a snackbar telling them how to create a timer. We hope this will increase
     * the use of timers.
     */
    private void showOnboardingIfNecessary() {
        int numberOfTimesToShowOnboarding =
                SharedPrefsHelper.getSharedPrefs(this).getInt(SHOW_ONBOARDING_COUNTDOWN, ONBOARDING_COUNTDOWN);

        if ((mOnboardingSnackbar != null && !mOnboardingSnackbar.isShown()))
            return;

        //add BuildConfig.DEBUG to this statement to make it display always for testing.
        if (numberOfTimesToShowOnboarding > 0) {
            //show onboarding snackbar.
            mOnboardingSnackbar = Snackbar
                    .make(binding.mainContent,
                            "Tap a running machine to be notified when it finishes.",
                            Snackbar.LENGTH_INDEFINITE)
                    .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        /**
                         * If the user dismisses the snackbar, we should respect their
                         * desire to not show the tutorial again.
                         * We do this by setting the countdown to zero.
                         */
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            if (event == DISMISS_EVENT_SWIPE) {
                                SharedPrefsHelper
                                        .getSharedPrefs(MachineActivity.this)
                                        .edit()
                                        .putInt(SHOW_ONBOARDING_COUNTDOWN,
                                                0)
                                        .apply();
                            }
                        }
                    })
                    .setAction("Don't show again", new View.OnClickListener() {

                        /**
                         * If the user dismisses the snackbar, we should respect their
                         * desire to not show the tutorial again.
                         * We do this by setting the countdown to zero.
                         */
                        @Override
                        public void onClick(View v) {
                            SharedPrefsHelper
                                    .getSharedPrefs(MachineActivity.this)
                                    .edit()
                                    .putInt(SHOW_ONBOARDING_COUNTDOWN,
                                            0)
                                    .apply();
                        }
                    });

            mOnboardingSnackbar.show();

            SharedPrefsHelper
                    .getSharedPrefs(this)
                    .edit()
                    .putInt(SHOW_ONBOARDING_COUNTDOWN,
                            --numberOfTimesToShowOnboarding)
                    .apply();
        }
    }

    private void updateFilteringWarning() {
        final SharedPreferences p = SharedPrefsHelper.getSharedPrefs(MachineActivity.this);
        boolean filtering = false;
        try {
            filtering = p.getBoolean("filter", false);
        } catch (ClassCastException e) {
            filtering = false;
        }

        if (filterWarningBar == null) {
            filterWarningBar = Snackbar.make(binding.mainContent, "Only showing available machines.", Snackbar.LENGTH_INDEFINITE)
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

        if (filtering) {
            filterWarningBar.show();
        } else {
            filterWarningBar.dismiss();
        }
    }

    private void setUpViewPager() {
        appSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager(), currentRoom);
        binding.viewpager.setAdapter(appSectionsPagerAdapter);
        binding.slidingTabs.setupWithViewPager(binding.viewpager);
    }

    private void initToolbar() {
        binding.toolbar.setTitle(currentRoom);
        setSupportActionBar(binding.toolbar);
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
        boolean filtering = false;
        try {
            filtering = p.getBoolean("filter", false);
        } catch (ClassCastException e) {
            filtering = false;
        }
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