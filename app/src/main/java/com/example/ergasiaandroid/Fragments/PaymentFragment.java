package com.example.ergasiaandroid.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentFragment extends Fragment {

    // Μεταβλητές για να περαστούν από το προηγούμενο Fragment
    private String sector, address, startTime, plate, email, spotPriceStr;
    private double amount;

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
        TextInputEditText cardNumber = view.findViewById(R.id.cardNumber);
        TextInputEditText expiryMonth = view.findViewById(R.id.expiryMonth);
        TextInputEditText expiryYear = view.findViewById(R.id.expiryYearEdit);
        TextInputEditText cvv = view.findViewById(R.id.cvv);
        TextInputEditText cardHolder = view.findViewById(R.id.cardHolder);
        Button payButton = view.findViewById(R.id.payButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        TextView paymentAmount = view.findViewById(R.id.paymentAmount);
        TextView parkingInfo = view.findViewById(R.id.parkingInfo);

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
            if (validateCard(cardNumber, expiryMonth, expiryYear, cvv, cardHolder)) {
                saveUserDataToDatabase();         // Αποθήκευση στοιχείων χρήστη
                saveParkingHistoryToDatabase();   // Καταγραφή ιστορικού στάθμευσης
                updateWallet();                   // Μείωση υπολοίπου χρήστη

                Toast.makeText(getContext(), "Πληρωμή επιτυχής!", Toast.LENGTH_SHORT).show();

                // Επιστροφή στον χάρτη
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new MapFragment())
                        .commit();
            }
        });

        // Ακύρωση πληρωμής → επιστροφή στο StopParkingFragment
        cancelButton.setOnClickListener(v -> goBackToStopFragment());
    }

    // Έλεγχος αν έχουν συμπληρωθεί όλα τα στοιχεία της κάρτας
    private boolean validateCard(TextInputEditText cardNumber, TextInputEditText expiryMonth,
                                 TextInputEditText expiryYear, TextInputEditText cvv,
                                 TextInputEditText cardHolder) {
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

    // Αποθήκευση των στοιχείων του χρήστη στη βάση (μέσω PHP API)
    private void saveUserDataToDatabase() {
        String url = "http://10.0.2.2/parking_app/save_user_data.php";

        try {
            if (email == null || sector == null || startTime == null) {
                System.out.println("❌ Null πεδία στο saveUserDataToDatabase");
                return;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("user_id", email);
            params.put("wallet_balance", 0.0);
            params.put("total_spent", amount);
            params.put("total_park_time", 1);
            params.put("last_sector", sector);
            params.put("last_park_time", startTime);

            JSONObject jsonObject = new JSONObject(params);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, url, jsonObject,
                    response -> {
                        System.out.println("✅ User data saved: " + response.toString());
                    },
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

    // Αποθήκευση ιστορικού στάθμευσης
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
                            System.out.println("⚠️ Server error response: " + responseBody);
                        }
                        Toast.makeText(getContext(), "❌ Σφάλμα ιστορικού στάθμευσης", Toast.LENGTH_SHORT).show();
                    });

            Volley.newRequestQueue(requireContext()).add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Μειώνει το υπόλοιπο στο πορτοφόλι του χρήστη
    private void updateWallet() {
        SharedPreferences prefs = requireContext().getSharedPreferences("WalletPrefs", Context.MODE_PRIVATE);
        float currentBalance = prefs.getFloat("wallet_balance", 0);  // ⚠️ χρησιμοποιείται float!
        float newBalance = (float) (currentBalance - amount);
        prefs.edit().putFloat("wallet_balance", newBalance).apply();  // Ενημερώνει το υπόλοιπο
    }

    // Επιστροφή στο StopParkingFragment με τα αρχικά δεδομένα
    private void goBackToStopFragment() {
        StopParkingFragment stopParkingFragment = StopParkingFragment.newInstance(
                sector, address, startTime, plate, email, spotPriceStr, true, amount);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, stopParkingFragment)
                .commit();
    }

    // Όταν ο χρήστης πατήσει το "πίσω" κουμπί στο toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            goBackToStopFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
