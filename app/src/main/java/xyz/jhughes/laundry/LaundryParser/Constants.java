package xyz.jhughes.laundry.LaundryParser;

/*
 * Created by tylorgarrett on 8/24/15.
 * Heavily modified by hughesjeff
 */
public class Constants {

    static Rooms roomsConstants = Rooms.getRoomsConstantsInstance();

    public static String[] getListOfRooms() {
        return roomsConstants.getListOfRooms();
    }

    public static String getName(String room) {
        return roomsConstants.roomToApiLocation(room);
    }

    public static int getLocationImageResource(String room) {
        return roomsConstants.roomToImageResource(room);
    }

    public static int getMachineAvailabilityColor(String availability) {
        return roomsConstants.machineAvailabilityToColor(availability);
    }

}
