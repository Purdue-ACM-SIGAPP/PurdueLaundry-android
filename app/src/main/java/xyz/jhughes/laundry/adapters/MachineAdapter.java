package xyz.jhughes.laundry.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.LaundryParser.MachineStates;
import xyz.jhughes.laundry.LaundryParser.MachineTypes;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.SnackbarPostListener;
import xyz.jhughes.laundry.analytics.AnalyticsHelper;
import xyz.jhughes.laundry.notificationhelpers.NotificationCreator;
import xyz.jhughes.laundry.storage.SharedPrefsHelper;

public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.ViewHolder> {

    private final String roomName;
    private final SnackbarPostListener listener;
    private ArrayList<Machine> currentMachines, allMachines;
    private Context mContext;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MachineAdapter(ArrayList<Machine> machines, Context context, Boolean dryers, String roomName, SnackbarPostListener listener) {
        this.mContext = context;
        this.roomName = roomName;
        this.listener = listener;

        currentMachines = new ArrayList<>();
        allMachines = new ArrayList<>();

        final SharedPreferences p = SharedPrefsHelper.getSharedPrefs(mContext);
        boolean filterByAvailable = p.getBoolean("filter", false);

        filter(dryers, filterByAvailable, machines);
    }

    private void filter(boolean dryers, boolean available, ArrayList<Machine> machines) {
        for(Machine m : machines) {
            if(dryers != m.getType().equals(MachineTypes.DRYER)) continue;
            allMachines.add(m);
            if(!available) currentMachines.add(m);
            else if(m.getStatus().equals(MachineStates.AVAILABLE)) currentMachines.add(m);

        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MachineAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_machine, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final Machine m = currentMachines.get(position);
        holder.nameTextView.setText(m.getName());
        switch (m.getStatus()) {
            case MachineStates.IN_USE:
                // Instead of showing "In Use", show how many minutes are left!
                holder.statusTextView.setText(m.getTime()); // this will need to be updated once people start using the machines again...It should be "xx min. left"

                break;
            case MachineStates.READY:
                holder.statusTextView.setText(mContext.getResources().getStringArray(R.array.options)[1]); // this should be replaced too
                break;
            default:
                holder.statusTextView.setText(m.getStatus());
                break;
        }
        //holder.timeLeftTextView.setText(m.getTime());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsHelper.sendEventHit(m.getType(), AnalyticsHelper.CLICK, m.getStatus());
                registerNotification(m);
            }
        });

        int color = Constants.getMachineAvailabilityColor(m.getStatus());
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, color));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return currentMachines.size();
    }

    //Set and Fire Notification
    private void fireNotificationInFuture(final int milliInFuture, final String notificationKey) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.alarm))
                .setMessage(mContext.getString(R.string.ask_set_alarm))
                .setCancelable(true)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AnalyticsHelper.sendEventHit("Reminders", AnalyticsHelper.CLICK, "YES");
                        mContext.startService(new Intent(mContext, NotificationCreator.class)
                                .putExtra("machine", notificationKey)
                                .putExtra("time", milliInFuture));
                    }
                })
                .setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AnalyticsHelper.sendEventHit("Reminders", AnalyticsHelper.CLICK, "NO");
                        dialog.cancel();
                        Toast.makeText(mContext, R.string.no_alarm_set, Toast.LENGTH_LONG).show();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void registerNotification(Machine m) {
        try {
            int minutesInFuture = Integer.parseInt(m.getTime().substring(0, m.getTime().indexOf(' ')));
            int millisInFuture = minutesInFuture * 60000; //60 seconds * 1000 milliseconds

            String notificationKey = roomName + " " + m.getName();

            if (NotificationCreator.notificationExists(notificationKey)) {
                listener.postSnackbar(mContext.getString(R.string.reminder_already_set), Snackbar.LENGTH_LONG);
            } else {
                fireNotificationInFuture(millisInFuture, notificationKey);
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            if (m.getStatus().compareTo("Out of order") != 0) {
                listener.postSnackbar(mContext.getString(R.string.machine_not_running), Snackbar.LENGTH_SHORT);
            } else {
                listener.postSnackbar("This machine is offline but may still be functioning. Visit " + m.getName() + " for details.", Snackbar.LENGTH_LONG);
            }
        }
    }

    public ArrayList<Machine> getCurrentMachines() {
        return currentMachines;
    }

    public ArrayList<Machine> getAllMachines() {
        return allMachines;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @Bind(R.id.machine_name_text_view)
        TextView nameTextView;
        @Bind(R.id.machine_status_text_view)
        TextView statusTextView;
        @Bind(R.id.card_view)
        CardView cardView;

        private ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}