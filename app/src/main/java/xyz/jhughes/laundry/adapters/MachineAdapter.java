package xyz.jhughes.laundry.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.jhughes.laundry.BuildConfig;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.SnackbarPostListener;
import xyz.jhughes.laundry.analytics.AnalyticsHelper;
import xyz.jhughes.laundry.apiclient.MachineService;
import xyz.jhughes.laundry.databinding.CardviewMachineBinding;
import xyz.jhughes.laundry.laundryparser.Constants;
import xyz.jhughes.laundry.laundryparser.Machine;
import xyz.jhughes.laundry.laundryparser.MachineStates;
import xyz.jhughes.laundry.laundryparser.MachineTypes;
import xyz.jhughes.laundry.apiclient.MachineAPI;
import xyz.jhughes.laundry.notificationhelpers.NotificationCreator;
import xyz.jhughes.laundry.notificationhelpers.OnMachineChangedToInUse;
import xyz.jhughes.laundry.notificationhelpers.ScreenOrientationLockToggleListener;
import xyz.jhughes.laundry.runnables.MachineCheckerRunnable;
import xyz.jhughes.laundry.storage.SharedPrefsHelper;

import static xyz.jhughes.laundry.laundryparser.MachineStates.AVAILABLE;

public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.ViewHolder> {

    private final String roomName;
    private final SnackbarPostListener listener;
    private final ScreenOrientationLockToggleListener mOnOrientationlockListener;
    @Inject
    MachineAPI machineAPI;
    private List<Machine> currentMachines, allMachines;
    private Context context;

    public MachineAdapter(List<Machine> machines, Context context, Boolean dryers, String roomName, SnackbarPostListener listener, ScreenOrientationLockToggleListener mOnOrientationlockListener) {
        this.context = context;
        this.roomName = roomName;
        this.listener = listener;
        this.mOnOrientationlockListener = mOnOrientationlockListener;

        currentMachines = new ArrayList<>();
        allMachines = new ArrayList<>();

        final SharedPreferences p = SharedPrefsHelper.getSharedPrefs(context);
        boolean filterByAvailable = false;
        try {
            filterByAvailable = p.getBoolean("filter", false);
        } catch (ClassCastException e) {
            filterByAvailable = false;
        }

        filter(dryers, filterByAvailable, machines);
    }

    private void filter(boolean dryers, boolean available, List<Machine> machines) {
        for (Machine m : machines) {
            if (dryers != m.getType().equals(MachineTypes.DRYER)) continue;
            allMachines.add(m);
            if (!available) currentMachines.add(m);
            else if (m.getStatus().equals(AVAILABLE)) currentMachines.add(m);
        }
    }

    @Override
    public MachineAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent,
                                                        int viewType) {
        CardviewMachineBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.cardview_machine, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final Machine machine = currentMachines.get(position);

        holder.binding.setMachine(machine);

        switch (machine.getStatus()) {
            case MachineStates.IN_USE:
                // Instead of showing "In Use", show how many minutes are left!
                holder.binding.machineStatusTextView.setText(machine.getTime()); // this will need to be updated once people start using the machines again...It should be "xx min. left"
                break;
            case MachineStates.READY:
                holder.binding.machineStatusTextView.setText(context.getResources().getStringArray(R.array.options)[1]); // this should be replaced too
                break;
            default:
                holder.binding.machineStatusTextView.setText(machine.getStatus());
                break;
        }
        //holder.timeLeftTextView.setText(m.getTime());
        holder.binding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsHelper.sendEventHit(machine.getType(), AnalyticsHelper.CLICK, machine.getStatus());
                registerNotification(machine);
            }
        });

        int color = Constants.getMachineAvailabilityColor(machine.getStatus());
        holder.binding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, color));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return currentMachines.size();
    }

    //Set and Fire Notification
    private void notificationWithDialog(final int milliInFuture, final String notificationKey) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.alarm))
                .setMessage(context.getString(R.string.ask_set_alarm))
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AnalyticsHelper.sendEventHit("Reminders", AnalyticsHelper.CLICK, "YES");
                        context.startService(new Intent(context, NotificationCreator.class)
                                .putExtra("machine", notificationKey)
                                .putExtra("time", milliInFuture));
                    }
                })
                .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AnalyticsHelper.sendEventHit("Reminders", AnalyticsHelper.CLICK, "NO");
                        dialog.cancel();
                        Toast.makeText(context, R.string.no_alarm_set, Toast.LENGTH_LONG).show();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void registerNotification(Machine m) {
        //For available (green) machines
        if (m.getStatus().equals(AVAILABLE)) {
            waitForMachine(m);
        } else {
            try {
                //For machines that are already running
                int minutesInFuture = Integer.parseInt(m.getTime().substring(0, m.getTime().indexOf(' ')));
                int millisInFuture = minutesInFuture * 60000; //60 seconds * 1000 milliseconds

                String notificationKey = roomName + " " + m.getName();

                if (NotificationCreator.notificationExists(notificationKey)) {
                    listener.postSnackbar(context.getString(R.string.reminder_already_set), Snackbar.LENGTH_LONG);
                } else {
                    notificationWithDialog(millisInFuture, notificationKey);
                }
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                if (m.getStatus().compareTo("Out of order") != 0) {
                    listener.postSnackbar(context.getString(R.string.machine_not_running), Snackbar.LENGTH_SHORT);
                } else {
                    listener.postSnackbar("This machine is offline but may still be functioning. Visit " + m.getName() + " for details.", Snackbar.LENGTH_LONG);
                }
            }
        }
    }

    public boolean createNotification(Machine m) {
        try {
            int minutesInFuture = Integer.parseInt(m.getTime().substring(0, m.getTime().indexOf(' ')));
            int milliInFuture = minutesInFuture * 60000; //60 seconds * 1000 milliseconds

            String notificationKey = roomName + " " + m.getName();

            if (NotificationCreator.notificationExists(notificationKey)) {
                listener.postSnackbar(context.getString(R.string.reminder_already_set), Snackbar.LENGTH_LONG);
            } else {
                context.startService(new Intent(context, NotificationCreator.class)
                        .putExtra("machine", notificationKey)
                        .putExtra("time", milliInFuture));
                Toast.makeText(context, context.getString(R.string.alarm_set), Toast.LENGTH_SHORT).show();
            }
            return true;
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return false;
        }

    }

    public void waitForMachine(final Machine m) {
        //Constructs the dialog to wait for a machine
        //checks the server while the dialog is open and the app is running in the background
        final Handler handler = new Handler();
        mOnOrientationlockListener.onLock();
        AlertDialog.Builder machineWaitingDialogBuilder = new AlertDialog.Builder(context);
        machineWaitingDialogBuilder.setTitle(context.getString(R.string.alarm))
                .setMessage(context.getString(R.string.available_timer_message1) + " " + m.getName() + " " + context.getString(R.string.available_timer_message2))
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.available_timer_refresh), null)
                .setNegativeButton(context.getString(R.string.available_timer_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AnalyticsHelper.sendEventHit("Automatic Timer", "Click", "Timed cancelled");
                        handler.removeCallbacksAndMessages(null);
                        dialog.cancel();
                    }
                });
        //add message to load machine before
        LayoutInflater inflater = LayoutInflater.from(context);
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
                Call<List<Machine>> call = machineAPI.getMachineStatus(apiLocationFormat);
                call.enqueue(new Callback<List<Machine>>() {
                    @Override
                    public void onResponse(Call<List<Machine>> call, Response<List<Machine>> response) {
                        List<Machine> body = response.body();
                        if (body.contains(m)) {
                            Machine m3 = body.get(body.indexOf(m));
                            if (m3.getStatus().equals(MachineStates.IN_USE)) { //
                                createNotification(m3);
                                machineWaitingDialog.cancel();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Machine>> call, Throwable t) {
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
                //logged before call in MachineCheckerRunnable
                createNotification(m);
                machineWaitingDialog.cancel();
            }

            public void onTimeout() {
                AnalyticsHelper.sendEventHit("Automatic Timer", "Timer state", "Timed out");
                machineWaitingDialog.cancel();
            }
        }), MachineCheckerRunnable.TIME);
    }

    public List<Machine> getCurrentMachines() {
        return currentMachines;
    }

    public List<Machine> getAllMachines() {
        return allMachines;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardviewMachineBinding binding;

        private ViewHolder(CardviewMachineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}