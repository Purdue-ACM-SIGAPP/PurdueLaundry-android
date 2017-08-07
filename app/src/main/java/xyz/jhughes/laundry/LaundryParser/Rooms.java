package xyz.jhughes.laundry.LaundryParser;

import java.util.HashMap;

import xyz.jhughes.laundry.R;

public class Rooms {

    private static Rooms rm = new Rooms();

    private String[] LIST_OF_ROOMS = {"Cary Hall West", "Cary Hall East", "Earhart Hall", "Harrison Hall", "Hawkins Hall", "Hillenbrand Hall", "McCutcheon Hall",
            "Meredith Northwest", "Meredith Southeast", "Owen Hall", "Shreve Hall", "Tarkington Hall", "Third Street Suites", "Wiley Hall",
            "Windsor - Duhme", "Windsor - Warren"};
    private HashMap<String, String> roomsToAPILocations;
    private HashMap<String, String> APILocationToRooms;
    private HashMap<String, Integer> roomsToImage;
    private HashMap<String, Integer> machineAvailabilityColors;

    private Rooms() {
        roomsToAPILocations = new HashMap<>();
        roomsToImage = new HashMap<>();
        machineAvailabilityColors = new HashMap<>();
        APILocationToRooms = new HashMap<>();

        for (String room : LIST_OF_ROOMS) {
            roomsToAPILocations.put(room, toAPILocation(room));
            APILocationToRooms.put(toAPILocation(room), room);
            roomsToImage.put(room, toImageResourceId(room));
        }

        machineAvailabilityColors.put(MachineStates.AVAILABLE, R.color.Available);
        machineAvailabilityColors.put(MachineStates.IN_USE, R.color.InUse);
        machineAvailabilityColors.put(MachineStates.ALMOST_DONE, R.color.AlmostDone);
        machineAvailabilityColors.put(MachineStates.END_CYCLE, R.color.Finished);

    }

    public static Rooms getRoomsConstantsInstance() {
        return rm;
    }

    public String[] getListOfRooms() {
        return LIST_OF_ROOMS;
    }

    public int machineAvailabilityToColor(String availability) {
        if (machineAvailabilityColors.containsKey(availability)) {
            return machineAvailabilityColors.get(availability);
        } else {
            return R.color.InUse;
        }
    }

    public String roomToApiLocation(String roomName) {
        return roomsToAPILocations.get(roomName);
    }

    public String ApiLocationToRoom(String apiLocation) {
        return getRoom(apiLocation);
    }

    public int roomToImageResource(String roomName) {
        return roomsToImage.get(roomName);
    }

    private String toAPILocation(String room) {
        switch (room) {
            case "Cary Hall West":
                return "Cary Quad West Laundry";
            case "Cary Hall East":
                return "Cary Quad East Laundry";
            case "Earhart Hall":
                return "Earhart Laundry Room";
            case "Harrison Hall":
                return "Harrison Laundry Room";
            case "Hawkins Hall":
                return "Hawkins Laundry Room";
            case "Hillenbrand Hall":
                return "Hillenbrand Laundry Room";
            case "McCutcheon Hall":
                return "McCutcheon Laundry Room";
            case "Meredith Northwest":
                return "Meredith NW Laundry Room";
            case "Meredith Southeast":
                return "Meredith SE Laundry Room";
            case "Owen Hall":
                return "Owen Laundry Room";
            case "Shreve Hall":
                return "Shreve Laundry Room";
            case "Tarkington Hall":
                return "Tarkington Laundry Room";
            case "Third Street Suites":
                return "Third St. Suites Laundry Room";
            case "Wiley Hall":
                return "Wiley Laundry Room";
            case "Windsor - Duhme":
                return "Windsor - Duhme Laundry Room";
            case "Windsor - Warren":
                return "Windsor - Warren Laundry Room";
            default:
                break;
        }
        return null;
    }

    private int toImageResourceId(String room) {
        switch (room) {
            case "Cary Hall East":
            case "Cary Hall West":
                return R.drawable.image_cary;
            case "Earhart Hall":
                return R.drawable.image_earhart;
            case "Harrison Hall":
                return R.drawable.image_harrison;
            case "Hawkins Hall":
                return R.drawable.image_hawkins;
            case "Hillenbrand Hall":
                return R.drawable.image_hillenbrand;
            case "McCutcheon Hall":
                return R.drawable.image_mccutcheon;
            case "Meredith Northwest":
            case "Meredith Southeast":
                return R.drawable.image_meredith;
            case "Owen Hall":
                return R.drawable.image_owen;
            case "Shreve Hall":
                return R.drawable.image_shreve;
            case "Tarkington Hall":
                return R.drawable.image_tarkington;
            case "Third Street Suites":
                return R.drawable.image_tss;
            case "Wiley Hall":
                return R.drawable.image_wiley;
            case "Windsor - Duhme":
            case "Windsor - Warren":
                return R.drawable.image_windsor;
        }
        return -1;
    }

    private String getRoom(String apiLocation) {
        return APILocationToRooms.get(apiLocation);
    }
}
