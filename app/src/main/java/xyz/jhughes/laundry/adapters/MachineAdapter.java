package xyz.jhughes.laundry.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.LaundryParser.MachineStates;
import xyz.jhughes.laundry.LaundryParser.MachineTypes;

import xyz.jhughes.laundry.apiclient.MachineService;
import xyz.jhughes.laundry.notificationhelpers.ScreenOrientationLockToggleListener;
import xyz.jhughes.laundry.notificationhelpers.OnMachineChangedToInUse;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.SnackbarPostListener;
import xyz.jhughes.laundry.analytics.AnalyticsHelper;
import xyz.jhughes.laundry.notificationhelpers.NotificationCreator;
import xyz.jhughes.laundry.runnables.MachineCheckerRunnable;
import xyz.jhughes.laundry.storage.SharedPrefsHelper;

public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.ViewHolder> {

    private final String roomName;
    private final SnackbarPostListener listener;
    private final ScreenOrientationLockToggleListener mOnOrientationlockListener;
    private ArrayList<Machine> currentMachines, allMachines;
    private Context mContext;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MachineAdapter(ArrayList<Machine> machines, Context context, Boolean dryers, String roomName, SnackbarPostListener listener, ScreenOrientationLockToggleListener mOnOrientationlockListener) {
        this.mContext = context;
        this.roomName = roomName;
        this.listener = listener;
        this.mOnOrientationlockListener = mOnOrientationlockListener;

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
    private void notificationWithDialog(final int milliInFuture, final String notificationKey) {
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
        //For available (green) machines
        if (m.getStatus().equals("Available")) {
            waitForMachine(m);
        } else {
            try {
                //For machines that are already running
                int minutesInFuture = Integer.parseInt(m.getTime().substring(0, m.getTime().indexOf(' ')));
                int millisInFuture = minutesInFuture * 60000; //60 seconds * 1000 milliseconds

                String notificationKey = roomName + " " + m.getName();

                if (NotificationCreator.notificationExists(notificationKey)) {
                    listener.postSnackbar(mContext.getString(R.string.reminder_already_set), Snackbar.LENGTH_LONG);
                } else {
                    notificationWithDialog(millisInFuture, notificationKey);
                }
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                if (m.getStatus().compareTo("Out of order") != 0) {
                    listener.postSnackbar(mContext.getString(R.string.machine_not_running), Snackbar.LENGTH_SHORT);
                } else {
                    listener.postSnackbar("This machine is offline but may still be functioning. Visit " + m.getName() + " for details.", Snackbar.LENGTH_LONG);
                }
            }
        }
    }

    public boolean createNotification(Machine m){
        try {
            int minutesInFuture = Integer.parseInt(m.getTime().substring(0, m.getTime().indexOf(' ')));
            int milliInFuture = minutesInFuture * 60000; //60 seconds * 1000 milliseconds

            String notificationKey = roomName + " " + m.getName();

            if (NotificationCreator.notificationExists(notificationKey)) {
                listener.postSnackbar(mContext.getString(R.string.reminder_already_set), Snackbar.LENGTH_LONG);
            } else {
                mContext.startService(new Intent(mContext, NotificationCreator.class)
                        .putExtra("machine", notificationKey)
                        .putExtra("time", milliInFuture));
                Toast.makeText(mContext, mContext.getString(R.string.alarm_set), Toast.LENGTH_SHORT).show();
            }
            return true;
        } catch (NumberFormatException | StringIndexOutOfBoundsException e){
            return false;
        }

    }

    public void waitForMachine(final Machine m){
        //Constructs the dialog to wait for a machine
        //checks the server while the dialog is open and the app is running in the background
        final Handler handler = new Handler();
        mOnOrientationlockListener.onLock();
        AlertDialog.Builder machineWaitingDialogBuilder = new AlertDialog.Builder(mContext);
        machineWaitingDialogBuilder.setTitle(mContext.getString(R.string.alarm))
                .setMessage(mContext.getString(R.string.available_timer_message1) + " " + m.getName() + " " + mContext.getString(R.string.available_timer_message2))
                .setCancelable(true)
                .setPositiveButton(mContext.getString(R.string.available_timer_refresh), null)
                .setNegativeButton(mContext.getString(R.string.available_timer_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AnalyticsHelper.sendEventHit("Automatic Timer", "Click", "Timed cancelled");
                        handler.removeCallbacksAndMessages(null);
                        dialog.cancel();
                    }
                });
        //add message to load machine before
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View q;
        if (m.getType().equals(MachineTypes.WASHER)) {
            q = inflater.inflate(R.layout.view_available_washer, null);
        } else {
            q = inflater.inflate(R.layout.view_available_dryer, null);
        }
        machineWaitingDialogBuilder.setView(q);
        TextView number = (TextView) q.findViewById(R.id.machine_name_number);
        number.setText(m.getNumberFromName());
        final AlertDialog machineWaitingDialog = machineWaitingDialogBuilder.create();
        machineWaitingDialog.show();
        machineWaitingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mOnOrientationlockListener.onUnlock();
            }
        });
        machineWaitingDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsHelper.sendEventHit("Automatic Timer", "Click", "Refresh");
                String apiLocationFormat = Constants.getApiLocation(MachineAdapter.this.roomName);
                Call<ArrayList<Machine>> call = MachineService.getService().getMachineStatus(apiLocationFormat);
                call.enqueue(new Callback<ArrayList<Machine>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Machine>> call, Response<ArrayList<Machine>> response) {
                        ArrayList<Machine> body = response.body();
                        if (body.contains(m)){
                            Machine m3 = body.get(body.indexOf(m));
                            if (m3.getStatus().equals(MachineStates.IN_USE)){ //
                                createNotification(m3);
                                machineWaitingDialog.cancel();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Machine>> call, Throwable t) {
                        //User presses refresh but call fails
                        AnalyticsHelper.sendErrorHit(t, false);
                    }
                });
            }
        });
        //This handler will start the loop TIME for checking the status of the machine
        handler.postDelayed(new MachineCheckerRunnable(m, this.roomName, handler, new OnMachineChangedToInUse() {
            @Override
            public void onMachineInUse(Machine m) {
                AnalyticsHelper.sendEventHit("Automatic Timer", "Click", "Timer created", 0);
                createNotification(m);
                machineWaitingDialog.cancel();
            } public void onTimeout(){
                AnalyticsHelper.sendEventHit("Automatic Timer", "Click", "Timed out");
                machineWaitingDialog.cancel();
            }
        }), MachineCheckerRunnable.TIME);
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