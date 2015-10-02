package xyz.jhughes.laundry.LaundryParser;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

import java.util.ArrayList;

/**
 * Created by hughesjeff
 */
public interface MachineRetrofitInterface {
    @GET("/Laundry/{location}")
    Call<ArrayList<Machine>> machines(
            @Path("location") String location
    );
}
