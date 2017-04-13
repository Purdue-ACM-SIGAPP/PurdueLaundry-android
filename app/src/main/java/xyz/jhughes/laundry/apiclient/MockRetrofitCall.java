package xyz.jhughes.laundry.apiclient;


import java.io.IOException;


import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by dmtsc on 4/4/2017.
 */

public class MockRetrofitCall<T> implements Call<T> {
    private T mObject;

    public MockRetrofitCall(T object) {
        this.mObject = object;
    }

    @Override
    public Response<T> execute() throws IOException {
        return null;
    }

    @Override
    public void enqueue(Callback<T> callback) {
        callback.onResponse(this, Response.success(mObject));
    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public Call clone() {
        return null;
    }

    @Override
    public Request request() {
        return null;
    }
}