package com.example.ergasiaandroid.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.content.SharedPreferences;
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

import com.example.ergasiaandroid.R;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StopParkingFragment extends Fragment {

    private String sector, address, startTime, plate, email;
    private double pricePerHour;
    private TextView paymentAmount;
    private TextView walletBalanceView;
    private String endTime;
    private double totalCost;
    private double walletBalance;

    // Περνάμε και address!
    public static StopParkingFragment newInstance(String sector, String address, String startTime, String plate, String email, String pricePerHour) {
        StopParkingFragment fragment = new StopParkingFragment();
        Bundle args = new Bundle();
        args.putString("sector", sector);
        args.putString("address", address);
        args.putString("start_time", startTime);
        args.putString("plate", plate);
        args.putString("email", email);
        args.putString("spot_price", pricePerHour);
        fragment.setArguments(args);
        return fragment;
    }

    public StopParkingFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
            String priceStr = getArguments().getString("spot_price", "1.5");
            try {
                pricePerHour = Double.parseDouble(priceStr);
            } catch (Exception e) {
                pricePerHour = 1.5;
            }
        }

        TextView textSector = view.findViewById(R.id.text_sector);
        TextView textAddress = view.findViewById(R.id.text_address);
        TextView textStartTime = view.findViewById(R.id.text_start_time);
        TextView textPlate = view.findViewById(R.id.text_plate);
        TextView textEmail = view.findViewById(R.id.text_email);
        paymentAmount = view.findViewById(R.id.text_payment_amount);
        Button finishButton = view.findViewById(R.id.button_finish);
        Button payWithCard = view.findViewById(R.id.button_pay_with_card);
        Button payWithWallet = view.findViewById(R.id.button_pay_with_wallet);

        walletBalanceView = view.findViewById(R.id.text_wallet_balance);
        walletBalanceView.setVisibility(View.GONE);

        payWithCard.setVisibility(View.GONE);
        payWithWallet.setVisibility(View.GONE);
        paymentAmount.setVisibility(View.GONE);

        textSector.setText("Θέση: " + sector);
        textAddress.setText("Διεύθυνση: " + address);
        textStartTime.setText("Ώρα Έναρξης: " + startTime);
        textPlate.setText("Πινακίδα: " + plate);
        textEmail.setText("Email: " + email);

        finishButton.setOnClickListener(v -> {
            endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            totalCost = calculateCost(startTime, endTime, pricePerHour);

            paymentAmount.setText(String.format("Ποσό Πληρωμής: %.2f €", totalCost));
            paymentAmount.setVisibility(View.VISIBLE);

            finishButton.setVisibility(View.GONE); // Κρύψε το "Ολοκλήρωση στάθμευσης"
            payWithCard.setVisibility(View.VISIBLE);
            payWithWallet.setVisibility(View.VISIBLE);

            // Εμφανίζουμε το υπόλοιπο wallet
            walletBalance = getWalletBalance();
            walletBalanceView.setText(String.format("Υπόλοιπο Wallet: %.2f €", walletBalance));
            walletBalanceView.setVisibility(View.VISIBLE);
        });

        payWithCard.setOnClickListener(v -> {
            // Μπορείς να προσθέσεις το ποσό στα arguments αν θέλεις
            PaymentFragment paymentFragment = PaymentFragment.newInstance(sector, startTime, plate, email /*, String.valueOf(totalCost)*/);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, paymentFragment)
                    .addToBackStack(null)
                    .commit();
        });

        payWithWallet.setOnClickListener(v -> {
            walletBalance = getWalletBalance();
            if (walletBalance >= totalCost) {
                setWalletBalance(walletBalance - totalCost);
                Toast.makeText(getContext(), "Πληρωμή με wallet ολοκληρώθηκε επιτυχώς!", Toast.LENGTH_LONG).show();
                // Ενημέρωση υπολοίπου στην οθόνη
                walletBalanceView.setText(String.format("Υπόλοιπο Wallet: %.2f €", walletBalance - totalCost));
                payWithCard.setEnabled(false);
                payWithWallet.setEnabled(false);
            } else {
                Toast.makeText(getContext(), "Ανεπαρκές υπόλοιπο στο wallet.", Toast.LENGTH_LONG).show();
            }
        });

        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle("Τέλος Στάθμευσης");
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private double getWalletBalance() {
        // Από SharedPreferences
        return requireActivity().getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE).getFloat("balance", 0f);
    }

    private void setWalletBalance(double newBalance) {
        requireActivity().getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)
                .edit().putFloat("balance", (float) newBalance).apply();
    }

    private double calculateCost(String startTimeStr, String endTimeStr, double costPerHour) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startTimeStr, formatter);
            LocalDateTime end = LocalDateTime.parse(endTimeStr, formatter);

            long seconds = Duration.between(start, end).getSeconds();
            double hours = seconds / 3600.0;
            double hoursRoundedUp = Math.max(1, Math.ceil(hours)); // ΠΑΝΤΑ τουλάχιστον 1 ώρα

            return hoursRoundedUp * costPerHour;
        } catch (Exception e) {
            e.printStackTrace();
            return costPerHour; // Αν αποτύχει, τουλάχιστον 1 ώρα
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getParentFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
