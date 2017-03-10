package xyz.jhughes.laundry.runnables;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.LaundryParser.MachineStates;
import xyz.jhughes.laundry.notificationhelpers.OnMachineInUse;
import xyz.jhughes.laundry.apiclient.MachineService;

/**
 * Created by Slang on 3/2/2017.
 */
public class MachineCheckerRunnable implements Runnable {


    private final Machine m;
    private final String roomName;
    private final OnMachineInUse listener;
    private Handler handler;
    private int timeout = 5;
    private final int TIME = 60000; //How long between server pings
                                    //note that the first post delayed time is determined in the machine adapter class

    public MachineCheckerRunnable(Machine m, String roomName, Handler handler, OnMachineInUse listener){
        this.listener = listener;
        this.m = m;
        this.roomName = roomName;
        this.handler = handler;
    }

    @Override
    public void run() {
        String apiLocationFormat = Constants.getApiLocation(this.roomName);
        Call<ArrayList<Machine>> call = MachineService.getService().getMachineStatus(apiLocationFormat);

        call.enqueue(new Callback<ArrayList<Machine>>() {
            @Override
            public void onResponse(Response<ArrayList<Machine>> response, Retrofit retrofit) {
                ArrayList<Machine> body = response.body();
                if (body.contains(m)){
                    Machine m2 = body.get(body.indexOf(m));
                    if (m2.getStatus().equals(MachineStates.IN_USE)){ //
                        listener.onMachineInUse(m2);
                    } else {
                        timeout--;
                        if (timeout != 0) {
                            handler.postDelayed(MachineCheckerRunnable.this, TIME);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
