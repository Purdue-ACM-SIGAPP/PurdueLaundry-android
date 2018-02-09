package xyz.jhughes.laundry.apiclient;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Path;
import xyz.jhughes.laundry.LaundryParser.Locations;
import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.LaundryParser.MachineList;
import xyz.jhughes.laundry.LaundryParser.MachineStates;
import xyz.jhughes.laundry.LaundryParser.MachineTypes;

/**
 * Created by Slang on 4/4/2017.
 */

public class MockLaundryApiService implements MachineAPI {
    private double firstTime = 0;
    @Override
    public Call<ArrayList<Machine>> getMachineStatus(@Path("location") String location) {
        ArrayList<Machine> onMachine = new ArrayList<>();
        onMachine.addAll(Arrays.asList(new Machine("Washer1", MachineTypes.WASHER, MachineStates.AVAILABLE, null)));
        ArrayList<Machine> offMachine = new ArrayList<>();
        offMachine.addAll(Arrays.asList(new Machine("Washer1", MachineTypes.WASHER, MachineStates.IN_USE, "2 minutes left")));
        firstTime++;
        Log.d("MockLaundryAPI", "Times called: " + firstTime);
        if (firstTime < 200){
            return new MockRetrofitCall<>(onMachine);
        }

        return new MockRetrofitCall<>(offMachine);
    }

    @Override
    public Call<Map<String, MachineList>> getAllMachines() {
        Map<String, MachineList> onMachine = new HashMap<String, MachineList>();
        MachineList onList = new MachineList();
        onList.getMachines().addAll(Arrays.asList(new Machine("Washer1", MachineTypes.WASHER, MachineStates.AVAILABLE, null)));
        onMachine.put("earhart", onList);
        Map<String, MachineList> offMachine = new HashMap<String, MachineList>();
        MachineList offList = new MachineList();
        offList.getMachines().addAll(Arrays.asList(new Machine("Washer1", MachineTypes.WASHER, MachineStates.IN_USE, "2 minutes left")));
        offMachine.put("earhart", offList);
        firstTime++;
        Log.d("MockLaundryAPI", "Times called: " + firstTime);
        if (firstTime < 200){
            return new MockRetrofitCall<>(onMachine);
        }
        return new MockRetrofitCall<>(offMachine);
    }


    @Override
    public Call<List<Locations>> getLocations() {
        List<Locations> mockList = new ArrayList<Locations>();
        Locations fakeRoom = new Locations();
        fakeRoom.name = "Test Building";
        mockList.add(fakeRoom);
        return new MockRetrofitCall<>(mockList);
    }
}
