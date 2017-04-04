package xyz.jhughes.laundry.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.jhughes.laundry.LaundryParser.Location;
import xyz.jhughes.laundry.LaundryParser.MachineList;
import xyz.jhughes.laundry.ModelOperations;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.adapters.LocationAdapter;
import xyz.jhughes.laundry.analytics.AnalyticsHelper;
import xyz.jhughes.laundry.analytics.ScreenTrackedActivity;
import xyz.jhughes.laundry.apiclient.MachineService;
import xyz.jhughes.laundry.storage.SharedPrefsHelper;

/**
 * The main activity of the app. Lists the locations of
 * laundry and an overview of the availabilities.
 */
public class LocationActivity extends ScreenTrackedActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    @Bind(R.id.recycler_view) RecyclerView recyclerView;
    @Bind(R.id.location_activity_toolbar) Toolbar toolbar;
    @Bind(R.id.progressBar) ProgressBar mLoadingProgressBar;
    @Bind(R.id.location_list_puller) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind((R.id.location_error_text)) TextView errorTextView;
    @Bind(R.id.location_error_button) Button errorButton;

    private LocationAdapter adapter;

    private boolean error = false;

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor e = SharedPrefsHelper.getSharedPrefs(this).edit();
        e.putString("lastScreenViewed", "LocationList");
        e.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);

        String msg;
        if((msg = getIntent().getStringExtra("error")) != null) {
            showErrorMessage(msg);
        } else if (!isNetworkAvailable()) {
            showNoInternetDialog();
        } else if (!getIntent().getBooleanExtra("forceMainMenu", false)) {
            String lastRoom = SharedPrefsHelper.getSharedPrefs(this)
                    .getString("lastScreenViewed", null);
            if (lastRoom != null && !lastRoom.equals("LocationList")) {
                Intent intent = new Intent(this, MachineActivity.class);
                Bundle b = new Bundle();
                b.putString("locationName", lastRoom);
                intent.putExtras(b);
                startActivity(intent);
            }
        }

        setScreenName("Location List");

        initRecyclerView();
        initToolbar();

        mSwipeRefreshLayout.setOnRefreshListener(this);
        errorButton.setOnClickListener(this);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerView.setAdapter(null);
        //We only want to clear the adapter/show the loading
        // if there are no items in the list already.
        if(recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() <= 0) {
            recyclerView.setAdapter(null);
        }
        if(!error) {
            getLaundryCall();
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void getLaundryCall() {

        if(!isNetworkAvailable()) {
            mSwipeRefreshLayout.setRefreshing(false);
            if(error) showErrorMessage("You have no internet connection.");
            else showNoInternetDialog();
            return;
        }
        hideErrorMessage();

        Call<Map<String,MachineList>> allMachineCall = MachineService.getService().getAllMachines();
        allMachineCall.enqueue(new Callback<Map<String, MachineList>>() {
            @Override
            public void onResponse(Call<Map<String, MachineList>> call, Response<Map<String, MachineList>> response) {
                if(response.isSuccessful()) {
                    Map<String,MachineList> machineMap = response.body();
                    List<Location> locations = ModelOperations.machineMapToLocationList(machineMap);
                    adapter = new LocationAdapter(locations, LocationActivity.this.getApplicationContext());

                    //We conditionally make the progress bar visible,
                    // but its cheap to always dismiss it without checking
                    // if its already gone.
                    mLoadingProgressBar.setVisibility(View.GONE);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(adapter);
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    int httpCode = response.code();
                    if(httpCode < 500) {
                        //client error
                        showErrorMessage(getString(R.string.error_client_message));
                    } else {
                        //server error
                        showErrorMessage(getString(R.string.error_server_message));
                        AnalyticsHelper.getDefaultTracker().send(
                                new HitBuilders.ExceptionBuilder()
                                        .setDescription("Error: {" +
                                                " HTTP Code: " + String.valueOf(httpCode) +
                                                " Message: " + response.message() +
                                                " }")
                                        .setFatal(false)
                                        .build());
                    }

                }
            }

            @Override
            public void onFailure(Call<Map<String, MachineList>> call, Throwable t) {
                Log.e("LocationActivity", "API ERROR - " + t.getMessage());
                //likely a timeout -- network is available due to prev. check
                showErrorMessage(getString(R.string.error_server_message));
                AnalyticsHelper.getDefaultTracker().send(
                        new HitBuilders.ExceptionBuilder()
                                .setDescription("Error: {" +
                                        " HTTP Code: -1" +
                                        " Message: " + t.getMessage() +
                                        " }")
                                .setFatal(false)
                                .build());

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void showErrorMessage(String message) {
        error = true;
        errorTextView.setText(message);
        recyclerView.setAdapter(null);
        mLoadingProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
        errorTextView.setVisibility(View.VISIBLE);
        errorButton.setVisibility(View.VISIBLE);
    }

    public void hideErrorMessage() {
        error = false;
        errorTextView.setVisibility(View.GONE);
        errorButton.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRefresh() {
        getLaundryCall();
        mSwipeRefreshLayout.setRefreshing(true);
    }

    private void showNoInternetDialog() {
        error = true;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Connection Error");
        alertDialogBuilder.setMessage("You have no internet connection");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showErrorMessage("You have no internet connection");
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onClick(View v) {
        if(v.equals(errorButton)) {
            mLoadingProgressBar.setVisibility(View.VISIBLE);
            getLaundryCall();
        }
    }
}
