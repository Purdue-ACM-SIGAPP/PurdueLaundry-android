package xyz.jhughes.laundry.apiclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import xyz.jhughes.laundry.LaundryParser.MachineList;
import xyz.jhughes.laundry.LaundryParser.MachineListDeserializer;
import xyz.jhughes.laundry.analytics.AnalyticsHelper;


/**
 * Singleton impl for the Machine API.
 */
public class MachineService {
    private static String API_ROOT = "http://laundry-api.sigapp.club";
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

        OkHttpClient okClient = new OkHttpClient();
        okClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                long t1 = System.nanoTime();
                Response response = chain.proceed(request);
                long t2 = System.nanoTime();

                AnalyticsHelper.sendTimedEvent(
                        "api",
                        "requestTimeNano",
                        response.request().url().encodedPath(),
                        t2-t1);

                return response;
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .client(okClient)
                .baseUrl(API_ROOT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        REST_CLIENT = retrofit.create(MachineAPI.class);
    }
}
