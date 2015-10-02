package xyz.jhughes.laundry;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private String currentlySelected;
    private boolean isRefreshing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_main);

        if (!isOnline()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Connection Error");
            alertDialogBuilder.setMessage("You have no internet connection");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Okay",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    LaundryMainActivity.this.finish();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        } else {
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
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    int prevPosition = 0;

    public void setUpSpinner() {

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Constants.getListOfRooms());

        s = (Spinner) findViewById(R.id.spinner);
        s.setAdapter(adapter);

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                if (!isOnline()) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LaundryMainActivity.this);
                    alertDialogBuilder.setTitle("Connection Error");
                    alertDialogBuilder.setMessage("You have no internet connection");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            s.setSelection(prevPosition);
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    GetMachineInfoAsyncTask task = new GetMachineInfoAsyncTask();
                    try {
                        task.execute(Constants.getName((String) parent.getItemAtPosition(position)));
                        currentlySelected = Constants.getName((String) parent.getItemAtPosition(position));
                        prevPosition = position;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing!
            }
        });
    }

    @Override
    public void onRefresh() {
        isRefreshing = true;
        refreshList();
    }

    public void refreshList() {
        GetMachineInfoAsyncTask task = new GetMachineInfoAsyncTask();
        try {
            task.execute(currentlySelected);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * AsycTask designed to get information from
     */
    public class GetMachineInfoAsyncTask extends AsyncTask<String, Integer, ArrayList<Machine>> {

        private ProgressDialog progressDialog = new ProgressDialog(LaundryMainActivity.this);

        @Override
        protected void onPreExecute() {
            if (!isRefreshing) {
                progressDialog.setMessage("Loading, please wait...");
                progressDialog.show();
            }
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
            ((SwipeRefreshLayout) findViewById(R.id.swipe_layout)).setRefreshing(false);
            isRefreshing = false;
            classMachines = machines;
            lv.setAdapter(new CustomMachineAdapter(classMachines, LaundryMainActivity.this));
        }
    }
}
