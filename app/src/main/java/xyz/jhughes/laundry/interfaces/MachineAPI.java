package xyz.jhughes.laundry.interfaces;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import xyz.jhughes.laundry.LaundryParser.Machine;

import java.util.ArrayList;

/**
 * Created by hughesjeff
 */
public interface MachineAPI {
    @GET("/laundry/{location}")
    Call<ArrayList<Machine>> getMachineStatus(
            @Path("location") String location
    );
}
