package xyz.jhughes.laundry.viewmodels;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import xyz.jhughes.laundry.data.MachineRepository;

@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory {

    private MachineRepository machineRepository;

    @Inject
    public ViewModelFactory(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LocationsViewModel.class)) {
            return (T) new LocationsViewModel(machineRepository);
        }
        return null;
    }
}
