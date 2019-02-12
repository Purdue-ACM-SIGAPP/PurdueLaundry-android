package xyz.jhughes.laundry.viewmodels;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import xyz.jhughes.laundry.data.MachineRepository;
import xyz.jhughes.laundry.laundryparser.Location;

public class MachineViewModel extends ViewModel {
    private MachineRepository machineRepository;

    public MachineViewModel(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    public LiveData<List<Location>> getMachines() {
        return machineRepository.getMachines();
    }

    public LiveData<Integer> getError() {
        return machineRepository.getError();
    }
}
