package xyz.jhughes.laundry.LaundryParser;

import java.util.HashMap;

import xyz.jhughes.laundry.R;

public class Rooms {

    private static Rooms rm = new Rooms();

    private String[] LIST_OF_ROOMS = {
            "Cary Quad West Laundry", "Cary Quad East Laundry", "Earhart Laundry Room", "Harrison Laundry Room", "Hawkins Laundry Room",
            "Hillenbrand Laundry Room", "McCutcheon Laundry Room", "Meredith NW Laundry Room", "Meredith SE Laundry Room", "Owen Laundry Room",
            "Shreve Laundry Room", "Tarkington Laundry Room", "Third St. Suites Laundry Room", "Wiley Laundry Room", "Windsor - Duhme Laundry Room",
            "Windsor - Warren Laundry Room"
    };
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
        return room;
    }

    private int toImageResourceId(String room) {
        switch (room.split(" ")[0].toLowerCase()) {
            case "cary":
                return R.drawable.image_cary;
            case "earhart":
                return R.drawable.image_earhart;
            case "harrison":
                return R.drawable.image_harrison;
            case "hawkins":
                return R.drawable.image_hawkins;
            case "hillenbrand":
                return R.drawable.image_hillenbrand;
            case "mccutcheon":
                return R.drawable.image_mccutcheon;
            case "meredith":
                return R.drawable.image_meredith;
            case "owen":
                return R.drawable.image_owen;
            case "shreve":
                return R.drawable.image_shreve;
            case "tarkington":
                return R.drawable.image_tarkington;
            case "third":
                return R.drawable.image_tss;
            case "wiley":
                return R.drawable.image_wiley;
            case "windsor":
                return R.drawable.image_windsor;
            default:
                return R.drawable.image_earhart; // Because it's nice
        }
    }

    private String getRoom(String apiLocation) {
        return APILocationToRooms.get(apiLocation);
    }
}
