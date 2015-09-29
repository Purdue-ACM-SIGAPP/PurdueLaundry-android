package xyz.jhughes.laundry.LaundryParser;

/*
 * Created by tylorgarrett on 8/24/15.
 */
public class Constants {
    static String CARY_WEST = "http://wpvitassuds01.itap.purdue.edu/washalertweb/washalertweb.aspx?location=f9db4842-8fae-47d6-8660-645d358ef739";
    static String EARHART = "http://wpvitassuds01.itap.purdue.edu/washalertweb/washalertweb.aspx?location=a0728ede-60be-4155-8ca9-dcde37ad431d";
    static String HARRISON = "http://wpvitassuds01.itap.purdue.edu/washalertweb/washalertweb.aspx?location=525ba5bf-7e58-4359-b78f-e8bfb34416cc";
    static String HAWKINS = "http://wpvitassuds01.itap.purdue.edu/washalertweb/washalertweb.aspx?location=1733b280-3ea8-4259-be35-d03b6b6d606a";
    static String HILLENBRAND = "http://wpvitassuds01.itap.purdue.edu/washalertweb/washalertweb.aspx?location=75896de0-7b2e-4270-bee0-4aefc49b1bd2";
    static String MCCUTCHEON = "http://wpvitassuds01.itap.purdue.edu/washalertweb/washalertweb.aspx?location=27b0544c-8fba-401b-b133-6307cd1fb851";
    static String MEREDITH_NW = "http://wpvitassuds01.itap.purdue.edu/washalertweb/washalertweb.aspx?location=697af07e-a32e-445a-b6ad-4f381458e7b4";
    static String MEREDITH_SE = "http://wpvitassuds01.itap.purdue.edu/washalertweb/washalertweb.aspx?location=3a05822f-c67a-49e9-8105-8255014d491f";
    static String OWEN = "http://wpvitassuds01.itap.purdue.edu/washalertweb/washalertweb.aspx?location=706682c2-e8f8-4503-8d36-1283cc9bda1e";
    static String SHREVE = "http://wpvitassuds01.itap.purdue.edu/washalertweb/washalertweb.aspx?location=f681e273-d865-4274-bf4a-ba9dea2229ce";
    static String TARKINGTON = "http://wpvitassuds01.itap.purdue.edu/washalertweb/washalertweb.aspx?location=06784d8c-9c16-4d05-9548-0f82dfdcc842";
    static String THIRD_STREET = "http://wpvitassuds01.itap.purdue.edu/washalertweb/washalertweb.aspx?location=96ed9941-352d-478f-88c3-1a0320066464";
    static String WILEY = "http://wpvitassuds01.itap.purdue.edu/washalertweb/washalertweb.aspx?location=c29eba8b-63d1-4090-bd32-ea85c67f483c";
    static String WINDSOR_DUHME = "http://wpvitassuds01.itap.purdue.edu/washalertweb/washalertweb.aspx?location=b98170b6-c561-4ea5-8b2d-28ebf4f7cdda";
    static String WINDSOR_WARREN = "http://wpvitassuds01.itap.purdue.edu/washalertweb/washalertweb.aspx?location=da8165d6-7ff9-4311-80c7-2bc3e2da5e5e";
    static String[] LIST_OF_ROOMS = {"Cary West", "Earhart", "Harrison", "Hawkins", "Hillenbrand", "McCutcheon",
            "Meredith Northwest", "Meredith Southeast", "Owen", "Shreve", "Tarkington", "Third Street", "Wiley",
            "Windsor - Duhme", "Windsor - Warren"};

    public static String[] getListOfRooms() {
        return LIST_OF_ROOMS;
    }
    public static String getURL(String room) {
        switch (room) {
            case "Cary West":
                return CARY_WEST;
            case "Earhart":
                return EARHART;
            case "Harrison":
                return HARRISON;
            case "Hawkins":
                return HAWKINS;
            case "Hillenbrand":
                return HILLENBRAND;
            case "McCutcheon":
                return MCCUTCHEON;
            case "Meredith Northwest":
                return MEREDITH_NW;
            case "Meredith Southeast":
                return MEREDITH_SE;
            case "Owen":
                return OWEN;
            case "Shreve":
                return SHREVE;
            case "Tarkington":
                return TARKINGTON;
            case "Third Street":
                return THIRD_STREET;
            case "Wiley":
                return WILEY;
            case "Windsor - Duhme":
                return WINDSOR_DUHME;
            case "Windsor - Warren":
                return WINDSOR_WARREN;
            default:
                break;
        }
        return null;
    }
}
