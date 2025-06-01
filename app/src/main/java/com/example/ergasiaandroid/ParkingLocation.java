package com.example.ergasiaandroid;

import org.json.JSONException;
import org.json.JSONObject;

public class ParkingLocation {
    public String id;
    public String name;
    public double latitude;
    public double longitude;
    public boolean isActive;
    public String hours;
    public double cost;

    public ParkingLocation(String id, String name, double latitude, double longitude,
                           boolean isActive, String hours, double cost) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isActive = isActive;
        this.hours = hours;
        this.cost = cost;
    }

    //  Μετατροπή σε JSON για αποθήκευση
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", id);
            obj.put("name", name);
            obj.put("latitude", latitude);
            obj.put("longitude", longitude);
            obj.put("isActive", isActive);
            obj.put("hours", hours);
            obj.put("cost", cost);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    // Δημιουργία ParkingLocation από JSON για φόρτωση
    public static ParkingLocation fromJSON(JSONObject obj) throws JSONException {
        return new ParkingLocation(
                obj.getString("id"),
                obj.getString("name"),
                obj.getDouble("latitude"),
                obj.getDouble("longitude"),
                obj.getBoolean("isActive"),
                obj.getString("hours"),
                obj.getDouble("cost")
        );
    }
}


