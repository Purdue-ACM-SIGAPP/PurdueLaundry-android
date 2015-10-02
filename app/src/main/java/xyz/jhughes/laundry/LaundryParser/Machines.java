package xyz.jhughes.laundry.LaundryParser;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

import java.util.List;

/**
 * Created by jeff on 10/1/15.
 */
public interface Machines {
    @GET("/Laundry/{location}")
    Call<List<Machine>> machines(
            @Path("location") String location
    );
}
