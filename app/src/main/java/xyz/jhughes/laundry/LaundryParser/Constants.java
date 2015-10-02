package xyz.jhughes.laundry.LaundryParser;

/*
 * Created by tylorgarrett on 8/24/15.
 * Heavily modified by hughesjeff
 */
public class Constants {

    static String[] LIST_OF_ROOMS = {"Cary West", "Earhart", "Harrison", "Hawkins", "Hillenbrand", "McCutcheon",
            "Meredith Northwest", "Meredith Southeast", "Owen", "Shreve", "Tarkington", "Third Street", "Wiley",
            "Windsor - Duhme", "Windsor - Warren"};

    public static String[] getListOfRooms() {
        return LIST_OF_ROOMS;
    }

    public static String getName(String room) {
        switch (room) {
            case "Cary West":
                return "cary";
            case "Earhart":
                return "earhart";
            case "Harrison":
                return "harrison";
            case "Hawkins":
                return "hawkins";
            case "Hillenbrand":
                return "hillenbrand";
            case "McCutcheon":
                return "mccutcheon";
            case "Meredith Northwest":
                return "meredith_nw";
            case "Meredith Southeast":
                return "meredith_se";
            case "Owen":
                return "owen";
            case "Shreve":
                return "shreve";
            case "Tarkington":
                return "tarkington";
            case "Third Street":
                return "third";
            case "Wiley":
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
