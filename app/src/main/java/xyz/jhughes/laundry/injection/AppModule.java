package xyz.jhughes.laundry.injection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import xyz.jhughes.laundry.LaundryParser.MachineList;
import xyz.jhughes.laundry.LaundryParser.MachineListDeserializer;
import xyz.jhughes.laundry.analytics.AnalyticsHelper;
import xyz.jhughes.laundry.apiclient.MachineService;
import xyz.jhughes.laundry.apiclient.MachineRepository;

@Module
public class AppModule {

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient() {
        return new OkHttpClient
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
    }

    @Provides
    @Singleton
    MachineService providesMachineService(OkHttpClient okHttpClient) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MachineList.class, new MachineListDeserializer())
                .create();
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(MachineRepository.API_ROOT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(MachineService.class);
    }

    @Provides
    @Singleton
    MachineRepository providesMachineRepository(MachineService machineService) {
        return new MachineRepository(machineService);
    }
}
