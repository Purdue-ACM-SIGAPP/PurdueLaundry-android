package xyz.jhughes.laundry;

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
import android.widget.Spinner;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.Information;
import xyz.jhughes.laundry.LaundryParser.Machine;

import java.util.ArrayList;

public class LaundryMainActivity extends AppCompatActivity {

    ArrayList<Machine> classMachines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (NullPointerException e) {
            System.out.println("Action bar was null!");
        }


        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Constants.getListOfRooms());

        Spinner s = (Spinner) findViewById(R.id.spinner);
        s.setAdapter(adapter);

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GetMachineInfoAsyncTask task = new GetMachineInfoAsyncTask();
                task.execute(Constants.getURL((String) parent.getItemAtPosition(position)));
                if (classMachines == null) {
                    //TODO this is always true. Unsure why. Will fix later. Need to study. 
                    return;
                }
                for (Machine m : classMachines) {
                    Log.d("Test code", m.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing! :D
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_laundry_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public class GetMachineInfoAsyncTask extends AsyncTask {
        @Override
        protected ArrayList<Machine> doInBackground(Object[] params) {
            Information information = new Information();
            System.out.println((String) params[0]);
            if (params[0] == null) {
                params[0] = Constants.getURL("Cary West");
            }
            ArrayList<Machine> machines = information.getInformation((String) params[0]);
            return machines;
        }

        protected void onPostExecute(ArrayList<Machine> machines) {
            super.onPostExecute(machines);
            classMachines = new ArrayList<>(machines);
        }
    }
}
