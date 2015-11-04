package xyz.jhughes.laundry.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.jhughes.laundry.InformationActivity;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.R;


public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.ViewHolder> {
    private ArrayList<Machine> currentMachines;
    private Context c;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView nameTextView;
        public TextView statusTextView;
        private TextView timeLeftTextView;
        private ImageView iconView;
        public CardView cardView;

        public ViewHolder(View v) {
            super(v);
            nameTextView = (TextView)v.findViewById(R.id.machine_name_text_view);
            statusTextView = (TextView)v.findViewById(R.id.machine_status_text_view);
            timeLeftTextView = (TextView)v.findViewById(R.id.machine_time_left_text_view);
            iconView = (ImageView)v.findViewById(R.id.icon);
            cardView = (CardView) v.findViewById(R.id.card_view);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MachineAdapter(ArrayList<Machine> machines, Context c, Boolean dryers) {
        this.c = c;
        currentMachines = new ArrayList<Machine>();
        if (dryers) {
            for (Machine m : machines) {
                if (m.getType().equals("Dryer")) {
                    this.currentMachines.add(m);
                }
            }
        } else {
            for (Machine m : machines) {
                if (m.getType().equals("Washer")) {
                    this.currentMachines.add(m);
                }
            }
        }

    }

    public void setMachines(ArrayList<Machine> machines, Boolean dryers){
        this.currentMachines.clear();
        if (dryers)
            for (Machine m : machines) {
                if (m.getType().equals("Dryer")) {
                    this.currentMachines.add(m);
                }
            }
        else {
            for (Machine m : machines) {
                if (m.getType().equals("Washer")) {
                    this.currentMachines.add(m);
                }
            }
        }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public MachineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.machine_card_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final Machine m = currentMachines.get(position);
        holder.nameTextView.setText(m.getName());
        holder.statusTextView.setText(m.getStatus());
        holder.timeLeftTextView.setText(m.getTime());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c, InformationActivity.class);
                i.putExtra("machine", m);
                c.startActivity(i);
            }
        });

        if (m.getType().equals("Dryer")) {
            ImageView imageView = holder.iconView;
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
            ImageView imageView = holder.iconView;
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

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return currentMachines.size();
    }
}