package xyz.jhughes.laundry;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.LaundryGetter;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.ListViewAdapter.CustomMachineAdapter;

import java.util.ArrayList;

/**
 * Created by hughesjeff
 */

public class LaundryMainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<Machine> classMachines;
    private ListView lv;
    private Spinner s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_main);

        lv = (ListView) findViewById(R.id.laundry_list);

        classMachines = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpSpinner();

        GetMachineInfoAsyncTask task = new GetMachineInfoAsyncTask();
        try {
            task.execute(Constants.getName(null));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((SwipeRefreshLayout) findViewById(R.id.swipe_layout)).setOnRefreshListener(this);
    }

    public void setUpSpinner() {

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Constants.getListOfRooms());

        s = (Spinner) findViewById(R.id.spinner);
        s.setAdapter(adapter);

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GetMachineInfoAsyncTask task = new GetMachineInfoAsyncTask();
                try {
                    task.execute(Constants.getName((String) parent.getItemAtPosition(position)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("Testing");
            }
        });
    }

    @Override
    public void onRefresh() {
        refreshList();
        ((SwipeRefreshLayout) findViewById(R.id.swipe_layout)).setRefreshing(false);
    }

    public void refreshList() {
        System.out.println("NYI");
        Toast.makeText(this, "Not Yet Implemented", Toast.LENGTH_LONG).show();
    }

    /**
     * AsycTask designed to get information from
     */
    public class GetMachineInfoAsyncTask extends AsyncTask<String, Integer, ArrayList<Machine>> {

        private ProgressDialog progressDialog = new ProgressDialog(LaundryMainActivity.this);

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading, please wait...");
            progressDialog.show();
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
            classMachines = machines;
            lv.setAdapter(new CustomMachineAdapter(classMachines, LaundryMainActivity.this));
        }
    }
}
