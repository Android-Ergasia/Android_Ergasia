package com.example.ergasiaandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminPanelActivity extends AppCompatActivity {

    private List<ParkingLocation> parkingLocations = new ArrayList<>();
    private LinearLayout adminLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        adminLayout = findViewById(R.id.adminLayout);

        loadParkingLocations();

        for (ParkingLocation p : parkingLocations) {
            View v = getLayoutInflater().inflate(R.layout.parking_item, null);

            EditText etHours = v.findViewById(R.id.etHours);
            EditText etCost = v.findViewById(R.id.etCost);
            CheckBox cbActive = v.findViewById(R.id.cbActive);
            Button btnSave = v.findViewById(R.id.btnSave);

            etHours.setText(p.hours);
            etCost.setText(String.valueOf(p.cost));
            cbActive.setChecked(p.isActive);

            btnSave.setOnClickListener(view -> {
                p.hours = etHours.getText().toString();
                p.cost = Double.parseDouble(etCost.getText().toString());
                p.isActive = cbActive.isChecked();
                saveParkingLocations();
            });

            adminLayout.addView(v);
        }

        Button btnBack = findViewById(R.id.btnBackToMap);
        btnBack.setOnClickListener(v -> {
            setResult(RESULT_OK); // ✅ ενημερώνει ότι υπήρξαν αλλαγές
            finish();
        });
    }

    private void saveParkingLocations() {
        List<JSONObject> jsonList = new ArrayList<>();
        for (ParkingLocation p : parkingLocations) {
            jsonList.add(p.toJSON());
        }

        String data = jsonList.toString();

        getSharedPreferences("parking_prefs", MODE_PRIVATE)
                .edit()
                .putString("parking_data", data)
                .apply();
    }

    private void loadParkingLocations() {
        parkingLocations.clear();
        String data = getSharedPreferences("parking_prefs", MODE_PRIVATE)
                .getString("parking_data", null);

        if (data != null) {
            try {
                JSONArray array = new JSONArray(data);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    ParkingLocation p = ParkingLocation.fromJSON(obj);
                    parkingLocations.add(p);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            parkingLocations.add(new ParkingLocation("1", "Aloha Lot", 21.3075, -157.8570, true, "08:00 - 20:00", 2.5));
            parkingLocations.add(new ParkingLocation("2", "Beachside Parking", 21.3080, -157.8600, false, "09:00 - 21:00", 3.0));
            parkingLocations.add(new ParkingLocation("3", "Downtown Garage", 21.3050, -157.8585, true, "24/7", 4.0));
        }
    }
}
