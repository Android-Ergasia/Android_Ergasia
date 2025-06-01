package com.example.ergasiaandroid.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ergasiaandroid.R;

import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class StopParkingFragment extends Fragment {

    // Μεταβλητές για δεδομένα θέσης στάθμευσης και χρήστη
    private String sector, address, startTime, plate, email;
    private double pricePerHour;
    private TextView paymentAmount;
    private TextView walletBalanceView;
    private TextView finishInstruction;
    private double totalCost;
    private double walletBalance;
    private boolean isPaymentPhase = false;

    // Δημιουργεί νέο instance του Fragment με δεδομένα (μέθοδος factory)
    public static StopParkingFragment newInstance(String sector, String address, String startTime,
                                                  String plate, String email, String spotPrice,
                                                  boolean paymentPhase, Double totalCost) {
        StopParkingFragment fragment = new StopParkingFragment();
        Bundle args = new Bundle();
        args.putString("sector", sector);
        args.putString("address", address);
        args.putString("start_time", startTime);
        args.putString("plate", plate);
        args.putString("email", email);
        args.putString("spot_price", spotPrice);
        args.putBoolean("payment_phase", paymentPhase);
        if (totalCost != null) args.putDouble("total_cost", totalCost);
        fragment.setArguments(args);
        return fragment;
    }

    public StopParkingFragment() {}

    // Φόρτωση layout του fragment
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stop_parking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Αν έχουν δοθεί ορίσματα, τα αποθηκεύει
        if (getArguments() != null) {
            sector = getArguments().getString("sector");
            address = getArguments().getString("address");
            startTime = getArguments().getString("start_time");
            plate = getArguments().getString("plate");
            email = getArguments().getString("email");
            isPaymentPhase = getArguments().getBoolean("payment_phase", false);
            String spotPriceStr = getArguments().getString("spot_price");
            pricePerHour = Double.parseDouble(spotPriceStr != null ? spotPriceStr : "0");

            // Αν είμαστε σε φάση πληρωμής, δείξε την αντίστοιχη διεπαφή
            if (isPaymentPhase) {
                totalCost = getArguments().getDouble("total_cost", 0.0);
                showPaymentOptionsUI(view);
            } else {
                showBasicUI(view);
            }
        }

        //Δείχνει το back button στο toolbar μόνο όταν ο χρήστης είναι στη φάση πληρωμής
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(isPaymentPhase);
        }
    }

    // Ορίζει τον τίτλο του activity
    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setTitle("Ολοκλήρωση Στάθμευσης");
    }

    // Απενεργοποιεί το back button όταν φεύγει το view
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    // Αν πατηθεί το back button (στο toolbar) και είμαστε στη φάση πληρωμής, γυρνάμε πίσω
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home && isPaymentPhase) {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new StartParkingFragment())
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Εμφάνιση βασικών στοιχείων πριν την πληρωμή
    private void showBasicUI(View view) {
        TextView textSector = view.findViewById(R.id.text_sector);
        TextView textAddress = view.findViewById(R.id.text_address);
        TextView textStartTime = view.findViewById(R.id.text_start_time);
        TextView textPlate = view.findViewById(R.id.text_plate);
        TextView textEmail = view.findViewById(R.id.text_email);
        Button finishButton = view.findViewById(R.id.button_finish);
        Button payWithCard = view.findViewById(R.id.button_pay_with_card);
        Button payWithWallet = view.findViewById(R.id.button_pay_with_wallet);
        paymentAmount = view.findViewById(R.id.text_payment_amount);
        walletBalanceView = view.findViewById(R.id.text_wallet_balance);
        finishInstruction = view.findViewById(R.id.finish_instruction);

        // Εμφάνιση στοιχείων χρήστη/στάθμευσης
        textSector.setText("Θέση: " + sector);
        textAddress.setText("Διεύθυνση: " + address);
        textStartTime.setText("Ώρα Έναρξης: " + startTime);
        textPlate.setText("Πινακίδα: " + plate);
        textEmail.setText("Email: " + email);

        // Κρύβει τα κουμπιά πληρωμής, δείχνει μόνο το "Ολοκλήρωση στάθμευσης"
        paymentAmount.setVisibility(View.GONE);
        walletBalanceView.setVisibility(View.GONE);
        payWithCard.setVisibility(View.GONE);
        payWithWallet.setVisibility(View.GONE);
        finishButton.setVisibility(View.VISIBLE);

        // Όταν πατηθεί το κουμπί "Ολοκλήρωση στάθμευσης", υπολογίζει το κόστος και δείχνει επιλογές πληρωμής
        finishButton.setOnClickListener(v -> {
            finishInstruction.setVisibility(View.GONE);
            String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            totalCost = calculateCost(startTime, endTime, pricePerHour);
            showPaymentOptionsUI(view);
        });
    }

    // Εμφάνιση UI πληρωμής
    private void showPaymentOptionsUI(View view) {
        TextView textSector = view.findViewById(R.id.text_sector);
        TextView textAddress = view.findViewById(R.id.text_address);
        TextView textStartTime = view.findViewById(R.id.text_start_time);
        TextView textPlate = view.findViewById(R.id.text_plate);
        TextView textEmail = view.findViewById(R.id.text_email);
        Button finishButton = view.findViewById(R.id.button_finish);
        Button payWithCard = view.findViewById(R.id.button_pay_with_card);
        Button payWithWallet = view.findViewById(R.id.button_pay_with_wallet);
        paymentAmount = view.findViewById(R.id.text_payment_amount);
        walletBalanceView = view.findViewById(R.id.text_wallet_balance);

        // Επαναπροβολή στοιχείων
        textSector.setText("Θέση: " + sector);
        textAddress.setText("Διεύθυνση: " + address);
        textStartTime.setText("Ώρα Έναρξης: " + startTime);
        textPlate.setText("Πινακίδα: " + plate);
        textEmail.setText("Email: " + email);

        // Εμφάνιση κουμπιών πληρωμής και υπολοίπου
        finishButton.setVisibility(View.GONE);
        payWithCard.setVisibility(View.VISIBLE);
        payWithWallet.setVisibility(View.VISIBLE);
        paymentAmount.setVisibility(View.VISIBLE);
        paymentAmount.setText(String.format("Ποσό Πληρωμής: %.2f €", totalCost));

        // Εμφάνιση υπολοίπου wallet
        walletBalance = getWalletBalance();
        walletBalanceView.setText(String.format("Υπόλοιπο Wallet: %.2f €", walletBalance));
        walletBalanceView.setVisibility(View.VISIBLE);

        // Πληρωμή με κάρτα → Μεταφορά σε PaymentFragment
        payWithCard.setOnClickListener(v -> {
            PaymentFragment paymentFragment = PaymentFragment.newInstance(
                    sector, address, startTime, plate, email, totalCost, String.valueOf(pricePerHour));
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, paymentFragment)
                    .addToBackStack("stop_parking")
                    .commit();
        });

        // Πληρωμή με wallet
        payWithWallet.setOnClickListener(v -> {
            if (walletBalance >= totalCost) {
                // Αποθήκευση email
                SharedPreferences prefsUser = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                prefsUser.edit().putString("user_email", email != null ? email : "").apply();

                // Αφαίρεση ποσού από wallet
                double newBalance = walletBalance - totalCost;
                setWalletBalance(newBalance);
                walletBalance = newBalance;

                Toast.makeText(getContext(), "Πληρωμή με wallet ολοκληρώθηκε επιτυχώς!", Toast.LENGTH_LONG).show();
                payWithCard.setEnabled(false);
                payWithWallet.setEnabled(false);

                // Αποστολή δεδομένων στον server
                sendParkingDataToServer();
                sendUserDataToServer();

                // Καθαρισμός του back stack
                requireActivity().getSupportFragmentManager()
                        .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } else {
                Toast.makeText(getContext(), "Ανεπαρκές υπόλοιπο στο wallet.", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Επιστρέφει το υπόλοιπο
    private double getWalletBalance() {
        SharedPreferences prefs = requireContext().getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE);
        return prefs.getFloat("balance", 0f);
    }

    // Ενημερώνει το υπόλοιπο
    private void setWalletBalance(double newBalance) {
        SharedPreferences prefs = requireContext().getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE);
        prefs.edit().putFloat("balance", (float) newBalance).apply();
    }

    // Υπολογισμός κόστους στάθμευσης
    private double calculateCost(String startStr, String endStr, double costPerHour) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startStr, fmt);
            LocalDateTime end = LocalDateTime.parse(endStr, fmt);
            long seconds = Duration.between(start, end).getSeconds();
            double hours = seconds / 3600.0;
            return Math.max(1, Math.ceil(hours)) * costPerHour;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // Αποστολή δεδομένων στάθμευσης στον server
    private void sendParkingDataToServer() {
        try {
            String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String url = "http://10.0.2.2/parking_app/insert_parking_history.php";

            Map<String, Object> params = new HashMap<>();
            params.put("user_id", email);
            params.put("spot", sector);
            params.put("start", startTime);
            params.put("end", endTime);
            params.put("rate", pricePerHour);
            params.put("amount", totalCost);

            JSONObject jsonObject = new JSONObject(params);
            System.out.println("📤 [DEBUG] Parking data: " + jsonObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    response -> System.out.println("✅ [WALLET] Parking saved: " + response.toString()),
                    error -> {
                        error.printStackTrace();
                        if (error.networkResponse != null) {
                            String responseBody = new String(error.networkResponse.data);
                            System.out.println("❌ [WALLET] Parking error: " + responseBody);
                        } else {
                            System.out.println("❌ [WALLET] Unknown parking error.");
                        }
                    });

            Volley.newRequestQueue(requireContext()).add(request);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ [WALLET] Exception during parking data send.");
        }
    }

    // Αποστολή δεδομένων χρήστη στον server
    private void sendUserDataToServer() {
        try {
            String url = "http://10.0.2.2/parking_app/save_user_data.php";

            Map<String, Object> params = new HashMap<>();
            params.put("user_id", email != null ? email : "");
            params.put("wallet_balance", walletBalance);
            params.put("total_spent", totalCost);
            params.put("total_park_time", 1);
            params.put("last_sector", sector != null ? sector : "");
            params.put("last_park_time", startTime != null ? startTime : "");

            JSONObject jsonObject = new JSONObject(params);
            System.out.println("📤 [DEBUG] Sending user stats: " + jsonObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    response -> System.out.println("✅ [WALLET] User data saved: " + response.toString()),
                    error -> {
                        error.printStackTrace();
                        if (error.networkResponse != null) {
                            String responseBody = new String(error.networkResponse.data);
                            System.out.println("❌ [WALLET] Server error: " + responseBody);
                        } else {
                            System.out.println("❌ [WALLET] Unknown network error");
                        }
                    });

            Volley.newRequestQueue(requireContext()).add(request);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ [WALLET] Exception during user data send");
        }
    }
}
