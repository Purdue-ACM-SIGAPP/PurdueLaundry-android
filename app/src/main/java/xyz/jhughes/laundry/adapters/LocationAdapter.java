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
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.MainActivity;
import xyz.jhughes.laundry.R;

import java.util.HashMap;

/**
 * Created by vieck on 10/29/15.
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private Context mContext;

    private HashMap<String, Integer[]> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public LocationAdapter(HashMap<String, Integer[]> mDataset, Context mContext) {
        this.mContext = mContext;
        this.mDataset = mDataset;
    }

    public void setMachines(HashMap<String, Integer[]> mDataset) {
        this.mDataset.clear();
        this.mDataset = mDataset;
        notifyDataSetChanged();
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
                SharedPreferences.Editor sharedPreferenceEditor = mContext.getSharedPreferences("xyz.jhughes.laundry", mContext.MODE_PRIVATE).edit();
                sharedPreferenceEditor.putString("lastRoom", location);
                sharedPreferenceEditor.apply();
                Intent intent = new Intent(mContext, MainActivity.class);
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
        public CardView cardView;
        public ImageView imageView;
        public TextView location, washerAvailableCount, washerTotalCount, dryerAvailableCount, dryerTotalCount;

        public ViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.card_view);
            imageView = (ImageView) v.findViewById(R.id.image_view_location);
            location = (TextView) v.findViewById(R.id.text_view_location_name);
            washerAvailableCount = (TextView) v.findViewById(R.id.text_view_washer_available_count);
            washerTotalCount = (TextView) v.findViewById(R.id.text_view_washer_total_count);
            dryerAvailableCount = (TextView) v.findViewById(R.id.text_view_dryer_available_count);
            dryerTotalCount = (TextView) v.findViewById(R.id.text_view_dryer_total_count);
        }
    }

    public void setImage(ImageView image, int position) {
        int width = 136;
        int height = 136;
        int imgId = R.drawable.image_windsor;
        switch (Constants.getListOfRooms()[position]) {
            case "Cary Hall West":
                System.out.println(image.getWidth() + " " + image.getHeight());
                imgId = R.drawable.image_cary;
                break;
            case "Earhart Hall":
                imgId = R.drawable.image_earhart;
                break;
            case "Harrison Hall":
                imgId = R.drawable.image_harrison;
                break;
            case "Hawkins Hall":
                imgId = R.drawable.image_hawkins;
                break;
            case "Hillenbrand Hall":
                imgId = R.drawable.image_hillenbrand;
                break;
            case "McCutcheon Hall":
                imgId = R.drawable.image_mccutcheon;
                break;
            case "Meredith Northwest":
            case "Meredith Southeast":
                imgId = R.drawable.image_meredith;
                break;
            case "Owen Hall":
                imgId = R.drawable.image_owen;
                break;
            case "Shreve Hall":
                imgId = R.drawable.image_shreve;
                break;
            case "Tarkington Hall":
                imgId = R.drawable.image_tarkington;
                break;
            case "Third Street Suites":
                imgId = R.drawable.image_tss;
                break;
            case "Wiley Hall":
                imgId = R.drawable.image_wiley;
                break;
            case "Windsor - Duhme":
            case "Windsor - Warren":
                imgId = R.drawable.image_windsor;
                break;

        }
        Picasso.with(mContext).load(imgId).fit().centerCrop().into(image);
    }

}
