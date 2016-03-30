package xyz.jhughes.laundry;

import java.util.List;

import xyz.jhughes.laundry.LaundryParser.Machine;

public class ModelOperations {
    public static boolean machinesOffline(List<Machine> machines) {
        for (Machine m : machines) {
            if (!(m.getStatus().equals("Not online"))) {
                return false;
            }
        }
        return true;
    }
}