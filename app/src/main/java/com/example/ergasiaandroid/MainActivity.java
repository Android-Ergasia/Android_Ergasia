package com.example.ergasiaandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnUser = findViewById(R.id.btnContinueAsUser);
        Button btnAdmin = findViewById(R.id.btnContinueAsAdmin);

        btnUser.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            // Μπορείς αν θέλεις να προσθέσεις intent.putExtra("isAdmin", false); για να είναι ξεκάθαρο
            intent.putExtra("isAdmin", false);
            startActivity(intent);
        });

        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra("isAdmin", true); // <-- Flag για διαχειριστή
            startActivity(intent);
        });
    }
}
