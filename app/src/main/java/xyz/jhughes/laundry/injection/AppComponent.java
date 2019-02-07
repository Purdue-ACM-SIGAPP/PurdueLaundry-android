package xyz.jhughes.laundry.injection;

import javax.inject.Singleton;

import dagger.Component;
import xyz.jhughes.laundry.data.MachineService;
import xyz.jhughes.laundry.viewmodels.ViewModelFactory;
import xyz.jhughes.laundry.views.activities.LocationActivity;
import xyz.jhughes.laundry.views.activities.MachineActivity;
import xyz.jhughes.laundry.views.fragments.MachineFragment;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(LocationActivity locationActivity);
    void inject(MachineActivity machineActivity);
    void inject(MachineFragment machineFragment);
    void inject(MachineService machineService);
    void inject(ViewModelFactory viewModelFactory);
}
