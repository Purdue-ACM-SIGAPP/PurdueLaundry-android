package xyz.jhughes.laundry.views.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.Menu;
import android.view.View;

import java.util.List;

import xyz.jhughes.laundry.laundryparser.Location;
import xyz.jhughes.laundry.laundryparser.Rooms;
import javax.inject.Inject;

import xyz.jhughes.laundry.AnalyticsApplication;
import xyz.jhughes.laundry.laundryparser.LocationResponse;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.viewmodels.LocationsViewModel;
import xyz.jhughes.laundry.viewmodels.MachineViewModel;
import xyz.jhughes.laundry.viewmodels.ViewModelFactory;
import xyz.jhughes.laundry.views.adapters.LocationAdapter;
import xyz.jhughes.laundry.analytics.ScreenTrackedActivity;
import xyz.jhughes.laundry.databinding.ActivityLocationBinding;
import xyz.jhughes.laundry.data.MachineAPI;
import xyz.jhughes.laundry.views.storage.SharedPrefsHelper;

/**
 * The main activity of the app. Lists the locations of
 * laundry and an overview of the availabilities.
 */
public class LocationActivity extends ScreenTrackedActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private ActivityLocationBinding binding;

    private LocationsViewModel locationsViewModel;
    private MachineViewModel machineViewModel;
    @Inject
    MachineAPI machineAPI;

    @Inject
    ViewModelFactory viewModelFactory;

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
        binding =  DataBindingUtil.setContentView(this, R.layout.activity_location);
        locationsViewModel = ViewModelProviders.of(this, viewModelFactory).get(LocationsViewModel.class);
        machineViewModel = ViewModelProviders.of(this, viewModelFactory).get(MachineViewModel.class);

        subscribeToErrorMessage();

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

        binding.locationListPuller.setOnRefreshListener(this);
        binding.locationErrorButton.setOnClickListener(this);
    }

    private void initToolbar() {
        setSupportActionBar(binding.toolbar);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.recyclerView.setAdapter(null);
        //We only want to clear the adapter/show the loading
        // if there are no items in the list already.
        if (binding.recyclerView.getAdapter() == null || binding.recyclerView.getAdapter().getItemCount() <= 0) {
            binding.recyclerView.setAdapter(null);
        }
        if (!error) {
            getRoomsCall(false, null);
            binding.progressBar.setVisibility(View.VISIBLE);
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
        machineViewModel.getLocations().observe(this, new Observer<List<Location>>() {
            @Override
            public void onChanged(List<Location> locations) {
                adapter = new LocationAdapter(locations, LocationActivity.this.getApplicationContext());

                //We conditionally make the progress bar visible,
                // but its cheap to always dismiss it without checking
                // if its already gone.
                binding.progressBar.setVisibility(View.GONE);
                binding.recyclerView.setHasFixedSize(true);
                binding.recyclerView.setAdapter(adapter);
                binding.locationListPuller.setRefreshing(false);
            }
        });
    }


    protected void getRoomsCall(final boolean goingToMachineActivity, final String lastRoom) {

        if (!isNetworkAvailable()) {
            binding.locationListPuller.setRefreshing(false);
            if (error) showErrorMessage("You have no internet connection.");
            else showNoInternetDialog();
            return;
        }
        hideErrorMessage();
        if (Rooms.getRoomsConstantsInstance().getListOfRooms() == null) {
            locationsViewModel.getLocations().observe(this, new Observer<List<LocationResponse>>() {
                @Override
                public void onChanged(List<LocationResponse> locationResponses) {
                    if (locationResponses != null) {
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
                    }
                    binding.locationListPuller.setRefreshing(false);
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

    private void subscribeToErrorMessage() {
        this.locationsViewModel.getError().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer errorMessageResourceId) {
                String errorMessage = getString(errorMessageResourceId);
                showErrorMessage(errorMessage);
            }
        });
    }

    public void showErrorMessage(String message) {
        error = true;
        binding.locationErrorText.setText(message);
        binding.recyclerView.setAdapter(null);
        binding.progressBar.setVisibility(View.GONE);
        binding.locationListPuller.setRefreshing(false);
        binding.locationErrorText.setVisibility(View.VISIBLE);
        binding.locationErrorButton.setVisibility(View.VISIBLE);
    }

    public void hideErrorMessage() {
        error = false;
        binding.locationErrorText.setVisibility(View.GONE);
        binding.locationErrorButton.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRefresh() {
        getRoomsCall(false, null);
        binding.locationListPuller.setRefreshing(true);
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
        if (v.equals(binding.locationErrorButton)) {
            binding.progressBar.setVisibility(View.VISIBLE);
            getRoomsCall(false, null);
        }
    }
}
