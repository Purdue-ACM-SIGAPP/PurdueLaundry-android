package xyz.jhughes.laundry.apiclient;


/**
 * Singleton impl for the Machine API.
 */
public class MachineAPI {
    public static final String API_ROOT = "http://laundry-api.sigapp.club";
    private MachineService machineService;

    public MachineAPI(MachineService machineService) {
        this.machineService = machineService;
    }

}
