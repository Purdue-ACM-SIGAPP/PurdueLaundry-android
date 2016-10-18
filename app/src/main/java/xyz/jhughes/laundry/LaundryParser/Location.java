package xyz.jhughes.laundry.LaundryParser;


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
}
