package com.example.ergasiaandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String ADMIN_CODE = "1234"; // Ο σωστός κωδικός

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnUser = findViewById(R.id.btnContinueAsUser);
        Button btnAdmin = findViewById(R.id.btnContinueAsAdmin);
        EditText editAdminCode = findViewById(R.id.editAdminCode);
        Button btnEnterAdmin = findViewById(R.id.btnEnterAdmin);
        TextView txtWrongCode = findViewById(R.id.txtWrongCode);

        btnUser.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra("isAdmin", false);
            startActivity(intent);
        });

        btnAdmin.setOnClickListener(v -> {
            editAdminCode.setVisibility(View.VISIBLE);
            btnEnterAdmin.setVisibility(View.VISIBLE);
            txtWrongCode.setVisibility(View.GONE); // Κρύψε μήνυμα λάθους αν είχε εμφανιστεί
        });

        btnEnterAdmin.setOnClickListener(v -> {
            String enteredCode = editAdminCode.getText().toString().trim();
            if (enteredCode.equals(ADMIN_CODE)) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("isAdmin", true);
                startActivity(intent);
            } else {
                txtWrongCode.setVisibility(View.VISIBLE);
            }
        });
    }
}
