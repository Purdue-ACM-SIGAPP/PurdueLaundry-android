package xyz.jhughes.laundry.ListViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.w3c.dom.Text;
import xyz.jhughes.laundry.LaundryMainActivity;
import xyz.jhughes.laundry.LaundryParser.Information;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.R;

import java.util.ArrayList;

/**
 * Created by jeff on 9/29/15.
 */
public class CustomMachineAdapter extends BaseAdapter {

    ArrayList<Machine> machines;
    Context c;

    public CustomMachineAdapter(ArrayList<Machine> machines, Context c) {
        this.machines = machines;
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
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_layout_item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.Name);
        TextView status = (TextView) convertView.findViewById(R.id.Status);

        Machine m = getItem(position);

        name.setText(m.getName());
        status.setText(m.getStatus());

        System.out.println(convertView);

        return convertView;
    }
}
