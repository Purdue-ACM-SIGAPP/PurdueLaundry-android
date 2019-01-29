package xyz.jhughes.laundry.laundryparser;


import java.util.List;

public class Location {

    private String locationName;
    private MachineList machineList;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public MachineList getMachineList() {
        return machineList;
    }

    public void setMachineList(MachineList machineList) {
        this.machineList = machineList;
    }

    public Location(String locationName, MachineList machineList){
        this.locationName = locationName;
        this.machineList = machineList;
    }

    public int getTotalWasherCount() {
        return getTotalMachineCount(MachineTypes.WASHER);
    }

    public int getAvailableWasherCount() {
        return getAvailableMachineCount(MachineTypes.WASHER);
    }

    public int getTotalDryerCount() {
        return getTotalMachineCount(MachineTypes.DRYER);
    }

    public int getAvailableDryerCount() {
        return getAvailableMachineCount(MachineTypes.DRYER);
    }

    private int getTotalMachineCount(String machineType) {
        int dryers = 0;
        for (Machine machine: this.machineList.getMachines()) {
            if (machine.getType().equals(machineType)) {
                dryers++;
            }
        }
        return dryers;
    }

    private int getAvailableMachineCount(String machineType) {
        int availableDryers = 0;
        for (Machine machine: this.machineList.getMachines()) {
            if (machine.getType().equals(machineType) && machine.getStatus().equals(MachineStates.AVAILABLE)) {
                availableDryers++;
            }
        }
        return availableDryers;
    }
}
