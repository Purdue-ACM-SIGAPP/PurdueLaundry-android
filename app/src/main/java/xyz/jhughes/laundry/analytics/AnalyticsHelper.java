package xyz.jhughes.laundry.analytics;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import xyz.jhughes.laundry.R;

/**
 * Delegate class for the Analytics.
 *
 * It will handle containing the tracker and posting events to move that out
 * of the rest of the app.
 */
public class AnalyticsHelper {

    private static Tracker mTracker;

    public synchronized static void initDefaultTracker(Application application) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(application);
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        mTracker = analytics.newTracker(R.xml.global_tracker);
    }

    public synchronized static Tracker getDefaultTracker() {
        if (mTracker == null) {
           throw new IllegalStateException("Tracker was not created! Call initDefaultTracker(Application) before this.");
        }
        return mTracker;
    }
}
