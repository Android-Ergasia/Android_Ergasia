package com.example.ergasiaandroid.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.ergasiaandroid.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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
        String url = "https://mocki.io/v1/97ec4d09-d847-42dd-a3ab-1f539fbc51c2";

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray history = response.getJSONArray("parking_history");

                            double totalAmount = 0.0;
                            double totalHours = 0.0;
                            Map<String, Double> hoursPerSpot = new HashMap<>();

                            for (int i = 0; i < history.length(); i++) {
                                JSONObject entry = history.getJSONObject(i);

                                String spot = entry.getString("spot");
                                String startStr = entry.getString("start");
                                String endStr = entry.getString("end");
                                double rate = entry.getDouble("rate");

                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                                Date start = format.parse(startStr);
                                Date end = format.parse(endStr);

                                long diffMs = end.getTime() - start.getTime();
                                double hours = diffMs / (1000.0 * 60 * 60);

                                totalHours += hours;
                                totalAmount += hours * rate;

                                if (!hoursPerSpot.containsKey(spot)) {
                                    hoursPerSpot.put(spot, 0.0);
                                }
                                hoursPerSpot.put(spot, hoursPerSpot.get(spot) + hours);
                            }

                            tvTotalAmount.setText("Συνολικό Ποσό: " + String.format(Locale.getDefault(), "%.2f €", totalAmount));
                            tvTotalHours.setText("Συνολικές Ώρες: " + String.format(Locale.getDefault(), "%.2f", totalHours));

                            layoutPerSpot.removeAllViews();
                            for (Map.Entry<String, Double> entry : hoursPerSpot.entrySet()) {
                                TextView tv = new TextView(getContext());
                                tv.setText(entry.getKey() + ": " + String.format(Locale.getDefault(), "%.2f ώρες", entry.getValue()));
                                tv.setTextSize(18);
                                layoutPerSpot.addView(tv);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            tvTotalAmount.setText("Σφάλμα ανάγνωσης δεδομένων");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tvTotalAmount.setText("Σφάλμα σύνδεσης");
                    }
                });

        queue.add(request);
    }
}
