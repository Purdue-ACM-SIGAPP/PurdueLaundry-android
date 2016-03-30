package xyz.jhughes.laundry;

import android.app.Application;
import android.content.Context;

import xyz.jhughes.laundry.analytics.AnalyticsHelper;

/**
 * Base activity for the app.
 *
 * It handles initializing the Analytics tracker.
 */
public class AnalyticsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AnalyticsHelper.initDefaultTracker(this);

        if(BuildConfig.DEBUG) {
            //if on a debug version, remove the alert boolean every time. This is expected.
            getSharedPreferences("alerts", Context.MODE_PRIVATE).edit().remove("offline_alert_thrown").apply();
        }
    }
}
