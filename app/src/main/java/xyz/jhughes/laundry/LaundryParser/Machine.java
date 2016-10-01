package xyz.jhughes.laundry.LaundryParser;

import com.google.gson.Gson;

import java.io.Serializable;

/*
 * Created by tylorgarrett on 8/24/15.
 */
public class Machine implements Serializable {
    private String name;
    private String type;
    private String status;
    private String time;

    public Machine(String name, String type, String status, String time) {
        // Get rid of leading 0s
        if (name.matches(" 0[1-9]{2}")) name = name.replaceFirst("0", "");
        else if (name.matches(" 0{2}[1-9]")) name = name.replaceAll("0", "");

        this.name = name;
        this.type = type;
        this.status = status;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void printDetails() {
        System.out.println("Name: " + getName());
        System.out.println("Type: " + getType());
        System.out.println("Status: " + getStatus());
        System.out.println("Time: " + getTime());
        System.out.println("");
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
