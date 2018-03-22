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
    private static final String API_ROOT = "http://laundry-api.sigapp.club";
    private static MachineAPI REST_CLIENT;

    static {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MachineList.class, new MachineListDeserializer())
                .create();

        OkHttpClient okClient =
                new OkHttpClient
                        .Builder()
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = chain.request();

                                long t1 = System.currentTimeMillis();
                                Response response = chain.proceed(request);
                                long t2 = System.currentTimeMillis();

                                AnalyticsHelper.sendTimedEvent(
                                        "api",
                                        "requestTimeMillis",
                                        response.request().url().encodedPath(),
                                        t2 - t1);

                                return response;
                            }
                        })
                        .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okClient)
                .baseUrl(API_ROOT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        REST_CLIENT = retrofit.create(MachineAPI.class);
    }

    public static MachineAPI getService() {
        return REST_CLIENT;
    }

}
