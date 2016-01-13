package xyz.jhughes.laundry;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import xyz.jhughes.laundry.interfaces.MachineAPI;


/**
 * Created by kyle on 10/6/15.
 */
public class MachineService {
    private static String API_ROOT = "http://api.tylorgarrett.com";
    private static MachineAPI REST_CLIENT;

    static {
        setupRestClient();
    }

    public static MachineAPI getService() {
        return REST_CLIENT;

    }

    private static void setupRestClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_ROOT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        REST_CLIENT = retrofit.create(MachineAPI.class);
    }
}
