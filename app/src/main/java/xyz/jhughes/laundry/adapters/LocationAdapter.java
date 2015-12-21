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

    private HashMap<String,Integer[]> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public LocationAdapter(HashMap<String,Integer[]> mDataset, Context mContext) {
        this.mContext = mContext;
        this.mDataset = mDataset;
    }

    public void setMachines(HashMap<String,Integer[]> mDataset) {
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
        holder.dryerCount.setText(count[1] + "/" + count[0]);
        holder.washerCount.setText(count[3] + "/" + count[2]);
        setImage(holder.imageView, position);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor sharedPreferenceEditor = mContext.getSharedPreferences("xyz.jhughes.laundry", mContext.MODE_PRIVATE).edit();
                sharedPreferenceEditor.putString("lastRoom",location);
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
        public TextView location, dryerCount, washerCount;

        public ViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.card_view);
            imageView = (ImageView) v.findViewById(R.id.image_view_location);
            location = (TextView) v.findViewById(R.id.text_view_location_name);
            dryerCount = (TextView) v.findViewById(R.id.text_view_dryer_count);
            washerCount = (TextView) v.findViewById(R.id.text_view_washer_count);
        }
    }

    public void setImage(ImageView image, int position) {
        switch (Constants.getListOfRooms()[position]) {
            case "Cary West":
                System.out.println(image.getWidth() + " " + image.getHeight());
                Picasso.with(mContext).load(R.drawable.image_cary).resize(223, 185).centerCrop().into(image);
                //image.setImageResource(R.drawable.image_cary);
                break;
            case "Earhart":
                Picasso.with(mContext).load(R.drawable.image_earhart).resize(223, 185).centerCrop().into(image);
                //image.setImageResource(R.drawable.image_earhart);
                break;
            case "Harrison":
                Picasso.with(mContext).load(R.drawable.image_harrison).resize(223, 185).centerCrop().into(image);
                //image.setImageResource(R.drawable.image_harrison);
                break;
            case "Hawkins":
                Picasso.with(mContext).load(R.drawable.image_hawkins).resize(223, 185).centerCrop().into(image);
                //image.setImageResource(R.drawable.image_hawkins);
                break;
            case "Hillenbrand":
                Picasso.with(mContext).load(R.drawable.image_hillenbrand).resize(223, 185).centerCrop().into(image);
                //image.setImageResource(R.drawable.image_hillenbrand);
                break;
            case "McCutcheon":
                Picasso.with(mContext).load(R.drawable.image_mccutcheon).resize(223, 185).centerCrop().into(image);
                //image.setImageResource(R.drawable.image_mccutcheon);
                break;
            case "Meredith Northwest": case "Meredith Southeast":
                Picasso.with(mContext).load(R.drawable.image_meredith).resize(223, 185).centerCrop().into(image);
                //image.setImageResource(R.drawable.image_meredith);
                break;
            case "Owen":
                Picasso.with(mContext).load(R.drawable.image_owen).resize(223, 185).centerCrop().into(image);
                //image.setImageResource(R.drawable.image_owen);
                break;
            case "Shreve":
                Picasso.with(mContext).load(R.drawable.image_shreve).resize(223, 185).centerCrop().into(image);
                //image.setImageResource(R.drawable.image_shreve);
                break;
            case "Tarkington":
                Picasso.with(mContext).load(R.drawable.image_tarkington).resize(223, 185).centerCrop().into(image);
                //image.setImageResource(R.drawable.image_tarkington);
                break;
            case "Third Street":
                Picasso.with(mContext).load(R.drawable.image_tss).resize(223, 185).centerCrop().into(image);
                //image.setImageResource(R.drawable.image_tss);
                break;
            case "Wiley":
                Picasso.with(mContext).load(R.drawable.image_wiley).resize(223, 185).centerCrop().into(image);
                //image.setImageResource(R.drawable.image_wiley);
                break;
            case "Windsor - Duhme":case "Windsor - Warren":
                Picasso.with(mContext).load(R.drawable.image_windsor).resize(223, 185).centerCrop().into(image);
                //image.setImageResource(R.drawable.image_windsor);
                break;

        }
    }

}
