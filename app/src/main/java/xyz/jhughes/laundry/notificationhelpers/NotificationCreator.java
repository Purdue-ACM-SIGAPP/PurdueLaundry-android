package xyz.jhughes.laundry.notificationhelpers;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;


import java.util.HashMap;
import java.util.concurrent.TimeUnit;


import xyz.jhughes.laundry.R;


public class NotificationCreator {

    private final static String GROUP_KEY_EMAILS = "laundry_notif_group_key";
    private static HashMap<String, Integer> notifcationIds = new HashMap<>();
    private static HashMap<Integer, CountDownTimer> timers = new HashMap<>();
    private static int id = 0;

    public static void createNotification(final Context mContext, final String machine, int timeLeft) {
        notifcationIds.put(machine, id);
        CountDownTimer timer = new CountDownTimer(timeLeft, 1000) {
            public void onTick(long millisUntilFinished) {
                updateTimeNotification(machine, mContext, millisUntilFinished);
            }

            public void onFinish() {
                updateTimeNotification(machine, mContext, 0);
            }
        }.start();
        timers.put(id, timer);

        id++;
    }

    private static void updateTimeNotification(String machine, Context context,long timeLeft) {
        int id = notifcationIds.get(machine);
        String countDown;
        if(timeLeft != 0) {
            countDown = String.format("%01d minutes left", TimeUnit.MILLISECONDS.toMinutes(timeLeft));
        } else {
            countDown = " is finished!";
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Intent cancelIntent  = new Intent(context,NotificationCancelReceiver.class);
        cancelIntent.putExtra("notificationId", id);
        cancelIntent.putExtra("machine",machine);
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

        // if time up or at 5 minutes
        if(timeLeft == 0 ||(countDown.equals("5:00"))) {
            builder.setVibrate(new long[]{1000, 1000, 1000});
        }

        notificationManager.notify(id,builder.build());
    }

    static void stopTimer(int id, String machine) {
        notifcationIds.remove(machine);
        timers.get(id).cancel();
        timers.remove(id);

    }

    public static boolean notificationExists(String machine){
        return notifcationIds.containsKey(machine);

    }
}
