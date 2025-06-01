package com.example.ergasiaandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Σταθερός κωδικός πρόσβασης για διαχειριστή (admin)
    private static final String ADMIN_CODE = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ορίζει ποιο layout θα εμφανιστεί όταν ξεκινάει η Activity
        setContentView(R.layout.activity_main);

        // Σύνδεση των στοιχείων του layout με τις μεταβλητές Java
        Button btnUser = findViewById(R.id.btnContinueAsUser);
        Button btnAdmin = findViewById(R.id.btnContinueAsAdmin);
        EditText editAdminCode = findViewById(R.id.editAdminCode);
        Button btnEnterAdmin = findViewById(R.id.btnEnterAdmin);
        TextView txtWrongCode = findViewById(R.id.txtWrongCode);

        // Όταν πατηθεί το κουμπί "Συνέχεια ως Χρήστης"
        btnUser.setOnClickListener(v -> {
            // Δημιουργία intent για μετάβαση στη MapsActivity
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            // Πέρασμα παραμέτρου που δηλώνει ότι ο χρήστης ΔΕΝ είναι admin
            intent.putExtra("isAdmin", false);
            // Εκκίνηση της νέας activity
            startActivity(intent);
        });

        // Όταν πατηθεί το κουμπί "Συνέχεια ως Διαχειριστής"
        btnAdmin.setOnClickListener(v -> {
            // Εμφάνιση του πεδίου εισαγωγής κωδικού και του κουμπιού επιβεβαίωσης
            editAdminCode.setVisibility(View.VISIBLE);
            btnEnterAdmin.setVisibility(View.VISIBLE);
            // Απόκρυψη του μηνύματος λάθους (αν ήταν ήδη ορατό)
            txtWrongCode.setVisibility(View.GONE);
        });

        // Όταν πατηθεί το κουμπί "Είσοδος" για admin
        btnEnterAdmin.setOnClickListener(v -> {
            // Παίρνει το κείμενο που πληκτρολόγησε ο χρήστης
            String enteredCode = editAdminCode.getText().toString().trim();

            // Έλεγχος αν ο κωδικός είναι σωστός
            if (enteredCode.equals(ADMIN_CODE)) {
                // Δημιουργία intent για μετάβαση στη MapsActivity
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                // Πέρασμα παραμέτρου που δηλώνει ότι ο χρήστης ΕΙΝΑΙ admin
                intent.putExtra("isAdmin", true);
                // Εκκίνηση της νέας activity
                startActivity(intent);
            } else {
                // Εμφάνιση μηνύματος λάθους για λάθος κωδικό
                txtWrongCode.setVisibility(View.VISIBLE);
            }
        });
    }
}
