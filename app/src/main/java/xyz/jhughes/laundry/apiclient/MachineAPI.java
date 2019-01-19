package xyz.jhughes.laundry.apiclient;


import java.util.List;
import java.util.Map;

import retrofit2.Call;
import xyz.jhughes.laundry.BuildConfig;
import xyz.jhughes.laundry.LaundryParser.LocationResponse;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.LaundryParser.MachineList;

public class MachineAPI {
    public static final String API_ROOT = "http://laundry-api.sigapp.club";
    private final boolean DEBUG = BuildConfig.DEBUG;
    private MachineService machineService;

    public MachineAPI(MachineService machineService) {
        this.machineService = machineService;
    }

    public Call<List<LocationResponse>> getLocations() {
        if (DEBUG) {
            return machineService.getLocations_DEBUG();
        } else {
            return machineService.getLocations();
        }
    }

    public Call<Map<String,MachineList>> getAllMachines() {
        if (DEBUG) {
           return machineService.getAllMachines_DEBUG();
        } else {
            return machineService.getAllMachines();
        }
    }

    public Call<List<Machine>> getMachineStatus(String location) {
        if (DEBUG) {
            return machineService.getMachineStatus_DEBUG(location);
        } else {
            return machineService.getMachineStatus(location);
        }
    }
}
