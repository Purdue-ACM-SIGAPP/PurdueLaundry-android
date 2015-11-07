package xyz.jhughes.laundry;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.jhughes.laundry.Adapters.LocationAdapter;
import xyz.jhughes.laundry.Adapters.MachineAdapter;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.Machine;

/**
 * Created by vieck on 10/27/15.
 */
public class LocationActivity extends AppCompatActivity {

    private Context mContext;

    private RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;

    private HashMap<String,Integer[]> locationHashMap;

    private LocationAdapter adapter;

    private boolean isDryers;

    private String select;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        locationHashMap = new HashMap<>();
        mContext = this;

        int[] laundryCount = new int[Constants.getListOfRooms().length];
        int[] dryerCount = new int[Constants.getListOfRooms().length];

        for (String name : Constants.getListOfRooms()) {
            Integer[] array = getLaundryCall(Constants.getName(name));
            locationHashMap.put(name, array);
        }
        adapter = new LocationAdapter(locationHashMap, mContext);
        recyclerView.setAdapter(adapter);

    }

    protected Integer[] getLaundryCall(String name) {
        final Integer[] countArray = new Integer[4];
        Call<ArrayList<Machine>> call = MachineService.getService().getMachineStatus(name);

        call.enqueue(new Callback<ArrayList<Machine>>() {
            @Override
            public void onResponse(Response<ArrayList<Machine>> response, Retrofit retrofit) {
                setUpAdapter(response.body());
                ArrayList<Machine> classMachines = response.body();
                for (Machine machine : classMachines) {
                    if (machine.getType().equals("Dryer")) {
                        //Increments Total Dryer Count For Specific Place
                        countArray[0]++;
                        if (machine.getStatus().equals("Available")) {
                            //Increments Available Dryer Count For Specific Place
                            countArray[1]++;
                        }
                    } else {
                        //Increments Total Washer Count For Specific Place
                        countArray[2]++;
                        if (machine.getStatus().equals("Available")) {
                            //Increments Available Washer Count For Specific Place
                            countArray[3]++;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("LocationActivity", t.getMessage());
            }
        });
        return countArray;
    }


    public void setUpAdapter(List<Machine> machines){

    }

}
