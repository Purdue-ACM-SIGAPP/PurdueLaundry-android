package xyz.jhughes.laundry.laundryparser;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import xyz.jhughes.laundry.ModelOperations;

/**
 * Created by kyle on 3/31/16.
 */
public class MachineListDeserializer implements JsonDeserializer<MachineList> {
        @Override
        public MachineList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            MachineList ml = new MachineList();
            JsonArray machineArray = json.getAsJsonArray();
            Type machineType = new TypeToken<Machine>() {}.getType();
            for(JsonElement machine: machineArray){
                Machine m = context.deserialize(machine,machineType);
                ml.getMachines().add(m);
            }
            boolean isOffline = ModelOperations.machinesOffline(ml.getMachines());
            if(isOffline){
                ml.setIsOffline(true);
            } else {
                ml.setIsOffline(false);
            }
            return ml;
        }
}