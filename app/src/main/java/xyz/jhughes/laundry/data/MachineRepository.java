package xyz.jhughes.laundry.data;

import android.util.Log;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.jhughes.laundry.R;
import xyz.jhughes.laundry.analytics.AnalyticsHelper;
import xyz.jhughes.laundry.laundryparser.LocationResponse;
import xyz.jhughes.laundry.laundryparser.Rooms;

public class MachineRepository {
    private MachineService machineService;
    private MutableLiveData<Integer> error = new MutableLiveData<>();

    public MachineRepository(MachineService machineService) {
        this.machineService = machineService;
    }

    public LiveData<List<LocationResponse>> getLocations() {
        final MutableLiveData<List<LocationResponse>> locations = new MutableLiveData<>();

        Call<List<LocationResponse>> roomCall = machineService.getLocations();
        roomCall.enqueue(new Callback<List<LocationResponse>>() {
            @Override
            public void onResponse(Call<List<LocationResponse>> call, Response<List<LocationResponse>> response) {
                if (response.isSuccessful()) {
                    //set rooms
                    List<LocationResponse> locationResponses = response.body();
                    String[] rooms = new String[locationResponses.size()];
                    for (int i = 0; i < locationResponses.size(); i++) {
                        rooms[i] = locationResponses.get(i).name;
                    }
                    Rooms.getRoomsConstantsInstance().setListOfRooms(rooms);
                    locations.setValue(locationResponses);
                } else {
                    int httpCode = response.code();
                    if (httpCode < 500) {
                        //client error
                        error.setValue(R.string.error_client_message);
                        AnalyticsHelper.sendEventHit("api", "apiCodes", "/location/all", httpCode);
                    } else {
                        //server error
                        error.setValue(R.string.error_server_message);
                        AnalyticsHelper.sendEventHit("api", "apiCodes", "/location/all", httpCode);
                    }

                }
            }

            @Override
            public void onFailure(Call<List<LocationResponse>> call, Throwable t) {
                Log.e("LocationActivity", "API ERROR - " + t.getMessage());
                //likely a timeout -- network is available due to prev. check
                error.setValue(R.string.error_server_message);
                AnalyticsHelper.sendErrorHit(t, false);
            }
        });
        return locations;
    }

    public LiveData<Integer> getError() {
        return error;
    }
}
