package xyz.jhughes.laundry.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.R;

import java.util.ArrayList;

public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.ViewHolder> {
    private ArrayList<Machine> currentMachines;
    private Context c;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView nameTextView;
        private TextView statusTextView;
        private TextView timeLeftTextView;
        //private ImageView iconView;
        private CardView cardView;
        private boolean alarmSet = false;

        private ViewHolder(View v) {
            super(v);
            nameTextView = (TextView) v.findViewById(R.id.machine_name_text_view);
            statusTextView = (TextView) v.findViewById(R.id.machine_status_text_view);
            timeLeftTextView = (TextView) v.findViewById(R.id.machine_time_left_text_view);
            //iconView = (ImageView)v.findViewById(R.id.icon);
            cardView = (CardView) v.findViewById(R.id.card_view);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MachineAdapter(ArrayList<Machine> machines, Context c, Boolean dryers, String options) {
        this.c = c;
        currentMachines = new ArrayList<Machine>();

        for (Machine m : machines) {
            machineHelper(m, dryers, options);
        }
    }

    public void setMachines(ArrayList<Machine> machines, Boolean dryers, String options) {
        this.currentMachines.clear();
        for (Machine m : machines) {
            machineHelper(m, dryers, options);
        }
    }

    private void machineHelper(Machine m, Boolean dryers, String options) {
        String status = m.getStatus();
        boolean isCorrectType = dryers == m.getType().equals("Dryer");
        boolean matchesParameters = options.contains(status);
        boolean isStillAllowed = !matchesParameters
                && !"Available|In use|Almost done|End of cycle".contains(status)
                && options.contains("In use");

        if (isCorrectType && (matchesParameters || isStillAllowed)) {
            currentMachines.add(m);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MachineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_machine, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final Machine m = currentMachines.get(position);
        holder.nameTextView.setText(m.getName());
        holder.statusTextView.setText(m.getStatus());
        holder.timeLeftTextView.setText(m.getTime());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Machine m = new Machine("Test", "Dryer", "Running", "25 Minutes Left");

                    //this is a hack if I've ever seen one before...
                    fireNotificationInFuture(Integer.parseInt(m.getTime().split(" ")[0]) * 60 * 1000, holder);
                } catch (NumberFormatException e) {
                    Toast.makeText(c, "Machine not running", Toast.LENGTH_LONG).show();
                }
            }
        });

        switch (m.getStatus()) {
            case "Available":
                holder.cardView.setBackgroundColor(ContextCompat.getColor(c, R.color.Available));
                break;
            case "In use":
                holder.cardView.setBackgroundColor(ContextCompat.getColor(c, R.color.InUse));
                break;
            case "Almost done":
                holder.cardView.setBackgroundColor(ContextCompat.getColor(c, R.color.AlmostDone));
                break;
            case "End of cycle":
                holder.cardView.setBackgroundColor(ContextCompat.getColor(c, R.color.Finished));
                break;
            default:
                holder.cardView.setBackgroundColor(ContextCompat.getColor(c, R.color.InUse));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return currentMachines.size();
    }

    //Set and Fire Notification
    private void fireNotificationInFuture(final int milliInFuture, final ViewHolder holder) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setTitle("Alarm");
        alertDialogBuilder.setMessage("Would you like to set an alarm for when the machine is finished?");
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (!holder.alarmSet) {
                    fire(milliInFuture);
                    holder.alarmSet = true;
                    dialog.cancel();
                    Toast.makeText(c, "Not Implemented", Toast.LENGTH_LONG).show();
                } else {
                    dialog.cancel();
                    Toast.makeText(c, "You already have an alarm set for this machine", Toast.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Toast.makeText(c, "No Alarm Set", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void fire(int timeInFuture) {

    }
}