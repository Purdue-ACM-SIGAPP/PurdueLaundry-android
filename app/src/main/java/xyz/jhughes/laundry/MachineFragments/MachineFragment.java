package xyz.jhughes.laundry.MachineFragments;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.LaundryGetter;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.ListViewAdapter.CustomMachineAdapter;
import xyz.jhughes.laundry.MainActivity;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.adapters.MachineAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MachineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<Machine> classMachines;
    private ListView lv;
    private RecyclerView recyclerView;
    private Spinner s;
    private String currentlySelected;
    private boolean isRefreshing;
    private boolean isDryers;

    private View rootView;

    public MachineFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //getFragmentManager().beginTransaction().remove(this).add(R.id.dryer_fragment, this).commit();

        isDryers = getArguments().getBoolean("isDryers");

        rootView = inflater.inflate(R.layout.fragment_dryer, container, false);

        //lv = (ListView) rootView.findViewById(R.id.dryer_list);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.dryer_machines_recycler_view);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(),2);
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

    public void refreshList() {
        GetMachineInfoAsyncTask task = new GetMachineInfoAsyncTask();
        try {
            task.execute(Constants.getName(MainActivity.getSelected()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class GetMachineInfoAsyncTask extends AsyncTask<String, Integer, ArrayList<Machine>> {

        private ProgressDialog progressDialog = new ProgressDialog(rootView.getContext());

        @Override
        protected void onPreExecute() {
            if (!isRefreshing) {
                progressDialog.setMessage("Loading, please wait...");
                progressDialog.show();
            }
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected ArrayList<Machine> doInBackground(String[] params) {
            if (params[0] == null) {
                params[0] = Constants.getName("Cary West");
            }
            LaundryGetter laundryGetter = new LaundryGetter(params[0]);
            return laundryGetter.getMachines();
        }

        @Override
        protected void onPostExecute(ArrayList<Machine> machines) {
            super.onPostExecute(machines);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            ((SwipeRefreshLayout) rootView.findViewById(R.id.dryer_list_layout)).setRefreshing(false);
            isRefreshing = false;
            classMachines = machines;
            //lv.setAdapter(new CustomMachineAdapter(classMachines, rootView.getContext(), true));
            recyclerView.setAdapter(new MachineAdapter(classMachines, rootView.getContext(),isDryers));
        }
    }

}
