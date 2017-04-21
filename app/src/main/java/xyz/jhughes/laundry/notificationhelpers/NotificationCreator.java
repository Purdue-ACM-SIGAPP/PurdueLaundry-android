package xyz.jhughes.laundry.notificationhelpers;


import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.analytics.AnalyticsHelper;

/*
Creates a notification from an intent.  The intent should have the following variables in the attached bundle
"title" - the title of the notification
"message" - the message of the notification - do not attach for no message
"time" - how long the notification should last
"displayTimer" - boolean, if the timer should be displayed after the message

Note: "machine" has been replace by title
Note: displayTimer defaults to true to account for old code

 */
public class NotificationCreator extends Service {

    private final static String GROUP_KEY_EMAILS = "laundry_notif_group_key";
    private static HashMap<String, Integer> notificationIds = new HashMap<>();
    private static HashMap<Integer, CountDownTimer> timers = new HashMap<>();
    private static int id = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManagerCompat.from(this).cancelAll();

        Object isMachine = intent.getExtras().get("machine");
        String tempTitle;
        if (isMachine != null){
            tempTitle = (String) isMachine;
        } else {
            tempTitle = (String) intent.getExtras().get("title");
        }
        final String title = tempTitle;
        final String message = (String) intent.getExtras().get("message");
        final boolean displayTimer = intent.getExtras().getBoolean("displayTimer", true);
        int timeLeft = (int) intent.getExtras().get("time");

        notificationIds.put(title, id);

        CountDownTimer timer = new CountDownTimer(timeLeft, 1000) {
            public void onTick(long millisUntilFinished) {
                updateTimeNotification(title, message, displayTimer, NotificationCreator.this, millisUntilFinished);
            }

            public void onFinish() {
                updateTimeNotification(title, message, displayTimer, NotificationCreator.this, 0);
            }
        }.start();

        timers.put(id, timer);

        id++;

        return START_REDELIVER_INTENT;
    }

    private static void updateTimeNotification(String title, String message, boolean displayTimer, Context context, long timeLeft) {
        int id = notificationIds.get(title);
        System.out.println(timeLeft);
        String contentText;
        if (message != null){
            contentText = message;
        } else {
            contentText = "";
        }

        String countDown;
        if (timeLeft > 0) {
            countDown = String.format("%01d minutes left", TimeUnit.MILLISECONDS.toMinutes(timeLeft));
        } else {
            timeLeft = 0;
            countDown = " is finished!";
        }
        if (displayTimer){
            contentText = contentText + countDown;
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Intent cancelIntent = new Intent(context, NotificationCancelReceiver.class);
        cancelIntent.putExtra("timeLeft", timeLeft);
        cancelIntent.putExtra("notificationId", id);
        cancelIntent.putExtra("title", title);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_machine_notification)
                .setColor(ContextCompat.getColor(context, R.color.Available))
                .setGroup(GROUP_KEY_EMAILS)
                .addAction(R.drawable.ic_action_content_clear, "Cancel", pendingIntent)
                .setOngoing(true)
                .setPriority(id);

        if (timeLeft == 0) {
            builder.setOngoing(false);
        }

        // if time up or at 5 minutes
        if (timeLeft == 0 || (countDown.equals("5:00"))) {
            AnalyticsHelper.sendEventHit("Reminders", "Passive", "Timer Finished");
            builder.setVibrate(new long[]{1000, 1000, 1000});
        }

        notificationManager.notify(id, builder.build());
    }

    static void stopTimer(int id, String title) {
        notificationIds.remove(title);
        if (timers.get(id) != null) //This could be called when the app has been cleared.
            timers.get(id).cancel();
        timers.remove(id);
    }

    public static boolean notificationExists(String title) {
        return notificationIds.containsKey(title);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
