package xyz.jhughes.laundry.apiclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import xyz.jhughes.laundry.LaundryParser.MachineList;
import xyz.jhughes.laundry.LaundryParser.MachineListDeserializer;


/**
 * Singleton impl for the Machine API.
 */
public class MachineService {
    private static String API_ROOT = "http://ec2-52-27-152-61.us-west-2.compute.amazonaws.com";
    private static MachineAPI REST_CLIENT;

    static {
        setupRestClient();
    }

    public static MachineAPI getService() {
        return REST_CLIENT;
    }

    private static void setupRestClient() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MachineList.class, new MachineListDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_ROOT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        REST_CLIENT = retrofit.create(MachineAPI.class);
    }
}
