package com.example.ergasiaandroid.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ergasiaandroid.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    // Στοιχεία UI για συνολικό ποσό, ώρες και layout με λεπτομέρειες ανά θέση
    private TextView tvTotalAmount, tvTotalHours;
    private LinearLayout layoutPerSpot;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Συνδέει το fragment με το layout XML
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        // Βρίσκει τα στοιχεία από το layout
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        tvTotalHours = view.findViewById(R.id.tvTotalHours);
        layoutPerSpot = view.findViewById(R.id.layoutPerSpot);

        // Καλεί τη μέθοδο για φόρτωση των στατιστικών
        fetchStats();

        return view;
    }

    private void fetchStats() {
        // Παίρνει το email του χρήστη από τα SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", null);

        // Αν δεν έχει αποθηκευτεί email, εμφανίζει μήνυμα
        if (userEmail == null) {
            tvTotalAmount.setText("Δεν υπάρχει χρήστης");
            return;
        }

        // Φτιάχνει το URL για το PHP API
        String url = "http://10.0.2.2/parking_app/get_parking_history.php?user_id=" + userEmail;

        // Δημιουργεί ένα Volley request queue
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Δημιουργεί το GET request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // Εμφανίζει την ακατέργαστη απάντηση στο log
                    System.out.println("📥 RAW JSON: " + response.toString());

                    try {
                        // Παίρνει τον πίνακα ιστορικού στάθμευσης
                        JSONArray history = response.getJSONArray("parking_history");

                        double totalAmount = 0.0;
                        double totalHours = 0.0;
                        Map<String, Double> hoursPerSpot = new HashMap<>();

                        // Formatter για μετατροπή ημερομηνιών
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                        // Διατρέχει κάθε εγγραφή στάθμευσης
                        for (int i = 0; i < history.length(); i++) {
                            JSONObject entry = history.getJSONObject(i);

                            String spot = entry.optString("spot", "Άγνωστη θέση");
                            String startStr = entry.optString("start", "");
                            String endStr = entry.optString("end", "");
                            double rate = entry.optDouble("rate", 0.0);

                            // Αν δεν υπάρχουν ημερομηνίες, αγνοεί την εγγραφή
                            if (startStr.isEmpty() || endStr.isEmpty()) continue;

                            Date start = format.parse(startStr);
                            Date end = format.parse(endStr);
                            if (start == null || end == null) continue;

                            long diffMs = end.getTime() - start.getTime();
                            if (diffMs <= 0) continue;

                            // Υπολογίζει τις ώρες στάθμευσης (στρογγυλοποιημένες προς τα πάνω)
                            double hours = Math.ceil(diffMs / (1000.0 * 60 * 60));

                            // Υπολογίζει το ποσό είτε από το πεδίο amount_paid είτε από το rate
                            double amount = entry.has("amount_paid") && !entry.isNull("amount_paid")
                                    ? entry.getDouble("amount_paid")
                                    : hours * rate;

                            // Προσθέτει στο σύνολο ωρών και ποσού
                            totalHours += hours;
                            totalAmount += amount;

                            // Προσθέτει ώρες ανά θέση (grouping)
                            hoursPerSpot.put(spot, hoursPerSpot.getOrDefault(spot, 0.0) + hours);
                        }

                        // Εμφανίζει συνολικά ποσά
                        tvTotalAmount.setText("Συνολικό Ποσό: " + String.format(Locale.getDefault(), "%.2f €", totalAmount));
                        tvTotalHours.setText("Συνολικές Ώρες: " + String.format(Locale.getDefault(), "%.0f", totalHours));

                        // Καθαρίζει και προσθέτει τις ώρες ανά θέση στο layout
                        layoutPerSpot.removeAllViews();
                        for (Map.Entry<String, Double> entry : hoursPerSpot.entrySet()) {
                            TextView tv = new TextView(getContext());
                            tv.setText(entry.getKey() + ": " + String.format(Locale.getDefault(), "%.0f ώρες", entry.getValue()));
                            tv.setTextSize(18);
                            layoutPerSpot.addView(tv);
                        }

                    } catch (Exception e) {
                        // Σφάλμα κατά την επεξεργασία JSON
                        e.printStackTrace();
                        tvTotalAmount.setText("Σφάλμα ανάγνωσης δεδομένων");
                        tvTotalHours.setText("");
                    }
                },
                error -> {
                    // Σφάλμα δικτύου ή HTTP
                    error.printStackTrace();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String responseBody = new String(error.networkResponse.data);
                        System.out.println(" Σφάλμα από server (Volley): " + responseBody);
                    } else {
                        System.out.println("Volley error χωρίς απάντηση");
                    }

                    // Εμφανίζει μηνύματα σφάλματος
                    tvTotalAmount.setText("Σφάλμα σύνδεσης");
                    tvTotalHours.setText("");
                });

        // Εκτελεί το request
        queue.add(request);
    }
}
