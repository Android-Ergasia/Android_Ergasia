package com.example.ergasiaandroid;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminPanelActivity extends AppCompatActivity {

    // Δήλωση μεταβλητών για τα UI στοιχεία
    private EditText etName, etPrice;
    private CheckBox cbActive;
    private Button btnSave, btnBack;

    // Η θέση στάθμευσης που τροποποιούμε
    private ParkingSpot spot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);  // Ορισμός layout

        // Σύνδεση των στοιχείων UI με το αντίστοιχο layout
        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        cbActive = findViewById(R.id.cbActive);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBackToMap);

        // Λήψη του αντικειμένου ParkingSpot που πέρασε από το intent
        spot = (ParkingSpot) getIntent().getSerializableExtra("spot");

        // Αν υπάρχει θέση, εμφάνισε τις τρέχουσες τιμές της
        if (spot != null) {
            etName.setText(spot.name);
            etPrice.setText(spot.pricePerHour.replace("€/ώρα", "").trim());
            cbActive.setChecked(spot.isAvailable);
        }

        // Όταν πατηθεί το κουμπί "Αποθήκευση"
        btnSave.setOnClickListener(v -> {
            if (spot != null) {
                // Ενημέρωση των πεδίων της spot από τα στοιχεία του χρήστη
                spot.name = etName.getText().toString().trim();
                spot.pricePerHour = etPrice.getText().toString().trim() + "€/ώρα";
                spot.isAvailable = cbActive.isChecked();

                // Δημιουργία JSON αντικειμένου για αποστολή στο server
                JSONObject json = new JSONObject();
                try {
                    json.put("id", spot.id);
                    json.put("name", spot.name);
                    json.put("price", spot.pricePerHour);
                    json.put("isAvailable", spot.isAvailable ? 1 : 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Εκτέλεση HTTP request σε ξεχωριστό νήμα
                new Thread(() -> {
                    HttpHandler handler = new HttpHandler();
                    String url = "http://10.0.2.2/parking_app/update_parking_spot.php";
                    String result = handler.makePostRequest(url, json);

                    // Ενημέρωση του UI από το κύριο νήμα
                    runOnUiThread(() -> {
                        try {
                            JSONObject res = new JSONObject(result);
                            String status = res.optString("status", "");
                            if ("Success".equals(status)) {
                                Toast.makeText(this, "Ενημερώθηκε!", Toast.LENGTH_SHORT).show();
                                finish(); // Κλείνει το activity
                            } else {
                                String error = res.optString("error", "Άγνωστο σφάλμα");
                                Toast.makeText(this, " Αποτυχία ενημέρωσης: " + error, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, " Σφάλμα στην απάντηση από τον server", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
            }
        });

        // Επιστροφή στο προηγούμενο activity χωρίς αποθήκευση
        btnBack.setOnClickListener(v -> finish());
    }
}
