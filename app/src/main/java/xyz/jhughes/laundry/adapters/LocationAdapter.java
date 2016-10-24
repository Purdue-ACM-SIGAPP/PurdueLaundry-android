package xyz.jhughes.laundry.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.Location;
import xyz.jhughes.laundry.ModelOperations;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.activities.MachineActivity;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private Context mContext;

    private List<Location> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public LocationAdapter(List<Location> mDataset, Context mContext) {
        this.mContext = mContext;
        this.mDataset = mDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_location, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        final Location location = mDataset.get(position);
        final String locationName = Constants.getLocationName(location.getLocationName());
        boolean isOffline = location.getMachineList().isOffline();
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
        Integer[] count = ModelOperations.getAvailableCounts(location.getMachineList().getMachines());
        holder.location.setText(locationName);
        holder.washerAvailableCount.setText(count[3].toString());
        holder.washerTotalCount.setText("/" + count[2].toString());
        holder.dryerAvailableCount.setText(count[1].toString());
        holder.dryerTotalCount.setText("/" + count[0].toString());
        setImage(holder.imageView, position,Constants.getLocationName(location.getLocationName()));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MachineActivity.class);
                Bundle b = new Bundle();
                b.putString("locationName", locationName);
                intent.putExtras(b);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
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
