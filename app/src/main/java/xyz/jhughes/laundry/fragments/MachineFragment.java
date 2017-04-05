package xyz.jhughes.laundry.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.ModelOperations;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.SnackbarPostListener;
import xyz.jhughes.laundry.activities.LocationActivity;
import xyz.jhughes.laundry.adapters.MachineAdapter;
import xyz.jhughes.laundry.analytics.AnalyticsHelper;
import xyz.jhughes.laundry.analytics.ScreenTrackedFragment;
import xyz.jhughes.laundry.apiclient.MachineService;
import xyz.jhughes.laundry.notificationhelpers.ScreenOrientationLockToggleListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class MachineFragment extends ScreenTrackedFragment implements SwipeRefreshLayout.OnRefreshListener, SnackbarPostListener, ScreenOrientationLockToggleListener {

    private ArrayList<Machine> classMachines;

    private MachineAdapter currentAdapter;

    @Bind(R.id.dryer_machines_recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.dryer_list_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.machine_fragment_too_filtered)
    TextView mTooFilteredTextView;
    @Bind(R.id.machine_fragment_notify_button)
    Button notifyButton;

    private boolean isRefreshing;
    private boolean isDryers;
    private ProgressDialog progressDialog;

    private View rootView;

    private String mRoomName;

    public MachineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this.getContext());
        {
            if (!isRefreshing) {
                progressDialog.setMessage(getString(R.string.loading_machines));
                progressDialog.show();
            }
            progressDialog.setCanceledOnTouchOutside(false);
        }

        mRoomName = getArguments().getString("roomName");
        String machineType = (getArguments().getBoolean("isDryers")) ? "Dryers" : "Washers";
        setScreenName(Constants.getApiLocation(mRoomName) + ": " + machineType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        isDryers = getArguments().getBoolean("isDryers");

        rootView = inflater.inflate(R.layout.fragment_machine, container, false);
        ButterKnife.bind(this, rootView);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        classMachines = new ArrayList<>();

        initializeNotifyOnAvaiableButton();
        refreshList();
        mSwipeRefreshLayout.setOnRefreshListener(this);

        return rootView;
    }

    @Override
    public void onRefresh() {
        isRefreshing = true;
        refreshList();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void refreshList() {
        if (isNetworkAvailable()) {
            String apiLocationFormat = Constants.getApiLocation(mRoomName);
            Call<ArrayList<Machine>> call = MachineService.getService().getMachineStatus(apiLocationFormat);

            call.enqueue(new Callback<ArrayList<Machine>>() {
                @Override
                public void onResponse(Call<ArrayList<Machine>> call, Response<ArrayList<Machine>> response) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        isRefreshing = false;
                        classMachines = response.body();

                        if (ModelOperations.machinesOffline(classMachines)) {
                            showOfflineDialogIfNecessary();
                        }

                        MachineAdapter adapter = new MachineAdapter(classMachines, rootView.getContext(), isDryers, mRoomName, MachineFragment.this, MachineFragment.this);
                        recyclerView.setAdapter(adapter);
                        currentAdapter = adapter;

                        //Check if the view is being filtered and causing the
                        // fragment to appear empty.
                        // This is not shown if the list is empty for any other reason.
                        if (currentAdapter.getCurrentMachines().isEmpty()) {
                            //Filters are too restrictive.
                            mTooFilteredTextView.setVisibility(View.VISIBLE);
                        } else {
                            mTooFilteredTextView.setVisibility(View.GONE);
                        }

                        boolean addNotifyButton = notifyButton.getVisibility() != View.VISIBLE;
                        if (addNotifyButton) {
                            for (Machine m : adapter.getCurrentMachines()) {
                                if (m.getStatus().equalsIgnoreCase("Available")) {
                                    addNotifyButton = false;
                                }
                            }
                            if (addNotifyButton) addNotifyOnAvailableButton();
                            else removeNotifyOnAvailableButton();
                        }
                        recyclerView.setAdapter(adapter);
                    } else {
                        int httpCode = response.code();
                        if (httpCode < 500) {
                            //client error
                            showErrorDialog(getString(R.string.error_client_message));
                        } else {
                            //server error
                            showErrorDialog(getString(R.string.error_server_message));
                            AnalyticsHelper.getDefaultTracker().send(
                                    new HitBuilders.ExceptionBuilder()
                                            .setDescription("Error")
                                            .set("HTTP Code", String.valueOf(httpCode))
                                            .set("Message", response.message())
                                            .setFatal(false)
                                            .build());
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Machine>> call, Throwable t) {
                    //likely a timeout -- network is available due to prev. check
                    showErrorDialog(getString(R.string.error_server_message));
                    mSwipeRefreshLayout.setRefreshing(false);
                    isRefreshing = false;
                    alertNetworkError();
                    AnalyticsHelper.getDefaultTracker().send(
                            new HitBuilders.ExceptionBuilder()
                                    .setDescription("Error")
                                    .set("HTTP Code", "-1")
                                    .set("Message", t.getMessage())
                                    .setFatal(false)
                                    .build());
                }
            });
        } else {
            showNoInternetDialog();
        }
    }

    private void showErrorDialog(final String message) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        mSwipeRefreshLayout.setRefreshing(false);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Connection Error");
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent i = new Intent(getActivity(), LocationActivity.class).putExtra("forceMainMenu", true).putExtra("error", message);
                startActivity(i);
                getActivity().finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void alertNetworkError() {
        postSnackbar("There was an issue updating the machines, please try again later.", Snackbar.LENGTH_SHORT);
    }

    private void removeNotifyOnAvailableButton() {
        notifyButton.setVisibility(View.GONE);
    }

    private void addNotifyOnAvailableButton() {
        notifyButton.setVisibility(View.VISIBLE);
    }

    private void initializeNotifyOnAvaiableButton() {
        final String text = isDryers ? getString(R.string.notify_on_dryer_available) : getString(R.string.notify_on_washer_available);
        notifyButton.setText(text);
        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Machine m = null;
                int mTime = Integer.MAX_VALUE;
                for (Machine machine : currentAdapter.getCurrentMachines()) {
                    try {
                        int machineTime = Integer.parseInt(machine.getTime().substring(0, machine.getTime().indexOf(' ')));
                        if (machineTime < mTime) {
                            m = machine;
                            mTime = machineTime;
                        }
                    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                        continue;
                    }
                }
                if (m == null) {
                    postSnackbar(getString(R.string.fragment_no_machine), Snackbar.LENGTH_LONG);
                    return;
                }
                if (m.getStatus().equals("Not online") || m.getStatus().equals("Out of order")) {
                    postSnackbar(getString(R.string.fragment_offline_location), Snackbar.LENGTH_LONG);
                    return;
                }
                currentAdapter.registerNotification(m);
            }
        });
    }

    private void showOfflineDialogIfNecessary() {
        if (!rootView.getContext().getSharedPreferences("alerts", Context.MODE_PRIVATE).getBoolean("offline_alert_thrown", false)) {
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("We cannot reach the machines at this location right now, but they may still be available to use.")
                    .setTitle("Can't reach machines").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();

            dialog.show();
            rootView.getContext().getSharedPreferences("alerts", Context.MODE_PRIVATE).edit().putBoolean("offline_alert_thrown", true).apply();
        }
    }

    private void showNoInternetDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Connection Error");
        alertDialogBuilder.setMessage("You have no internet connection");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent i = new Intent(getActivity(), LocationActivity.class).putExtra("forceMainMenu", true).putExtra("error", "You have no internet connection");
                startActivity(i);
                getActivity().finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void postSnackbar(String status, int length) {
        Snackbar snackbar = Snackbar.make(rootView, status, length);
        snackbar.show();
    }

    public void onLock(){
        int orientation = getActivity().getRequestedOrientation();
        int rotation = ((WindowManager) getActivity().getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case Surface.ROTATION_90:
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case Surface.ROTATION_180:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            default:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
        }
        getActivity().setRequestedOrientation(orientation);
    }

    public void onUnlock(){
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
