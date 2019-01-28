package xyz.jhughes.laundry.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.jhughes.laundry.AnalyticsApplication;
import xyz.jhughes.laundry.LaundryParser.Location;
import xyz.jhughes.laundry.LaundryParser.LocationResponse;
import xyz.jhughes.laundry.LaundryParser.MachineList;
import xyz.jhughes.laundry.LaundryParser.Rooms;
import xyz.jhughes.laundry.ModelOperations;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.adapters.LocationAdapter;
import xyz.jhughes.laundry.analytics.AnalyticsHelper;
import xyz.jhughes.laundry.analytics.ScreenTrackedActivity;
import xyz.jhughes.laundry.apiclient.MachineAPI;
import xyz.jhughes.laundry.storage.SharedPrefsHelper;

/**
 * The main activity of the app. Lists the locations of
 * laundry and an overview of the availabilities.
 */
public class LocationActivity extends ScreenTrackedActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.location_activity_toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar)
    ProgressBar mLoadingProgressBar;
    @BindView(R.id.location_list_puller)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView((R.id.location_error_text))
    TextView errorTextView;
    @BindView(R.id.location_error_button)
    Button errorButton;

    @Inject
    MachineAPI machineAPI;

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
        ((AnalyticsApplication)getApplication()).getAppComponent().inject(LocationActivity.this);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);

        String msg;
        if ((msg = getIntent().getStringExtra("error")) != null) {
            showErrorMessage(msg);
        } else if (!isNetworkAvailable()) {
            showNoInternetDialog();
        } else if (!getIntent().getBooleanExtra("forceMainMenu", false)) {
            String lastRoom = SharedPrefsHelper.getSharedPrefs(this)
                    .getString("lastScreenViewed", null);
            if (lastRoom != null && !lastRoom.equals("LocationList")) {
                getRoomsCall(true, lastRoom);
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
        if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() <= 0) {
            recyclerView.setAdapter(null);
        }
        if (!error) {
            getRoomsCall(false, null);
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence name = getString(R.string.notification_channel_name);
        String description = getString(R.string.notification_channel_desc);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(getString(R.string.notification_channel_name), name, importance);
        mChannel.setDescription(description);
        mChannel.enableVibration(true);
        mChannel.setSound(Uri.EMPTY, Notification.AUDIO_ATTRIBUTES_DEFAULT);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(mChannel);
    }

    protected void getLaundryCall() {

        Call<Map<String, MachineList>> allMachineCall = machineAPI.getAllMachines();
        allMachineCall.enqueue(new Callback<Map<String, MachineList>>() {
            @Override
            public void onResponse(Call<Map<String, MachineList>> call, Response<Map<String, MachineList>> response) {
                if (response.isSuccessful()) {
                    Map<String, MachineList> machineMap = response.body();
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
                    if (httpCode < 500) {
                        //client error
                        showErrorMessage(getString(R.string.error_client_message));
                        AnalyticsHelper.sendEventHit("api", "apiCodes", "/location/all", httpCode);
                    } else {
                        //server error
                        showErrorMessage(getString(R.string.error_server_message));
                        AnalyticsHelper.sendEventHit("api", "apiCodes", "/location/all", httpCode);
                    }

                }
            }

            @Override
            public void onFailure(Call<Map<String, MachineList>> call, Throwable t) {
                Log.e("LocationActivity", "API ERROR - " + t.getMessage());
                //likely a timeout -- network is available due to prev. check
                showErrorMessage(getString(R.string.error_server_message));

                AnalyticsHelper.sendErrorHit(t, false);

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    protected void getRoomsCall(final boolean goingToMachineActivity, final String lastRoom) {

        if (!isNetworkAvailable()) {
            mSwipeRefreshLayout.setRefreshing(false);
            if (error) showErrorMessage("You have no internet connection.");
            else showNoInternetDialog();
            return;
        }
        hideErrorMessage();
        if (Rooms.getRoomsConstantsInstance().getListOfRooms() == null) {
            Call<List<LocationResponse>> roomCall = machineAPI.getLocations();
            roomCall.enqueue(new Callback<List<LocationResponse>>() {
                @Override
                public void onResponse(Call<List<LocationResponse>> call, Response<List<LocationResponse>> response) {
                    if (response.isSuccessful()) {
                        //set rooms
                        List<LocationResponse> roomList = response.body();
                        String[] rooms = new String[roomList.size()];
                        for (int i = 0; i < roomList.size(); i++) {
                            rooms[i] = roomList.get(i).name;
                        }
                        Rooms.getRoomsConstantsInstance().setListOfRooms(rooms);
                        if (!goingToMachineActivity) {
                            //call laundry
                            getLaundryCall();
                        } else {
                            Intent intent = new Intent(LocationActivity.this, MachineActivity.class);
                            Bundle b = new Bundle();
                            b.putString("locationName", lastRoom);
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    } else {
                        int httpCode = response.code();
                        if (httpCode < 500) {
                            //client error
                            showErrorMessage(getString(R.string.error_client_message));
                            AnalyticsHelper.sendEventHit("api", "apiCodes", "/location/all", httpCode);
                        } else {
                            //server error
                            showErrorMessage(getString(R.string.error_server_message));
                            AnalyticsHelper.sendEventHit("api", "apiCodes", "/location/all", httpCode);
                        }

                    }
                }

                @Override
                public void onFailure(Call<List<LocationResponse>> call, Throwable t) {
                    Log.e("LocationActivity", "API ERROR - " + t.getMessage());
                    //likely a timeout -- network is available due to prev. check
                    showErrorMessage(getString(R.string.error_server_message));

                    AnalyticsHelper.sendErrorHit(t, false);

                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            if (!goingToMachineActivity) {
                getLaundryCall();
            } else {
                Intent intent = new Intent(LocationActivity.this, MachineActivity.class);
                Bundle b = new Bundle();
                b.putString("locationName", lastRoom);
                intent.putExtras(b);
                startActivity(intent);
            }
        }
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
        getRoomsCall(false, null);
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
        if (v.equals(errorButton)) {
            mLoadingProgressBar.setVisibility(View.VISIBLE);
            getRoomsCall(false, null);
        }
    }
}
