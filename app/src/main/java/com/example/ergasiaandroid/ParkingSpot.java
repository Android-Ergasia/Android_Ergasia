package com.example.ergasiaandroid;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class ParkingSpot implements Serializable {
    public int id;
    public String name;
    public double lat, lng;
    public boolean isAvailable;
    public String address;
    public String pricePerHour;

    // 🔹 Default constructor (για JSON μετατροπές, Retrofit κλπ)
    public ParkingSpot() {}

    // 🔹 Πλήρης constructor με διεύθυνση από γεωγραφικά στοιχεία
    public ParkingSpot(int id, String name, double lat, double lng, boolean isAvailable, String pricePerHour, Context context) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.isAvailable = isAvailable;
        this.pricePerHour = pricePerHour;
        this.address = getAddressFromLocation(context, lat, lng);
    }

    // 🔹 Constructor χωρίς Context (π.χ. από PHP ή JSON)
    public ParkingSpot(int id, String name, double lat, double lng, boolean isAvailable, String pricePerHour, String address) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.isAvailable = isAvailable;
        this.pricePerHour = pricePerHour;
        this.address = address;
    }

    // 🔹 Μέθοδος μετατροπής συντεταγμένων σε διεύθυνση (Android Geocoder)
    private String getAddressFromLocation(Context context, double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address addr = addresses.get(0);
                if (addr.getThoroughfare() != null && addr.getFeatureName() != null) {
                    return addr.getThoroughfare() + " " + addr.getFeatureName();
                } else if (addr.getAddressLine(0) != null) {
                    return addr.getAddressLine(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Άγνωστη διεύθυνση";
    }
}
