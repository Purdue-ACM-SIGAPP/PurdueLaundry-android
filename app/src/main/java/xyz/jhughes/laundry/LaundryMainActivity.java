package xyz.jhughes.laundry;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.Information;
import xyz.jhughes.laundry.LaundryParser.LaundryGetter;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.ListViewAdapter.CustomMachineAdapter;

import java.util.ArrayList;

public class LaundryMainActivity extends AppCompatActivity {

    private ArrayList<Machine> classMachines;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_main);

        lv = (ListView) findViewById(R.id.laundry_list);

        classMachines = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Constants.getListOfRooms());

        Spinner s = (Spinner) findViewById(R.id.spinner);
        s.setAdapter(adapter);

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ProgressDialog progressDialog = ProgressDialog.show(LaundryMainActivity.this, "",
                        "Loading, please wait...", true);
                GetMachineInfoAsyncTask task = new GetMachineInfoAsyncTask();
                try {
                    task.execute(Constants.getURL((String) parent.getItemAtPosition(position)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (classMachines == null) {
                    return;
                }
                lv.setAdapter(new CustomMachineAdapter(classMachines, LaundryMainActivity.this));
                progressDialog.cancel();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("Testing");
            }
        });

        LaundryGetter laundryGetter = new LaundryGetter();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_laundry_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //TODO add refresh button

        return super.onOptionsItemSelected(item);
    }

    /**
     * AsycTask designed to get information from
     */
    public class GetMachineInfoAsyncTask extends AsyncTask<String, Integer, ArrayList<Machine>> {

        @Override
        protected ArrayList<Machine> doInBackground(String[] params) {
            Information information = new Information();
            if (params[0] == null) {
                params[0] = Constants.getURL("Cary West");
            }
            return information.getInformation(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Machine> machines) {
            super.onPostExecute(machines);
            classMachines = machines;
        }
    }
}
