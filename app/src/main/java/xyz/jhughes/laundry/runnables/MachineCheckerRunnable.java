package xyz.jhughes.laundry.runnables;

import android.os.Handler;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.jhughes.laundry.BuildConfig;
import xyz.jhughes.laundry.LaundryParser.Constants;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.LaundryParser.MachineStates;
import xyz.jhughes.laundry.analytics.AnalyticsHelper;
import xyz.jhughes.laundry.notificationhelpers.OnMachineChangedToInUse;
import xyz.jhughes.laundry.apiclient.MachineService;

/**
 * Created by Slang on 3/2/2017.
 */
public class MachineCheckerRunnable implements Runnable {


    private final Machine m;
    private final String roomName;
    private final OnMachineChangedToInUse listener;
    private Handler handler;
    private int timeout = 5;
    public static final int TIME = 60000; //How long between server pings
                                    //note that the first post delayed time is pulled from here

    public MachineCheckerRunnable(Machine m, String roomName, Handler handler, OnMachineChangedToInUse listener){
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
            public void onResponse(Call<ArrayList<Machine>> call, Response<ArrayList<Machine>> response) {
                ArrayList<Machine> body = response.body();
                if (body.contains(m)){
                    Machine m2 = body.get(body.indexOf(m));
                    if (m2.getStatus().equals(MachineStates.IN_USE)){
                        AnalyticsHelper.sendEventHit("Automatic Timer", "Click", "Timer created", 6-timeout);
                        listener.onMachineInUse(m2);
                    } else {
                        timeout--;
                        if (timeout > 0) {
                            handler.postDelayed(MachineCheckerRunnable.this, TIME);
                        } else {
                            listener.onTimeout();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Machine>> call, Throwable t) {
                //continue trying with next ping in background
                timeout--;
                if (timeout > 0) {
                    handler.postDelayed(MachineCheckerRunnable.this, TIME);
                } else {
                    listener.onTimeout();
                }
                AnalyticsHelper.sendErrorHit(t, false);
            }

        });
    }
}
