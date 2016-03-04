package xyz.jhughes.laundry.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.jhughes.laundry.AnalyticsApplication;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.MachineService;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.adapters.LocationAdapter;

/**
 * Created by vieck on 10/27/15.
 */
public class LocationActivity extends AppCompatActivity {
    private Tracker mTracker;
    private final String activityName = "Location List";

    private Context mContext;

    private RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;

    private HashMap<String, Integer[]> locationHashMap;

    private LocationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        locationHashMap = new HashMap<>();
        mContext = this;

        for (String name : Constants.getListOfRooms()) {
            Integer[] array = getLaundryCall(Constants.getName(name));
            locationHashMap.put(name, array);
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.location_activity_toolbar);
        setSupportActionBar(toolbar);

        try {
            // Obtain the shared Tracker instance.
            AnalyticsApplication application = (AnalyticsApplication) getApplication();
            mTracker = application.getDefaultTracker();
        } catch(Exception e) {
            Log.e("AnalyticsException", e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mTracker.setScreenName(activityName);
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        } catch(Exception e) {
            Log.e("AnalyticsException", e.getMessage());
        }
    }

    protected Integer[] getLaundryCall(String name) {
        final Integer[] countArray = new Integer[4];
        for (int i = 0; i < 4; i++) {
            countArray[i] = 0;
        }
        Call<ArrayList<Machine>> call = MachineService.getService().getMachineStatus(name);

        call.enqueue(new Callback<ArrayList<Machine>>() {
            @Override
            public void onResponse(Response<ArrayList<Machine>> response, Retrofit retrofit) {
                ArrayList<Machine> classMachines = response.body();
                for (Machine machine : classMachines) {
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

                adapter = new LocationAdapter(locationHashMap, mContext);
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("LocationActivity", "API Fail " + t.getMessage());
            }
        });
        return countArray;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        return super.onCreateOptionsMenu(menu);
    }
}
