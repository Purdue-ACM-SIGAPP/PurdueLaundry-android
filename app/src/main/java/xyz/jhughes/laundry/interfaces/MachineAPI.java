package xyz.jhughes.laundry.interfaces;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import xyz.jhughes.laundry.LaundryParser.Machine;

/**
 * Created by hughesjeff
 */
public interface MachineAPI {
    @GET("/laundry/{location}")
    Call<ArrayList<Machine>> getMachineStatus(
            @Path("location") String location
    );
}
