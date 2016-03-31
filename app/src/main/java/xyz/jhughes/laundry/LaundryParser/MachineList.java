package xyz.jhughes.laundry.LaundryParser;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;



import xyz.jhughes.laundry.ModelOperations;

/**
 * Created by kyle on 3/31/16.
 */
public class MachineList {
    private List<Machine> machines;
    private boolean isOffline;

    public boolean isOffline() {
        return isOffline;
    }

    public void setIsOffline(boolean isOffline) {
        this.isOffline = isOffline;
    }


    public List<Machine> getMachines() {
        return machines;
    }

    public void setMachines(List<Machine> machines) {
        this.machines = machines;
    }


    public MachineList() {
        this.machines = new ArrayList<>();
    }
}
