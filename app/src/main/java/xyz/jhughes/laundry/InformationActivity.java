package xyz.jhughes.laundry;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import xyz.jhughes.laundry.Helpers.NotificationPublisher;
import xyz.jhughes.laundry.LaundryParser.Machine;

import java.util.Scanner;

public class InformationActivity extends AppCompatActivity {

    Machine classMachine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infromation);

        Machine machine = (Machine) getIntent().getSerializableExtra("machine");
        classMachine = machine;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(machine.getName());

        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            //If null, just return because all things are broke.
            return;
        }

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ImageView imageView = (ImageView) findViewById(R.id.machineImage);
        setImage(imageView, machine);

        ((TextView) findViewById(R.id.MachineID)).setText(machine.getName());

        if (machine.getTime().hashCode() == 160) {
            ((TextView) findViewById(R.id.MachineTime)).setText("Available");
        } else {
            ((TextView) findViewById(R.id.MachineTime)).setText(machine.getTime());
        }
    }

    public void setImage(ImageView imageView, Machine m) {
        if (m.getType().equals("Dryer")) {
            switch (m.getStatus()) {
                case "Available":
                    imageView.setImageResource(R.drawable.dryer_available);
                    break;
                case "In use":
                    imageView.setImageResource(R.drawable.dryer_running);
                    break;
                case "Almost done":
                    imageView.setImageResource(R.drawable.dryer_almost_done);
                    break;
                case "End of cycle":
                    imageView.setImageResource(R.drawable.dryer_end_cycle);
                    break;
                default:
                    imageView.setImageResource(R.drawable.dryer_running);
            }
        } else if (m.getType().equals("Washer")) {
            switch (m.getStatus()) {
                case "Available":
                    imageView.setImageResource(R.drawable.washer_available);
                    break;
                case "In use":
                    imageView.setImageResource(R.drawable.washer_running);
                    break;
                case "Almost done":
                    imageView.setImageResource(R.drawable.washer_almost_done);
                    break;
                case "End of cycle":
                    imageView.setImageResource(R.drawable.washer_end_cycle);
                    break;
                default:
                    imageView.setImageResource(R.drawable.washer_running);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_infromation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        finish();

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        if (classMachine.getTime().hashCode() == 160) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Machine Error");
            alertDialogBuilder.setMessage("Machine is not running!");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else if (v.getId() == R.id.time_end) {
            int index = classMachine.getTime().indexOf(' ');
            int delay = Integer.parseInt(classMachine.getTime().substring(0, index));
            delay *= 60 * 1000; //60 seconds per minute, 1000 milliseconds per second
            Notification n = getNotification(classMachine.getName() + " is ready!");
            scheduleNotification(n, delay);
        } else if (v.getId() == R.id.time_five_left) {
            System.out.println("NYI");
        }
    }

    private void scheduleNotification(Notification notification, int delay) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Laundry is ready!");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher);
        return builder.build();
    }
}