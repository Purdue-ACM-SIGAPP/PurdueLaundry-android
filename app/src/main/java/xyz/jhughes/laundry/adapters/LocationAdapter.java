package xyz.jhughes.laundry.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.ModelOperations;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.activities.MachineActivity;
import xyz.jhughes.laundry.storage.SharedPrefsHelper;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private Context mContext;

    private Map<String, List<Machine>> mDataset;
    private List<String> allLocations = new ArrayList<>();
    private List<List<Machine>> allMachines = new ArrayList<>();

    // Provide a suitable constructor (depends on the kind of dataset)
    public LocationAdapter(Map<String, List<Machine>> mDataset, Context mContext) {
        this.mContext = mContext;

        for(String key : mDataset.keySet()){
            List<Machine> machines = mDataset.get(key);
            if(! (ModelOperations.machinesOffline(machines))){
                allLocations.add(0,key);
                allMachines.add(0,machines);
            } else {
                allLocations.add(key);
                allMachines.add(machines);
            }

        }
        this.mDataset = mDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_location, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    private Integer[] getCounts(List<Machine> machines){
        final Integer[] countArray = new Integer[4];
        for (int i = 0; i < 4; i++) {
            countArray[i] = 0;
        }
        for (Machine machine : machines) {
            if (machine.getType().equals("Dryer")) {
                //Increments Total Dryer Count For Specific Place
                countArray[0] = countArray[0] + 1;
                if (machine.getStatus().equals("Available")) {
                    //Increments Available Dryer Count For Specific Place
                    countArray[1] = countArray[1] + 1;
                }
            } else {
                //Increments Total Washer Count For Specific Place
                countArray[2] = countArray[2] + 1;
                if (machine.getStatus().equals("Available")) {
                    //Increments Available Washer Count For Specific Place
                    countArray[3] = countArray[3] + 1;
                }
            }
        }
        return countArray;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final String location = Constants.getLocationName(allLocations.get(position));
        List<Machine> machinesByLocation = allMachines.get(position);
        boolean isOffline = ModelOperations.machinesOffline(machinesByLocation);
        holder.textViewOffline.setVisibility(View.GONE);
        if(isOffline){
            holder.cardView.setAlpha((float)0.6);
            holder.washerAvailableCount.setVisibility(View.GONE);
            holder.washerTotalCount.setVisibility(View.GONE);
            holder.dryerAvailableCount.setVisibility(View.GONE);
            holder.dryerTotalCount.setVisibility(View.GONE);
            holder.textViewDryer.setVisibility(View.GONE);
            holder.textViewWasher.setVisibility(View.GONE);
            holder.textViewOffline.setVisibility(View.VISIBLE);
        }
        Integer[] count = getCounts(machinesByLocation);
        holder.location.setText(location);
        holder.washerAvailableCount.setText(count[3].toString());
        holder.washerTotalCount.setText("/" + count[2].toString());
        holder.dryerAvailableCount.setText(count[1].toString());
        holder.dryerTotalCount.setText("/" + count[0].toString());
        setImage(holder.imageView, position,Constants.getLocationName(allLocations.get(position)));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor sharedPreferenceEditor = SharedPrefsHelper.getSharedPrefs(mContext).edit();
                sharedPreferenceEditor.putString("lastRoom", location);
                sharedPreferenceEditor.apply();
                Intent intent = new Intent(mContext, MachineActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allLocations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @Bind(R.id.card_view)  CardView cardView;
        @Bind(R.id.image_view_location) ImageView imageView;
        @Bind(R.id.text_view_location_name) TextView location;
        @Bind(R.id.text_view_washer_count)  TextView washerAvailableCount;
        @Bind(R.id.text_view_washer_total)  TextView washerTotalCount;
        @Bind(R.id.text_view_dryer_count)   TextView dryerAvailableCount;
        @Bind(R.id.text_view_dryer_total)   TextView dryerTotalCount;
        @Bind(R.id.text_view_washer)        TextView textViewWasher;
        @Bind(R.id.text_view_dryer)         TextView textViewDryer;
        @Bind(R.id.text_view_offline)       TextView textViewOffline;

        private ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public void setImage(ImageView image, int position, String hall) {
        int imgId = Constants.getLocationImageResource(hall);
        Picasso.with(mContext).load(imgId).fit().centerCrop().into(image);
    }

}
