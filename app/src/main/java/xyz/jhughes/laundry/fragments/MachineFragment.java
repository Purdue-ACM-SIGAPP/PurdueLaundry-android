package xyz.jhughes.laundry.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.LaundryParser.MachineStates;
import xyz.jhughes.laundry.ModelOperations;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.SnackbarPostListener;
import xyz.jhughes.laundry.adapters.MachineAdapter;
import xyz.jhughes.laundry.analytics.ScreenTrackedFragment;
import xyz.jhughes.laundry.apiclient.MachineService;

/**
 * A simple {@link Fragment} subclass.
 */
public class MachineFragment extends ScreenTrackedFragment implements SwipeRefreshLayout.OnRefreshListener, SnackbarPostListener {

    private ArrayList<Machine> classMachines;

    @Bind(R.id.dryer_machines_recycler_view) RecyclerView recyclerView;
    @Bind(R.id.dryer_list_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.machine_fragment_too_filtered)
    TextView mTooFilteredTextView;

    private boolean isRefreshing;
    private boolean isDryers;
    private ProgressDialog progressDialog;

    private View rootView;

    public static String options = "Available|In use|Almost done|End of cycle";
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
                public void onResponse(Response<ArrayList<Machine>> response, Retrofit retrofit) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    mSwipeRefreshLayout.setRefreshing(false);
                    isRefreshing = false;
                    classMachines = response.body();

                    if(ModelOperations.machinesOffline(classMachines)) {
                        showOfflineDialogIfNecessary();
                    }

                    MachineAdapter adapter = new MachineAdapter(classMachines,rootView.getContext(),isDryers,options, mRoomName, MachineFragment.this);

                    //Check if the view is being filtered and causing the
                    // fragment to appear empty.
                    // This is not shown if the list is empty for any other reason.
                    if(!options.equals(MachineStates.FILTERABLE_OPTIONS) && adapter.getItemCount() == 0) {
                        //Filters are too restrictive.
                        mTooFilteredTextView.setVisibility(View.VISIBLE);
                    } else {
                        mTooFilteredTextView.setVisibility(View.GONE);
                    }

                    recyclerView.setAdapter(adapter);

                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        } else {
            showNoInternetDialog();
        }
    }

    private void showOfflineDialogIfNecessary() {

        if(!rootView.getContext().getSharedPreferences("alerts", Context.MODE_PRIVATE).getBoolean("offline_alert_thrown", false)) {
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
                getActivity().finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void postSnackbar(String status, int length) {
        Snackbar snackbar = Snackbar.make(rootView, status, length);
        snackbar.show();
    }
}
