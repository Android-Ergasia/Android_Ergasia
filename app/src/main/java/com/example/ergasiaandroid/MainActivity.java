package com.example.ergasiaandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ergasiaandroid.Fragments.RegisterFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private Button btnLogin;
    private TextView txtError, txtGoToRegister;
    private ScrollView loginScroll;
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // περιλαμβάνει login + fragment_container

        // UI στοιχεία
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtError = findViewById(R.id.txtError);
        txtGoToRegister = findViewById(R.id.txtGoToRegister);

        loginScroll = findViewById(R.id.loginScroll);
        fragmentContainer = findViewById(R.id.fragment_container);

        // Σύνδεση
        btnLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                txtError.setText("Συμπλήρωσε email και κωδικό.");
                txtError.setVisibility(View.VISIBLE);
                return;
            }

            loginUser(email, password);
        });

        // Εναλλαγή σε RegisterFragment
        txtGoToRegister.setOnClickListener(v -> {
            loginScroll.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Αν επιστρέψουμε από RegisterFragment, να επανεμφανιστεί το login
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                fragmentContainer.setVisibility(View.GONE);
                loginScroll.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loginUser(String email, String password) {
        String url = "http://10.0.2.2/parking_app/login.php";

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        JSONObject jsonObject = new JSONObject(params);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                response -> {
                    boolean success = response.optBoolean("success", false);
                    if (success) {
                        String role = response.optString("role", "user");
                        boolean isAdmin = role.equals("admin");

                        // Αποθήκευση email σε SharedPreferences
                        getSharedPreferences("UserPrefs", MODE_PRIVATE)
                                .edit()
                                .putString("user_email", email)
                                .apply();

                        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                        intent.putExtra("isAdmin", isAdmin);
                        intent.putExtra("user_email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        txtError.setText(response.optString("message", "Λάθος στοιχεία"));
                        txtError.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    txtError.setText("Σφάλμα σύνδεσης.");
                    txtError.setVisibility(View.VISIBLE);
                    error.printStackTrace();
                });

        Volley.newRequestQueue(this).add(request);
    }
}
