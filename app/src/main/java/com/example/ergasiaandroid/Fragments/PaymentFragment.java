package com.example.ergasiaandroid.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ergasiaandroid.R;
import com.example.ergasiaandroid.HttpHandler;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentFragment extends Fragment {

    // Μεταβλητές για να περαστούν από το προηγούμενο Fragment
    private String sector, address, startTime, plate, email, spotPriceStr;
    private double amount;

    // TextInputEditText για πεδία κάρτας
    private TextInputEditText cardNumber, expiryMonth, expiryYear, cvv, cardHolder;
    private Button payButton;
    private TextView paymentAmount, parkingInfo;

    // Δημιουργεί νέο instance του fragment με δεδομένα πληρωμής
    public static PaymentFragment newInstance(String sector, String address, String startTime,
                                              String plate, String email, double amount, String spotPriceStr) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();
        args.putString("sector", sector);
        args.putString("address", address);
        args.putString("start_time", startTime);
        args.putString("plate", plate);
        args.putString("email", email);
        args.putDouble("amount", amount);
        args.putString("spot_price", spotPriceStr);
        fragment.setArguments(args);
        return fragment;
    }

    public PaymentFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Παίρνει τα ορίσματα που δόθηκαν μέσω newInstance
        if (getArguments() != null) {
            sector = getArguments().getString("sector");
            address = getArguments().getString("address");
            startTime = getArguments().getString("start_time");
            plate = getArguments().getString("plate");
            email = getArguments().getString("email");
            amount = getArguments().getDouble("amount", 0.0);
            spotPriceStr = getArguments().getString("spot_price");
        }

        // Ρύθμιση toolbar
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle("Πληρωμή στάθμευσης");
            setHasOptionsMenu(true);
        }

        // Αντιστοίχιση UI στοιχείων
        cardNumber   = view.findViewById(R.id.cardNumber);
        expiryMonth  = view.findViewById(R.id.expiryMonth);
        expiryYear   = view.findViewById(R.id.expiryYearEdit);
        cvv          = view.findViewById(R.id.cvv);
        cardHolder   = view.findViewById(R.id.cardHolder);
        payButton    = view.findViewById(R.id.payButton);
        paymentAmount= view.findViewById(R.id.paymentAmount);
        parkingInfo  = view.findViewById(R.id.parkingInfo);

        // Προβολή ποσού πληρωμής
        String costText = String.format(Locale.getDefault(), "%.2f €", amount);
        paymentAmount.setText("Πληρωτέο Ποσό: " + costText);

        // Εμφάνιση στοιχείων στάθμευσης
        StringBuilder info = new StringBuilder();
        info.append("Θέση: ").append(sector).append("\n")
                .append("Διεύθυνση: ").append(address).append("\n")
                .append("Ώρα έναρξης: ").append(startTime).append("\n")
                .append("Πινακίδα: ").append(plate).append("\n")
                .append("Email: ").append(email);
        parkingInfo.setText(info.toString());

        // Αποθηκεύει το email σε SharedPreferences για μελλοντική χρήση
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        prefs.edit().putString("user_email", email).apply();

        // Όταν πατηθεί το κουμπί "Πληρωμή"
        payButton.setOnClickListener(v -> {
            // Ελέγχουμε αν όλα τα πεδία κάρτας είναι σωστά
            if (!validateCardFields()) {
                return;
            }

            // 1) Αποθήκευση στοιχείων κάρτας στη βάση
            saveCardDataToDatabase();

            // 2) Αποθήκευση στοιχείων χρήστη & ιστορικού στάθμευσης & μείωση wallet
            saveUserDataToDatabase();
            saveParkingHistoryToDatabase();
            updateWallet();

            // 3) Κάνουμε ξανά διαθέσιμη τη θέση
            setSpotAvailable(sector);

            Toast.makeText(getContext(), "Πληρωμή επιτυχής!", Toast.LENGTH_SHORT).show();

            // Επιστροφή στον χάρτη
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MapFragment())
                    .commit();
        });
    }

    // Έλεγχος εγκυρότητας πεδίων κάρτας
    private boolean validateCardFields() {
        if (TextUtils.isEmpty(cardNumber.getText())) {
            cardNumber.setError("Απαιτείται αριθμός κάρτας");
            return false;
        }
        if (TextUtils.isEmpty(expiryMonth.getText())) {
            expiryMonth.setError("Απαιτείται μήνας λήξης");
            return false;
        }
        if (TextUtils.isEmpty(expiryYear.getText())) {
            expiryYear.setError("Απαιτείται έτος λήξης");
            return false;
        }
        if (TextUtils.isEmpty(cvv.getText())) {
            cvv.setError("Απαιτείται CVV");
            return false;
        }
        if (TextUtils.isEmpty(cardHolder.getText())) {
            cardHolder.setError("Απαιτείται όνομα κατόχου");
            return false;
        }
        return true;
    }

    // 1) Αποθήκευση στοιχείων κάρτας στη βάση
    private void saveCardDataToDatabase() {
        String url = "http://10.0.2.2/parking_app/save_card_data.php";

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("user_id",    email != null ? email : "");
            params.put("card_number",cardNumber.getText().toString().trim());
            params.put("expiry_month",expiryMonth.getText().toString().trim());
            params.put("expiry_year", expiryYear.getText().toString().trim());
            params.put("cvv",         cvv.getText().toString().trim());
            params.put("card_holder", cardHolder.getText().toString().trim());

            JSONObject jsonObject = new JSONObject(params);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, url, jsonObject,
                    response -> {
                        // Επιτυχία αποθήκευσης κάρτας
                        System.out.println("✅ Card saved: " + response.toString());
                    },
                    error -> {
                        error.printStackTrace();
                        if (error.networkResponse != null) {
                            String body = new String(error.networkResponse.data);
                            System.out.println("⚠️ CARD save error: " + body);
                        }
                        Toast.makeText(getContext(), "❌ Σφάλμα αποθήκευσης κάρτας", Toast.LENGTH_SHORT).show();
                    });

            Volley.newRequestQueue(requireContext()).add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2α) Αποθήκευση των στοιχείων του χρήστη στη βάση (μέσω PHP API)
    private void saveUserDataToDatabase() {
        String url = "http://10.0.2.2/parking_app/save_user_data.php";

        try {
            if (email == null || sector == null || startTime == null) {
                System.out.println("❌ Null πεδία στο saveUserDataToDatabase");
                return;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("user_id",     email);
            params.put("wallet_balance", 0.0);
            params.put("total_spent", amount);
            params.put("total_park_time", 1);
            params.put("last_sector", sector);
            params.put("last_park_time", startTime);

            JSONObject jsonObject = new JSONObject(params);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, url, jsonObject,
                    response -> System.out.println("✅ User data saved: " + response.toString()),
                    error -> {
                        error.printStackTrace();
                        if (error.networkResponse != null) {
                            String body = new String(error.networkResponse.data);
                            System.out.println("⚠️ USER DB error response: " + body);
                        }
                        Toast.makeText(getContext(), "❌ Σφάλμα χρήστη DB", Toast.LENGTH_SHORT).show();
                    });

            Volley.newRequestQueue(requireContext()).add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2β) Αποθήκευση ιστορικού στάθμευσης
    private void saveParkingHistoryToDatabase() {
        String url = "http://10.0.2.2/parking_app/insert_parking_history.php";

        try {
            String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            Map<String, Object> params = new HashMap<>();
            params.put("user_id", email);
            params.put("spot", sector);
            params.put("start", startTime);
            params.put("end", endTime);
            params.put("rate", Double.parseDouble(spotPriceStr));
            params.put("amount", amount);

            JSONObject jsonObject = new JSONObject(params);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, url, jsonObject,
                    response -> System.out.println("✅ Parking history saved: " + response.toString()),
                    error -> {
                        error.printStackTrace();
                        if (error.networkResponse != null) {
                            String responseBody = new String(error.networkResponse.data);
                            System.out.println("⚠️ Parking history error: " + responseBody);
                        }
                        Toast.makeText(getContext(), "❌ Σφάλμα ιστορικού στάθμευσης", Toast.LENGTH_SHORT).show();
                    });

            Volley.newRequestQueue(requireContext()).add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2γ) Μειώνει το υπόλοιπο στο πορτοφόλι του χρήστη
    private void updateWallet() {
        SharedPreferences prefs = requireContext().getSharedPreferences("WalletPrefs", Context.MODE_PRIVATE);
        float currentBalance = prefs.getFloat("wallet_balance", 0);
        float newBalance = (float) (currentBalance - amount);
        prefs.edit().putFloat("wallet_balance", newBalance).apply();
    }

    // 3) Μέθοδος που καλεί το PHP για να κάνει ξανά διαθέσιμη τη θέση
    private void setSpotAvailable(String spotName) {
        new Thread(() -> {
            try {
                String url = "http://10.0.2.2/parking_app/make_available.php";
                String postData = "spot_name=" + java.net.URLEncoder.encode(spotName, "UTF-8");

                String response = HttpHandler.post(url, postData);

                if (response != null && response.contains("success")) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Η θέση \"" + spotName + "\" είναι ξανά διαθέσιμη.", Toast.LENGTH_SHORT).show()
                    );
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "⚠️ Αποτυχία ενημέρωσης διαθεσιμότητας.", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "⚠️ Εξαίρεση: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}
