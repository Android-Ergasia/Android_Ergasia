package com.example.ergasiaandroid;


import android.os.*;
import org.json.*;
import java.util.*;
import okhttp3.*;

public class OkHttpHandler {

    public OkHttpHandler() {
        // Καθορισμός πολιτικής για χρήση σε κύριο νήμα (thread)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    // Μέθοδος για την αποστολή των δεδομένων για την καταχώρηση του parking session
    public void logParkingSession(String url, int user_id, String license_plate, int spot_id,
                                  String start_time, String end_time, int total_minutes, double amount_paid) throws Exception {

        // Δημιουργία του OkHttpClient
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        // Δημιουργία δεδομένων POST για αποστολή στο server
        RequestBody body = new FormBody.Builder()
                .add("user_id", String.valueOf(user_id))
                .add("license_plate", license_plate)
                .add("spot_id", String.valueOf(spot_id))
                .add("start_time", start_time)
                .add("end_time", end_time)
                .add("total_minutes", String.valueOf(total_minutes))
                .add("amount_paid", String.valueOf(amount_paid))
                .build();

        // Δημιουργία του αιτήματος (Request)
        Request request = new Request.Builder()
                .url(url)
                .post(body) // Χρησιμοποιούμε την μέθοδο POST για την αποστολή δεδομένων
                .build();

        // Εκτέλεση του αιτήματος
        Response response = client.newCall(request).execute();

        // Διαβάζουμε την απόκριση από το server
        String responseData = response.body().string();
        if (response.isSuccessful()) {
            System.out.println("Καταχώρηση επιτυχής: " + responseData);
        } else {
            System.out.println("Σφάλμα κατά την αποθήκευση: " + responseData);
        }
    }

}
