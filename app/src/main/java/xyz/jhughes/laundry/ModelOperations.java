package xyz.jhughes.laundry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import xyz.jhughes.laundry.laundryparser.Location;
import xyz.jhughes.laundry.laundryparser.Machine;
import xyz.jhughes.laundry.laundryparser.MachineList;
import xyz.jhughes.laundry.laundryparser.MachineStates;
import xyz.jhughes.laundry.laundryparser.MachineTypes;

public class ModelOperations {
    public static boolean machinesOffline(List<Machine> machines) {
        for (Machine m : machines) {
            if (!(m.getStatus().equals("Not online"))) {
                return false;
            }
        }
        return true;
    }

    public static List<Location> machineMapToLocationList(Map<String, MachineList> machineListMap) {
        List<Location> locations = new ArrayList<>();
        for(String key: machineListMap.keySet()){
            MachineList ml = machineListMap.get(key);
            Location location = new Location(key,ml);
            if(ml.isOffline()){
                locations.add(location);
            } else {
                locations.add(0,location);
            }
        }

        Collections.sort(locations, new Comparator<Location>() {
            @Override
            public int compare(Location l1, Location l2) {
                if(!l1.getMachineList().isOffline() && l2.getMachineList().isOffline()){
                    return -1;
                } else if(l1.getMachineList().isOffline() && !l2.getMachineList().isOffline()){
                    return 1;
                }
                else{
                    return l1.getLocationName().compareTo(l2.getLocationName());
                }
            }
        });
        return locations;
    }
}