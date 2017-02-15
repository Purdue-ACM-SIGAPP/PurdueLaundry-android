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
    @GET("/v1/location/{location}")
    Call<ArrayList<Machine>> getMachineStatus(
            @Path("location") String location
    );

    @GET("/v1/location/all")
    Call<Map<String,MachineList>>getAllMachines();
}
