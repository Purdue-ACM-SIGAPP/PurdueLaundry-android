package xyz.jhughes.laundry.apiclient;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.LaundryParser.MachineList;

/**
 * Retrofit interface for the Machine API.
 */
public interface MachineAPI {
    @GET("/v2/location/{location}")
    Call<ArrayList<Machine>> getMachineStatus(
            @Path("location") String location
    );

    @GET("/v2/location/all")
    Call<Map<String,MachineList>>getAllMachines();
}
