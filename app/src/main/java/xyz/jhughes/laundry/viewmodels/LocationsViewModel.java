package xyz.jhughes.laundry.viewmodels;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import xyz.jhughes.laundry.laundryparser.LocationResponse;

public class LocationsViewModel extends ViewModel {
    private LiveData<List<LocationResponse>> locations;

    public LiveData<List<LocationResponse>> getLocations() {
        return locations;
    }
}
