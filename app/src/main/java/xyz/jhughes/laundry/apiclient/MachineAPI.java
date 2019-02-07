package xyz.jhughes.laundry.apiclient;


import java.util.List;
import java.util.Map;

import retrofit2.Call;
import xyz.jhughes.laundry.laundryparser.Machine;
import xyz.jhughes.laundry.laundryparser.MachineList;
import xyz.jhughes.laundry.BuildConfig;
import xyz.jhughes.laundry.laundryparser.LocationResponse;

public class MachineAPI {
    public static final String API_ROOT = "http://laundry-api.sigapp.club";
    private final boolean DEBUG = BuildConfig.DEBUG;
    private MachineService machineService;

    public MachineAPI(MachineService machineService) {
        this.machineService = machineService;
    }

    public Call<List<LocationResponse>> getLocations() {
        return machineService.getLocations();
    }

    public Call<Map<String, MachineList>> getAllMachines() {
        return machineService.getAllMachines();
    }

    public Call<List<Machine>> getMachineStatus(String location) {
        return machineService.getMachineStatus(location);
    }
}
