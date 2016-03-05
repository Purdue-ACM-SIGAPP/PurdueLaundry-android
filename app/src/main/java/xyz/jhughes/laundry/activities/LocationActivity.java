package xyz.jhughes.laundry.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.apiclient.MachineService;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.adapters.LocationAdapter;
import xyz.jhughes.laundry.analytics.AnalyticsHelper;

/**
 * The main activity of the app. Lists the locations of
 * laundry and an overview of the availabilities.
 */
public class LocationActivity extends AppCompatActivity {
    private final String ACTIVITY_NAME = "Location List";

    @Bind(R.id.recycler_view) RecyclerView recyclerView;
    @Bind(R.id.location_activity_toolbar) Toolbar toolbar;
    @Bind(R.id.progressBar) ProgressBar mLoadingProgressBar;


    private HashMap<String, Integer[]> locationHashMap;

    private LocationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);

        initRecyclerView();
        initToolbar();

        locationHashMap = new HashMap<>();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AnalyticsHelper.sendScreenViewHit(ACTIVITY_NAME);

        recyclerView.setAdapter(null);
        mLoadingProgressBar.setVisibility(View.VISIBLE);

        for (String name : Constants.getListOfRooms()) {
            Integer[] array = getLaundryCall(Constants.getName(name));
            locationHashMap.put(name, array);
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
                //Handler h = new Handler();
                //h.postDelayed(new Runnable() {
                //    @Override
                //    public void run() {
                        adapter = new LocationAdapter(locationHashMap, LocationActivity.this.getApplicationContext());
                        mLoadingProgressBar.setVisibility(View.GONE);
                        recyclerView.setAdapter(adapter);
                //    }
                //}, 5000);
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
