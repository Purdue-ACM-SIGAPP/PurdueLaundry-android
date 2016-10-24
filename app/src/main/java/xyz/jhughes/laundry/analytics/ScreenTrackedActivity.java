package xyz.jhughes.laundry.analytics;

import android.support.v7.app.AppCompatActivity;

/**
 * A way to cleanly track the screen view of an Activity.
 */
public abstract class ScreenTrackedActivity extends AppCompatActivity implements ScreenTrackedScreen {
    private String ACTIVITY_NAME;

    public void setScreenName(String name) {
        ACTIVITY_NAME = name;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsHelper.sendScreenViewHit(ACTIVITY_NAME);
    }
}
