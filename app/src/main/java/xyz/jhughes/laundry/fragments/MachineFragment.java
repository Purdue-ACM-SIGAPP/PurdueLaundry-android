package xyz.jhughes.laundry.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.MachineService;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.activities.MachineActivity;
import xyz.jhughes.laundry.adapters.MachineAdapter;
import xyz.jhughes.laundry.analytics.AnalyticsHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class MachineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<Machine> classMachines;
    private RecyclerView recyclerView;
    private boolean isRefreshing;
    private boolean isDryers;
    private String selected;
    private ProgressDialog progressDialog;

    private View rootView;

    public static String options = "Available|In use|Almost done|End of cycle";

    public MachineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this.getContext());
        {
            if (!isRefreshing) {
                progressDialog.setMessage("Loading, please wait...");
                progressDialog.show();
            }
            progressDialog.setCanceledOnTouchOutside(false);
        }

        try {
            // Get tracker.
            Tracker mTracker = AnalyticsHelper.getDefaultTracker();
            mTracker.setScreenName(MachineActivity.getSelected());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        } catch(Exception e) {
            Log.e("AnalyticsException", e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        isDryers = getArguments().getBoolean("isDryers");
        selected = Constants.getName(MachineActivity.getSelected());

        rootView = inflater.inflate(R.layout.fragment_dryer, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.dryer_machines_recycler_view);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        classMachines = new ArrayList<>();

        refreshList();

        ((SwipeRefreshLayout) rootView.findViewById(R.id.dryer_list_layout)).setOnRefreshListener(this);

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
            Call<ArrayList<Machine>> call = MachineService.getService().getMachineStatus(selected);

            call.enqueue(new Callback<ArrayList<Machine>>() {
                @Override
                public void onResponse(Response<ArrayList<Machine>> response, Retrofit retrofit) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    ((SwipeRefreshLayout) rootView.findViewById(R.id.dryer_list_layout)).setRefreshing(false);
                    isRefreshing = false;
                    classMachines = response.body();
                    recyclerView.setAdapter(new MachineAdapter(classMachines, rootView.getContext(), isDryers, options));
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        } else {
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
    }
}
