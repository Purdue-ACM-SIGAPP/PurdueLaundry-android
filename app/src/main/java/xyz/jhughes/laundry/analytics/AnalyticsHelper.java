package xyz.jhughes.laundry.analytics;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

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
    public static final String ERROR = "Error";
    public static final String REMINDER_CLICK_YES = "REMINDER CLICK_YES";
    public static final String REMINDER_CLICK_NO  = "REMINDER_CLICK_NO";

    private static final String SEPARATOR = "|||";

    private static Map<String, Long> mTimingMap = new HashMap<>();

    private static WeakReference<Context> mContext;

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
        // Google Play runs some tests on our app that we don't want to track. This catches that.
        String testLabSetting = Settings.System.getString(application.getContentResolver(), "firebase.test.lab");
        if ("true".equals(testLabSetting)) {
            // Do something when running in Test Lab
            analytics.setDryRun(true);
        }

        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        mTracker = analytics.newTracker(R.xml.global_tracker);

        //Make GA catch all exceptions that arent caught by the system.
        Thread.UncaughtExceptionHandler myHandler = new ExceptionReporter(
                mTracker,
                Thread.getDefaultUncaughtExceptionHandler(),
                application.getBaseContext());

        // Make myHandler the new default uncaught exception handler.
        Thread.setDefaultUncaughtExceptionHandler(myHandler);

        mContext = new WeakReference<Context>(application.getBaseContext());
    }

    /**
     * Used to send a timer hit to GA.
     *
     * @param category Category of the event.
     * @param variable The variable being tracked.
     * @param label    Label of the event.
     */
    public static void sendTimedEvent(String category, String variable, String label, long timeInMillis) {
        checkState();
        try {
            // Get tracker.
            // Build and send an Event.
            mTracker.send(new HitBuilders.TimingBuilder()
                    .setCategory(category)
                    .setVariable(variable)
                    .setLabel(label)
                    .setValue(timeInMillis)
                    .build());
        } catch (Exception e) {
            Log.e("AnalyticsException", e.getMessage());
        }
    }

    /**
     * Start timing an event. This is a helper to track timing across threads or
     * across disparate parts of the app in an easier way. Coupled with endTiming(...)
     * @param category
     * @param variable
     * @param label
     * @return The key for the hashmap. Should be passed to endTiming(...)
     */
    public static String startTiming(String category, String variable, String label) {
        String key = category + SEPARATOR+ variable + SEPARATOR+ label;

        mTimingMap.put(key, System.currentTimeMillis());

        return key;
    }


    public static void endTiming(String category, String variable, String label) {
        String key = category + SEPARATOR+ variable + SEPARATOR+ label;
        endTiming(key);
    }

    public static void endTiming(String key) {
        String[] split = key.split(SEPARATOR);

        Long startTime = mTimingMap.get(key);
        if(startTime ==  null) {
            throw new IllegalStateException("The key provided does not have an associated timing.");
        }

        sendTimedEvent(split[0], split[1], split[2], System.currentTimeMillis() - startTime);
    }

    /**
     * Used to get the default tracker for the app.
     *
     * Note: Only use this if you need to send a very customized event,
     * in general you should add extra methods to this class to manage events.
     *
     * This is marked as Deprecated to discourage directly using it.
     * Please make a relevant method in this class if you need
     * something for a specific use case.
     *
     * @return The default GA Tracker for this app.
     */
    @Deprecated
    protected synchronized static Tracker getDefaultTracker() {
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
     * Sends a basic event hit for the given category, action, and name. Sends a 0 for result.
     *
     * NOTE: When using this method, strings should be added as constants
     * for the sake of consistency across hits.
     *
     * @param category Category of the event.
     * @param action Action of the event.
     * @param label Label of the event.
     */
    public static void sendEventHit(String category, String action, String label) {
        sendEventHit(category, action, label, -1);
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
     * @param value Value of the event.
     */
    public static void sendEventHit(String category, String action, String label, long value) {
        checkState();
        try {
            // Get tracker.
            // Build and send an Event.
            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label);

            if(value != -1) {
                eventBuilder.setValue(value);
            }

            mTracker.send(eventBuilder.build());
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

    public static void sendErrorHit(Throwable error, boolean fatal) {
        checkState();
        try {

            // Get tracker.
            // Build and send an Event.
            mTracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(new StandardExceptionParser(mContext.get(), null)
                            .getDescription(Thread.currentThread().getName(), error))
                    .setFatal(fatal)
                    .build());
        } catch (Exception e) {
            Log.e("AnalyticsException", e.getMessage());
        }
    }
}
