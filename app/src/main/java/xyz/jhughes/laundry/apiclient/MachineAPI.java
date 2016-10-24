package xyz.jhughes.laundry.apiclient;

import java.util.ArrayList;
import java.util.Map;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.LaundryParser.MachineList;

/**
 * Retrofit interface for the Machine API.
 */
public interface MachineAPI {
    @GET("/laundry/{location}")
    Call<ArrayList<Machine>> getMachineStatus(
            @Path("location") String location
    );

    @GET("/laundry/location/all")
    Call<Map<String,MachineList>>getAllMachines();
}
