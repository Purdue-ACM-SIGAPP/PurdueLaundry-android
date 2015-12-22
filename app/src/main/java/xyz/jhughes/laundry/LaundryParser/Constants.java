package xyz.jhughes.laundry.LaundryParser;

/*
 * Created by tylorgarrett on 8/24/15.
 * Heavily modified by hughesjeff
 */
public class Constants {

    static String[] LIST_OF_ROOMS = {"Cary Hall West", "Earhart Hall", "Harrison Hall", "Hawkins Hall", "Hillenbrand Hall", "McCutcheon Hall",
            "Meredith Northwest", "Meredith Southeast", "Owen Hall", "Shreve Hall", "Tarkington Hall", "Third Street Suites", "Wiley Hall",
            "Windsor - Duhme", "Windsor - Warren"};

    public static String[] getListOfRooms() {
        return LIST_OF_ROOMS;
    }

    public static String getName(String room) {
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
}
