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

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.activities.MachineActivity;
import xyz.jhughes.laundry.storage.SharedPrefsHelper;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private Context mContext;

    private HashMap<String, Integer[]> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public LocationAdapter(HashMap<String, Integer[]> mDataset, Context mContext) {
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
        final String location = Constants.getListOfRooms()[position];
        Integer[] count = mDataset.get(location);
        holder.location.setText(location);
        holder.washerAvailableCount.setText(count[3].toString());
        holder.washerTotalCount.setText("/" + count[2].toString());
        holder.dryerAvailableCount.setText(count[1].toString());
        holder.dryerTotalCount.setText("/" + count[0].toString());
        setImage(holder.imageView, position);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor sharedPreferenceEditor = SharedPrefsHelper.getSharedPrefs(mContext).edit();
                sharedPreferenceEditor.putString("lastRoom", location);
                sharedPreferenceEditor.apply();
                Intent intent = new Intent(mContext, MachineActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.keySet().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @Bind(R.id.card_view) private CardView cardView;
        @Bind(R.id.image_view_location) private ImageView imageView;
        @Bind(R.id.text_view_location_name) private TextView location;
        @Bind(R.id.text_view_washer_count) private TextView washerAvailableCount;
        @Bind(R.id.text_view_washer_total) private TextView washerTotalCount;
        @Bind(R.id.text_view_dryer_count) private TextView dryerAvailableCount;
        @Bind(R.id.text_view_dryer_total) private TextView dryerTotalCount;

        private ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public void setImage(ImageView image, int position) {
        String hall = Constants.getListOfRooms()[position];
        int imgId = Constants.getLocationImageResource(hall);
        Picasso.with(mContext).load(imgId).fit().centerCrop().into(image);
    }

}
