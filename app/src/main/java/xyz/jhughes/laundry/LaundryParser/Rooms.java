package xyz.jhughes.laundry.LaundryParser;

import java.util.HashMap;

import xyz.jhughes.laundry.R;

public class Rooms {

     private static Rooms rm = new Rooms();

     private  String[] LIST_OF_ROOMS = {"Cary Hall West", "Earhart Hall", "Harrison Hall", "Hawkins Hall", "Hillenbrand Hall", "McCutcheon Hall",
            "Meredith Northwest", "Meredith Southeast", "Owen Hall", "Shreve Hall", "Tarkington Hall", "Third Street Suites", "Wiley Hall",
            "Windsor - Duhme", "Windsor - Warren"};
    private HashMap<String, String> roomsToAPILocations;
    private HashMap<String, Integer> roomsToImage;
    private HashMap<String, Integer> machineAvailabilityColors;

    private Rooms() {
        roomsToAPILocations = new HashMap<>();
        roomsToImage = new HashMap<>();
        machineAvailabilityColors = new HashMap<>();

        for (String room : LIST_OF_ROOMS) {
            roomsToAPILocations.put(room, toAPILocation(room));
            roomsToImage.put(room, toImageResourceId(room));
        }

        machineAvailabilityColors.put("Available", R.color.Available);
        machineAvailabilityColors.put("In use", R.color.InUse);
        machineAvailabilityColors.put("Almost done", R.color.AlmostDone);
        machineAvailabilityColors.put("End of cycle", R.color.Finished);

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

    public int roomToImageResource(String roomName) {
        return roomsToImage.get(roomName);
    }

    private String toAPILocation(String room) {
        switch (room) {
            case "Cary Hall West":
                return "cary";
            case "Earhart Hall":
                return "earhart";
            case "Harrison Hall":
                return "harrison";
            case "Hawkins Hall":
                return "hawkins";
            case "Hillenbrand Hall":
                return "hillenbrand";
            case "McCutcheon Hall":
                return "mccutcheon";
            case "Meredith Northwest":
                return "meredith_nw";
            case "Meredith Southeast":
                return "meredith_se";
            case "Owen Hall":
                return "owen";
            case "Shreve Hall":
                return "shreve";
            case "Tarkington Hall":
                return "tarkington";
            case "Third Street Suites":
                return "third";
            case "Wiley Hall":
                return "wiley";
            case "Windsor - Duhme":
                return "windsor_duhme";
            case "Windsor - Warren":
                return "windsor_warren";
            default:
                break;
        }
        return null;
    }

    private int toImageResourceId(String room) {
        switch (room) {
            case "Cary Hall West":
                return  R.drawable.image_cary;
            case "Earhart Hall":
                 return  R.drawable.image_earhart;
            case "Harrison Hall":
                  return R.drawable.image_harrison;
            case "Hawkins Hall":
                 return  R.drawable.image_hawkins;
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
}
