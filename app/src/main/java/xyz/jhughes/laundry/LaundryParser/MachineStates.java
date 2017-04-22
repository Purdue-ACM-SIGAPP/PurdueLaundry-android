package xyz.jhughes.laundry.LaundryParser;

/**
 * Created by dmtsc on 10/18/2016.
 */

public class MachineStates {
    public static final String IN_USE = "In use";
    public static final String AVAILABLE = "Available";
    public static final String ALMOST_DONE = "Almost done";
    public static final String END_CYCLE = "End of cycle";
    public static final String READY = "Ready to start";
    public static final String NOT_ONLINE = "not online";
    public static final String OUT_OF_ORDER = "Out of order";

    public static final String SEPARATOR = "|";

    //"Available|In use|Almost done|End of cycle"
    public static final String FILTERABLE_OPTIONS = AVAILABLE + SEPARATOR +
            IN_USE + SEPARATOR +
            ALMOST_DONE + SEPARATOR +
            END_CYCLE;

    /*public static final String[] STILL_ALLOWED = new String[] {
            AVAILABLE, IN_USE, ALMOST_DONE, END_CYCLE
    };*/

}
