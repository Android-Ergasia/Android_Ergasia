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

    private TextView tvTotalAmount, tvTotalHours;
    private LinearLayout layoutPerSpot;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        tvTotalHours = view.findViewById(R.id.tvTotalHours);
        layoutPerSpot = view.findViewById(R.id.layoutPerSpot);

        fetchStats();

        return view;
    }

    private void fetchStats() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", null);
        if (userEmail == null) {
            tvTotalAmount.setText("Δεν υπάρχει χρήστης");
            return;
        }

        String url = "http://10.0.2.2/parking_app/get_parking_history.php?user_id=" + userEmail;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    System.out.println("📥 RAW JSON: " + response.toString());

                    try {
                        JSONArray history = response.getJSONArray("parking_history");

                        double totalAmount = 0.0;
                        double totalHours = 0.0;
                        Map<String, Double> hoursPerSpot = new HashMap<>();

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                        for (int i = 0; i < history.length(); i++) {
                            JSONObject entry = history.getJSONObject(i);

                            String spot = entry.optString("spot", "Άγνωστη θέση");
                            String startStr = entry.optString("start", "");
                            String endStr = entry.optString("end", "");
                            double rate = entry.optDouble("rate", 0.0);

                            if (startStr.isEmpty() || endStr.isEmpty()) continue;

                            Date start = format.parse(startStr);
                            Date end = format.parse(endStr);
                            if (start == null || end == null) continue;

                            long diffMs = end.getTime() - start.getTime();
                            if (diffMs <= 0) continue;

                            double hours = Math.ceil(diffMs / (1000.0 * 60 * 60)); // Στρογγυλοποίηση προς τα πάνω

                            double amount = entry.has("amount_paid") && !entry.isNull("amount_paid")
                                    ? entry.getDouble("amount_paid")
                                    : hours * rate;

                            totalHours += hours;
                            totalAmount += amount;

                            hoursPerSpot.put(spot, hoursPerSpot.getOrDefault(spot, 0.0) + hours);
                        }

                        tvTotalAmount.setText("Συνολικό Ποσό: " + String.format(Locale.getDefault(), "%.2f €", totalAmount));
                        tvTotalHours.setText("Συνολικές Ώρες: " + String.format(Locale.getDefault(), "%.0f", totalHours));

                        layoutPerSpot.removeAllViews();
                        for (Map.Entry<String, Double> entry : hoursPerSpot.entrySet()) {
                            TextView tv = new TextView(getContext());
                            tv.setText(entry.getKey() + ": " + String.format(Locale.getDefault(), "%.0f ώρες", entry.getValue()));
                            tv.setTextSize(18);
                            layoutPerSpot.addView(tv);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        tvTotalAmount.setText("Σφάλμα ανάγνωσης δεδομένων");
                        tvTotalHours.setText("");
                    }
                },
                error -> {
                    error.printStackTrace();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String responseBody = new String(error.networkResponse.data);
                        System.out.println("⚠️ Σφάλμα από server (Volley): " + responseBody);
                    } else {
                        System.out.println("⚠️ Volley error χωρίς απάντηση");
                    }

                    tvTotalAmount.setText("Σφάλμα σύνδεσης");
                    tvTotalHours.setText("");
                });

        queue.add(request);
    }
}
