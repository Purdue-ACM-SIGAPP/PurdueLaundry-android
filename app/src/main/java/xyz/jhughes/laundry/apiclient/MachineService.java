package xyz.jhughes.laundry.apiclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import xyz.jhughes.laundry.LaundryParser.MachineList;
import xyz.jhughes.laundry.LaundryParser.MachineListDeserializer;


/**
 * Singleton impl for the Machine API.
 */
public class MachineService {
    private static String API_ROOT = "http://laundry-api.sigapp.club";
    private static MachineAPI REST_CLIENT;
    private static MockLaundryApiService MOCK_CLIENT;

    static {
        setupRestClient();
    }

    public static MachineAPI getService() {
        /*if (BuildConfig.DEBUG){
            return getMockService();
        }*/
        return REST_CLIENT;
    }

    public static MachineAPI getMockService(){
        if (MOCK_CLIENT == null){
            MOCK_CLIENT = new MockLaundryApiService();
        }
        return MOCK_CLIENT;
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
