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


public class NotificationCreator extends Service {

    private final static String GROUP_KEY_EMAILS = "laundry_notif_group_key";
    private static HashMap<String, Integer> notifcationIds = new HashMap<>();
    private static HashMap<Integer, CountDownTimer> timers = new HashMap<>();
    private static int id = 0;
    private static NotificationCreator self;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        self = this;

        NotificationManagerCompat.from(this).cancelAll();

        final String machine = (String) intent.getExtras().get("machine");
        int timeLeft = (int) intent.getExtras().get("time");

        notifcationIds.put(machine, id);

        CountDownTimer timer = new CountDownTimer(timeLeft, 1000) {
            public void onTick(long millisUntilFinished) {
                updateTimeNotification(machine, NotificationCreator.this, millisUntilFinished);
                System.out.println(timers.toString());
            }

            public void onFinish() {
                updateTimeNotification(machine, NotificationCreator.this, 0);
                if (timers.isEmpty()) {
                    stopSelf();
                }
            }
        }.start();

        timers.put(id, timer);

        id++;

        return START_REDELIVER_INTENT;
    }

    private static void updateTimeNotification(String machine, Context context, long timeLeft) {
        int id = notifcationIds.get(machine);
        String countDown;
        //System.out.println(timeLeft);
        if (timeLeft > 0) {
            countDown = String.format("%01d minutes left", TimeUnit.MILLISECONDS.toMinutes(timeLeft));
        } else {
            timeLeft = 0;
            countDown = " is finished!";
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Intent cancelIntent = new Intent(context, NotificationCancelReceiver.class);
        cancelIntent.putExtra("notificationId", id);
        cancelIntent.putExtra("machine", machine);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(machine)
                .setContentText(countDown)
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

    static void stopTimer(int id, String machine) {
        notifcationIds.remove(machine);
        if (timers.get(id) != null) //This could be called when the app has been cleared.
            timers.get(id).cancel();
        timers.remove(id);

        System.out.println(self.toString());

        if (timers.isEmpty()) {
            self.stopSelf();
        }
    }

    public static boolean notificationExists(String machine) {
        return notifcationIds.containsKey(machine);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
