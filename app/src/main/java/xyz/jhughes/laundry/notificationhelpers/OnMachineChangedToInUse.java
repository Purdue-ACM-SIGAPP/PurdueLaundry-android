package xyz.jhughes.laundry.notificationhelpers;

import xyz.jhughes.laundry.LaundryParser.Machine;

/**
 * Created by Slang on 3/2/2017.
 */

public interface OnMachineChangedToInUse {
    void onMachineInUse(Machine m);
    void onTimeout();
}
