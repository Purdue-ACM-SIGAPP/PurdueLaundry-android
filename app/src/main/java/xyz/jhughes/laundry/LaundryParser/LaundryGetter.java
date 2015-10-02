package xyz.jhughes.laundry.LaundryParser;

import retrofit.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 10/1/15.
 */
public class LaundryGetter {

    public LaundryGetter() {
        Retrofit retroFit = new Retrofit.Builder().baseUrl("http://api.tylorgarrett.com/").build();
        Machines machines = retroFit.create(Machines.class);
        Call<List<Machine>> call = machines.machines("Hillenbrand");
        List<Machine> machineArrayList = null;
        try {
            machineArrayList = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (machineArrayList == null) {
            return;
        }

        for (Machine m : machineArrayList) {
            m.printDetails();
        }
    }
}
