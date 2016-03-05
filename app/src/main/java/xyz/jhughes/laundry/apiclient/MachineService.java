package xyz.jhughes.laundry.apiclient;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import xyz.jhughes.laundry.apiclient.MachineAPI;


/**
 * Singleton impl for the Machine API.
 */
public class MachineService {
    private static String API_ROOT = "http://ec2-52-37-183-17.us-west-2.compute.amazonaws.com";
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
