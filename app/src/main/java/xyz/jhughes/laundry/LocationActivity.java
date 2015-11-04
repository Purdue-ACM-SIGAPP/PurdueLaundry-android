package xyz.jhughes.laundry;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.jhughes.laundry.Adapters.LocationAdapter;
import xyz.jhughes.laundry.Adapters.MachineAdapter;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.Machine;

/**
 * Created by vieck on 10/27/15.
 */
public class LocationActivity extends AppCompatActivity {

    private Context mContext;

    private RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;

    private LocationAdapter adapter;

    private boolean isDryers;

    private String selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        mContext = this;

        for (String name : Constants.getListOfRooms()) {
            Call<ArrayList<Machine>> call = MachineService.getService().getMachineStatus("hawkins");

            call.enqueue(new Callback<ArrayList<Machine>>() {
                @Override
                public void onResponse(Response<ArrayList<Machine>> response, Retrofit retrofit) {

                    ArrayList<Machine> classMachines = response.body();
                    adapter = new LocationAdapter(classMachines, mContext);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("LocationActivity", t.getMessage());
                }
            });

        }

    }


}
