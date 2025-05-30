package com.example.ergasiaandroid;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminPanelActivity extends AppCompatActivity {

    private EditText etName, etPrice;
    private CheckBox cbActive;
    private Button btnSave, btnBack;

    private ParkingSpot spot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        cbActive = findViewById(R.id.cbActive);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBackToMap);

        spot = (ParkingSpot) getIntent().getSerializableExtra("spot");

        if (spot != null) {
            etName.setText(spot.name);
            etPrice.setText(spot.pricePerHour.replace("€/ώρα", "").trim());
            cbActive.setChecked(spot.isAvailable);
        }

        btnSave.setOnClickListener(v -> {
            if (spot != null) {
                spot.name = etName.getText().toString().trim();
                spot.pricePerHour = etPrice.getText().toString().trim() + "€/ώρα";
                spot.isAvailable = cbActive.isChecked();

                // Αν θες, αποθήκευση σε storage/DB/SharedPreferences
                Toast.makeText(this, "Ενημερώθηκε!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
