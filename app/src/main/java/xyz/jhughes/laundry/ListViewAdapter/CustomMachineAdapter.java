package xyz.jhughes.laundry.ListViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.R;

import java.util.ArrayList;

/**
 * Created by hughesjeff
 */
public class CustomMachineAdapter extends BaseAdapter {

    /**
     * The ArrayList of Machines that we adapt from
     */
    ArrayList<Machine> machines;

    /**
     * The current Application's context. This allows us to use the Layout Inflater Service
     */
    Context c;

    public CustomMachineAdapter(ArrayList<Machine> machines, Context c, boolean dryers) {
        this.machines = new ArrayList<>();
        if (dryers) {
            for (Machine m : machines) {
                if (m.getType().equals("Dryer")) {
                    this.machines.add(m);
                }
            }
        } else {
            for (Machine m : machines) {
                if (m.getType().equals("Washer")) {
                    this.machines.add(m);
                }
            }
        }
        //this.machines = machines;
        this.c = c;
    }

    @Override
    public int getCount() {
        return machines.size();
    }

    @Override
    public Machine getItem(int position) {
        return machines.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_layout_item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.Name);
        TextView status = (TextView) convertView.findViewById(R.id.Status);
        TextView time = (TextView) convertView.findViewById(R.id.time);

        Machine m = getItem(position);

        name.setText(m.getName().replace(" ADA", ""));
        status.setText(m.getStatus());
        time.setText(m.getTime());

        if (m.getType().equals("Dryer")) {
            ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
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
            ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
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

        return convertView;
    }
}
