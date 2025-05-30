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

    private String sector, address, startTime, plate, email;
    private double pricePerHour;
    private TextView paymentAmount;
    private TextView walletBalanceView;
    private TextView finishInstruction;
    private double totalCost;
    private double walletBalance;
    private boolean isPaymentPhase = false;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stop_parking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            sector = getArguments().getString("sector");
            address = getArguments().getString("address");
            startTime = getArguments().getString("start_time");
            plate = getArguments().getString("plate");
            email = getArguments().getString("email");
            isPaymentPhase = getArguments().getBoolean("payment_phase", false);
            String spotPriceStr = getArguments().getString("spot_price");
            pricePerHour = Double.parseDouble(spotPriceStr != null ? spotPriceStr : "0");

            if (isPaymentPhase) {
                totalCost = getArguments().getDouble("total_cost", 0.0);
                showPaymentOptionsUI(view);
            } else {
                showBasicUI(view);
            }
        }

        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(isPaymentPhase);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setTitle("Ολοκλήρωση Στάθμευσης");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

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

        textSector.setText("Θέση: " + sector);
        textAddress.setText("Διεύθυνση: " + address);
        textStartTime.setText("Ώρα Έναρξης: " + startTime);
        textPlate.setText("Πινακίδα: " + plate);
        textEmail.setText("Email: " + email);

        paymentAmount.setVisibility(View.GONE);
        walletBalanceView.setVisibility(View.GONE);
        payWithCard.setVisibility(View.GONE);
        payWithWallet.setVisibility(View.GONE);
        finishButton.setVisibility(View.VISIBLE);

        finishButton.setOnClickListener(v -> {
            finishInstruction.setVisibility(View.GONE);
            String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            totalCost = calculateCost(startTime, endTime, pricePerHour);
            showPaymentOptionsUI(view);
        });
    }

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

        textSector.setText("Θέση: " + sector);
        textAddress.setText("Διεύθυνση: " + address);
        textStartTime.setText("Ώρα Έναρξης: " + startTime);
        textPlate.setText("Πινακίδα: " + plate);
        textEmail.setText("Email: " + email);

        finishButton.setVisibility(View.GONE);
        payWithCard.setVisibility(View.VISIBLE);
        payWithWallet.setVisibility(View.VISIBLE);
        paymentAmount.setVisibility(View.VISIBLE);
        paymentAmount.setText(String.format("Ποσό Πληρωμής: %.2f €", totalCost));

        walletBalance = getWalletBalance();
        walletBalanceView.setText(String.format("Υπόλοιπο Wallet: %.2f €", walletBalance));
        walletBalanceView.setVisibility(View.VISIBLE);

        payWithCard.setOnClickListener(v -> {
            PaymentFragment paymentFragment = PaymentFragment.newInstance(
                    sector, address, startTime, plate, email, totalCost, String.valueOf(pricePerHour));
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, paymentFragment)
                    .addToBackStack("stop_parking")
                    .commit();
        });

        payWithWallet.setOnClickListener(v -> {
            if (walletBalance >= totalCost) {
                setWalletBalance(walletBalance - totalCost);
                Toast.makeText(getContext(), "Πληρωμή με wallet ολοκληρώθηκε επιτυχώς!", Toast.LENGTH_LONG).show();
                payWithCard.setEnabled(false);
                payWithWallet.setEnabled(false);
                sendParkingDataToServer();
                sendUserDataToServer();
                requireActivity().getSupportFragmentManager()
                        .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } else {
                Toast.makeText(getContext(), "Ανεπαρκές υπόλοιπο στο wallet.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private double getWalletBalance() {
        SharedPreferences prefs = requireContext().getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE);
        return prefs.getFloat("balance", 0f);
    }

    private void setWalletBalance(double newBalance) {
        SharedPreferences prefs = requireContext().getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE);
        prefs.edit().putFloat("balance", (float) newBalance).apply();
    }

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

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    response -> System.out.println("✅ [WALLET] Parking saved: " + response.toString()),
                    error -> error.printStackTrace());

            Volley.newRequestQueue(requireContext()).add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendUserDataToServer() {
        try {
            String url = "http://10.0.2.2/parking_app/save_user_data.php";

            Map<String, Object> params = new HashMap<>();
            params.put("user_id", email);
            params.put("wallet_balance", walletBalance);
            params.put("total_spent", totalCost);
            params.put("total_park_time", 1);
            params.put("last_sector", sector);
            params.put("last_park_time", startTime);

            JSONObject jsonObject = new JSONObject(params);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    response -> System.out.println("✅ [WALLET] User data saved: " + response.toString()),
                    error -> error.printStackTrace());

            Volley.newRequestQueue(requireContext()).add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
