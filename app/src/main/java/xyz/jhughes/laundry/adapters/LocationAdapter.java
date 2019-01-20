package xyz.jhughes.laundry.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.jhughes.laundry.laundryparser.Constants;
import xyz.jhughes.laundry.laundryparser.Location;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.databinding.CardviewLocationBinding;

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
        CardviewLocationBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.cardview_location, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        final Location location = mDataset.get(position);
        final String locationName = Constants.getLocationName(location.getLocationName());
        boolean isOffline = location.getMachineList().isOffline();
        holder.binding.setLocation(location);
        /*holder.textViewOffline.setVisibility(View.GONE);
        if (isOffline) {
            holder.cardView.setAlpha((float) 0.6);
            holder.washerAvailableCount.setVisibility(View.GONE);
            holder.washerTotalCount.setVisibility(View.GONE);
            holder.dryerAvailableCount.setVisibility(View.GONE);
            holder.dryerTotalCount.setVisibility(View.GONE);
            holder.textViewDryer.setVisibility(View.GONE);
            holder.textViewWasher.setVisibility(View.GONE);
            holder.textViewOffline.setVisibility(View.VISIBLE);
        }
        Integer[] count = ModelOperations.getAvailableCounts(location.getMachineList().getMachines());
        holder.location.setText(locationName.split("Laundry")[0]);
        holder.washerAvailableCount.setText(count[3].toString());
        holder.washerTotalCount.setText("/" + count[2].toString());
        holder.dryerAvailableCount.setText(count[1].toString());
        holder.dryerTotalCount.setText("/" + count[0].toString());
        setImage(holder.imageView, position, Constants.getLocationName(location.getLocationName()));
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
        });*/
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardviewLocationBinding binding;

        private ViewHolder(CardviewLocationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setImage(ImageView image, int position, String hall) {
        int imgId = Constants.getLocationImageResource(hall);
        Picasso.with(mContext).load(imgId).fit().centerCrop().into(image);
    }

}
