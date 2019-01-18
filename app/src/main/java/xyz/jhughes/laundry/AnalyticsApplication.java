package xyz.jhughes.laundry;

import android.app.Application;
import android.content.Context;

import xyz.jhughes.laundry.analytics.AnalyticsHelper;
import xyz.jhughes.laundry.injection.AppComponent;
import xyz.jhughes.laundry.injection.AppModule;
import xyz.jhughes.laundry.injection.DaggerAppComponent;

/**
 * Base activity for the app.
 * <p>
 * It handles initializing the Analytics tracker.
 */
public class AnalyticsApplication extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule())
                .build();
        AnalyticsHelper.initDefaultTracker(this);

        if (BuildConfig.DEBUG) {
            //if on a debug version, remove the alert boolean every time. This is expected.
            getSharedPreferences("alerts", Context.MODE_PRIVATE).edit().remove("offline_alert_thrown").apply();
        }
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
