package xyz.jhughes.laundry.viewmodels;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import xyz.jhughes.laundry.data.MachineRepository;
import xyz.jhughes.laundry.laundryparser.LocationResponse;

public class LocationsViewModel extends ViewModel {
    private MachineRepository machineRepository;

    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LocationsViewModel(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    public LiveData<List<LocationResponse>> getLocations() {
        return machineRepository.getLocations();
    }

    public LiveData<Boolean> getLoadingStatus() {
        return isLoading;
    }

    public LiveData<Integer> getError() {
        return machineRepository.getError();
    }
}
