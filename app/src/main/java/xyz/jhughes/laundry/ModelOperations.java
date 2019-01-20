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

    public static Integer[] getAvailableCounts(List<Machine> machines){
        final Integer[] countArray = new Integer[4];
        for (int i = 0; i < 4; i++) {
            countArray[i] = 0;
        }
        for (Machine machine : machines) {
            if (machine.getType().equals(MachineTypes.DRYER)) {
                //Increments Total Dryer Count For Specific Place
                countArray[0] = countArray[0] + 1;
                if (machine.getStatus().equals(MachineStates.AVAILABLE)) {
                    //Increments Available Dryer Count For Specific Place
                    countArray[1] = countArray[1] + 1;
                }
            } else {
                //Increments Total Washer Count For Specific Place
                countArray[2] = countArray[2] + 1;
                if (machine.getStatus().equals(MachineStates.AVAILABLE)) {
                    //Increments Available Washer Count For Specific Place
                    countArray[3] = countArray[3] + 1;
                }
            }
        }
        return countArray;
    }


}