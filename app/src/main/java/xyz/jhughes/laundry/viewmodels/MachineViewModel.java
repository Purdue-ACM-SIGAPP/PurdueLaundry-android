package xyz.jhughes.laundry.viewmodels;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import xyz.jhughes.laundry.data.MachineRepository;
import xyz.jhughes.laundry.laundryparser.Location;
import xyz.jhughes.laundry.laundryparser.Machine;

public class MachineViewModel extends ViewModel {
    private MachineRepository machineRepository;

    public MachineViewModel(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    public LiveData<List<Location>> getLocations() {
        return machineRepository.getMachinesUnderLocation();
    }

    public LiveData<List<Machine>> getMachines(String location) {
        return machineRepository.getMachines(location);
    }

    public LiveData<Integer> getError() {
        return machineRepository.getError();
    }
}
