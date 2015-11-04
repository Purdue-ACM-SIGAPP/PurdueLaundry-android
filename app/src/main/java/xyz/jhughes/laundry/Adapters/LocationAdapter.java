package xyz.jhughes.laundry.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import javax.crypto.Mac;

import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.R;

/**
 * Created by vieck on 10/29/15.
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private Context mContext;

    private ArrayList<Machine> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public LocationAdapter(ArrayList<Machine> machines, Context mContext) {
        this.mContext = mContext;
        mDataset = new ArrayList<Machine>();
        for (Machine m : machines) {
            if (m.getType().equals("Dryer")) {
                this.mDataset.add(m);
            }
        }
        for (Machine m : machines) {
            if (m.getType().equals("Washer")) {
                this.mDataset.add(m);
            }
        }
    }

    public void setMachines(ArrayList<Machine> machines, Boolean dryers) {
        this.mDataset.clear();
        if (dryers)
            for (Machine m : machines) {
                if (m.getType().equals("Dryer")) {
                    this.mDataset.add(m);
                }
            }
        else {
            for (Machine m : machines) {
                if (m.getType().equals("Washer")) {
                    this.mDataset.add(m);
                }
            }
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_location, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Machine machine = mDataset.get(position);
        //holder.textView.setText(machine.getName());
    }

    @Override
    public int getItemCount() {
        return 9;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;

        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.textview);
        }
    }

}
