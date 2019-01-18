package xyz.jhughes.laundry.apiclient;


/**
 * Singleton impl for the Machine API.
 */
public class MachineRepository {
    public static final String API_ROOT = "http://laundry-api.sigapp.club";
    private MachineService machineService;

    public MachineRepository(MachineService machineService) {
        this.machineService = machineService;
    }

    public MachineService getService() {
        return machineService;
    }

}
