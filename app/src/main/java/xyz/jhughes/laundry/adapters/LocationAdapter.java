package xyz.jhughes.laundry.adapters;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.jhughes.laundry.activities.MachineActivity;
import xyz.jhughes.laundry.laundryparser.Constants;
import xyz.jhughes.laundry.laundryparser.Location;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.databinding.CardviewLocationBinding;
import xyz.jhughes.laundry.laundryparser.MachineTypes;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private Context context;

    private List<Location> locations;

    public LocationAdapter(List<Location> locations, Context context) {
        this.locations = locations;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardviewLocationBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.cardview_location, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        final Location location = locations.get(position);
        final String locationName = Constants.getLocationName(location.getLocationName());
        boolean isOffline = location.getMachineList().isOffline();

        CardviewLocationBinding binding = holder.binding;
        binding.setLocation(location);
        binding.textViewOffline.setVisibility(View.GONE);
        if (isOffline) {
            binding.cardView.setAlpha((float) 0.6);
            binding.textViewWasherCount.setVisibility(View.GONE);
            binding.textViewWasherTotal.setVisibility(View.GONE);
            binding.textViewDryerCount.setVisibility(View.GONE);
            binding.textViewDryerTotal.setVisibility(View.GONE);
            binding.textViewDryer.setVisibility(View.GONE);
            binding.textViewWasher.setVisibility(View.GONE);
            binding.textViewOffline.setVisibility(View.VISIBLE);
        }
        binding.textViewLocationName.setText(locationName.split("Laundry")[0]);
        setImage(binding.imageViewLocation, Constants.getLocationName(location.getLocationName()));
        binding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MachineActivity.class);
                Bundle b = new Bundle();
                b.putString("locationName", locationName);
                intent.putExtras(b);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardviewLocationBinding binding;

        private ViewHolder(CardviewLocationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setImage(ImageView image, String hall) {
        int imgId = Constants.getLocationImageResource(hall);
        Picasso.with(context).load(imgId).fit().centerCrop().into(image);
    }

}
