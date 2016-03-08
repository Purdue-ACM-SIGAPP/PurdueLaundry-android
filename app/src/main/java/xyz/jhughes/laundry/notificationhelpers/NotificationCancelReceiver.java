package xyz.jhughes.laundry.notificationhelpers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import xyz.jhughes.laundry.analytics.AnalyticsHelper;

public class NotificationCancelReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notificationId", -1);
        String machine = intent.getStringExtra("machine");
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCreator.stopTimer(notificationId, machine);
        manager.cancel(notificationId);
        AnalyticsHelper.sendEventHit("Reminders", AnalyticsHelper.CLICK, "CANCEL");
    }
}
