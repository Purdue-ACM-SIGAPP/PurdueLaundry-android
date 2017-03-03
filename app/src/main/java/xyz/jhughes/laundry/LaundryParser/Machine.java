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
        setName(name);
        this.type = type;
        this.status = status;
        this.time = time;
    }

    public String getName() {
        name = name.replace("00", "");
        if (name.contains("0")) {
            for (int i = name.length() - 1; i > 0; i--) {
                if (Character.isDigit(name.charAt(i)) && name.charAt(i - 1) == '0') {
                    name = name.substring(0, i - 1) + name.substring(i);
                    break;
                }
            }
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Machine machine = (Machine) o;

        if (!getName().equals(machine.getName())) return false;
        return type.equals(machine.type);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
