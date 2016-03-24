package xyz.jhughes.laundry.analytics;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import xyz.jhughes.laundry.BuildConfig;
import xyz.jhughes.laundry.R;

/**
 * Delegate class for the Analytics.
 *
 * It will handle containing the tracker and posting events to move that out
 * of the rest of the app.
 */
public class AnalyticsHelper {
    public static final String CLICK = "Click";
    public static final String REMINDER_CLICK_YES = "REMINDER CLICK_YES";
    public static final String REMINDER_CLICK_NO  = "REMINDER_CLICK_NO";

    private static Tracker mTracker;

    /**
     * Must be called first, ideally in the Application onCreate().
     * @param application The application for the GA instance.
     */
    public synchronized static void initDefaultTracker(Application application) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(application);

        if(BuildConfig.DEBUG) {
            analytics.setDryRun(true);
        }

        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        mTracker = analytics.newTracker(R.xml.global_tracker);
    }

    /**
     * Used to get the default tracker for the app.
     *
     * Note: Only use this if you need to send a very customized event,
     * in general you should add extra methods to this class to manage events.
     * @return The default GA Tracker for this app.
     */
    public synchronized static Tracker getDefaultTracker() {
        checkState();
        return mTracker;
    }

    /**
     * Performs a screen hit for the app.
     * It sets the screen name and sends the ScreenView.
     *
     * @param activity_name Name of the screen.
     */
    public static void sendScreenViewHit(String activity_name) {
        checkState();
        mTracker.setScreenName(activity_name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    /**
     * Sends a basic event hit for the given category, action, and name.
     *
     * NOTE: When using this method, strings should be added as constants
     * for the sake of consistency across hits.
     *
     * @param category Category of the event.
     * @param action Action of the event.
     * @param label Label of the event.
     */
    public static void sendEventHit(String category, String action, String label) {
        checkState();
        try {
            // Get tracker.
            // Build and send an Event.
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .build());
        } catch(Exception e) {
            Log.e("AnalyticsException", e.getMessage());
        }
    }

    /**
     * Ensure that the tracker exists. This should be called as
     * the first line of any method using the tracker.
     */
    private static void checkState() {
        if(mTracker == null) {
            throw new IllegalStateException("Tracker was not created! Call initDefaultTracker(Application) before this.");
        }
    }
}
