package com.example.ergasiaandroid.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
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
import androidx.fragment.app.FragmentManager;

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
        if (totalCost != null) {
            args.putDouble("total_cost", totalCost);
        }
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
            isPaymentPhase = getArguments().getBoolean("payment_phase", false);

            String spotPriceStr = getArguments().getString("spot_price");
            if (spotPriceStr == null || spotPriceStr.isEmpty()) {
                throw new IllegalArgumentException("Το κόστος θέσης στάθμευσης (spot_price) πρέπει να δοθεί.");
            }
            pricePerHour = Double.parseDouble(spotPriceStr);

            if (isPaymentPhase) {
                totalCost = getArguments().getDouble("total_cost", 0.0);
                showPaymentOptionsUI(view);
                return;
            }
        }

        // Εμφάνιση στοιχείων χωρίς payment options (αρχική φάση)
        showBasicUI(view);
    }

    private void showBasicUI(View view) {
        TextView textSector = view.findViewById(R.id.text_sector);
        TextView textAddress = view.findViewById(R.id.text_address);
        TextView textStartTime = view.findViewById(R.id.text_start_time);
        TextView textPlate = view.findViewById(R.id.text_plate);
        TextView textEmail = view.findViewById(R.id.text_email);
        TextView finishInstruction = view.findViewById(R.id.finish_instruction);

        Button finishButton = view.findViewById(R.id.button_finish);
        Button payWithCard = view.findViewById(R.id.button_pay_with_card);
        Button payWithWallet = view.findViewById(R.id.button_pay_with_wallet);
        paymentAmount = view.findViewById(R.id.text_payment_amount);
        walletBalanceView = view.findViewById(R.id.text_wallet_balance);

        // Εμφάνιση με bold label και κανονικό value
        textSector.setText(makeStyledLabelValue("Θέση: ", sector));
        textAddress.setText(makeStyledLabelValue("Διεύθυνση: ", address));
        textStartTime.setText(makeStyledLabelValue("Ώρα Έναρξης: ", startTime));
        textPlate.setText(makeStyledLabelValue("Πινακίδα: ", plate));
        textEmail.setText(makeStyledLabelValue("Email: ", email));

        // Αρχικά κρυφά
        paymentAmount.setVisibility(View.GONE);
        walletBalanceView.setVisibility(View.GONE);
        payWithCard.setVisibility(View.GONE);
        payWithWallet.setVisibility(View.GONE);
        finishButton.setVisibility(View.VISIBLE);
        finishInstruction.setVisibility(View.VISIBLE);

        finishButton.setOnClickListener(v -> {
            endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            totalCost = calculateCost(startTime, endTime, pricePerHour);
            finishInstruction.setVisibility(View.GONE); // Κρύβει την οδηγία μόλις πατήσεις το κουμπί
            showPaymentOptionsUI(view);
        });

        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle("Τέλος Στάθμευσης");
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    private void showPaymentOptionsUI(View view) {
        // Εμφάνιση ΠΑΝΤΑ ΟΛΩΝ των στοιχείων!
        TextView textSector = view.findViewById(R.id.text_sector);
        TextView textAddress = view.findViewById(R.id.text_address);
        TextView textStartTime = view.findViewById(R.id.text_start_time);
        TextView textPlate = view.findViewById(R.id.text_plate);
        TextView textEmail = view.findViewById(R.id.text_email);

        textSector.setText(makeStyledLabelValue("Θέση: ", sector));
        textAddress.setText(makeStyledLabelValue("Διεύθυνση: ", address));
        textStartTime.setText(makeStyledLabelValue("Ώρα Έναρξης: ", startTime));
        textPlate.setText(makeStyledLabelValue("Πινακίδα: ", plate));
        textEmail.setText(makeStyledLabelValue("Email: ", email));

        Button finishButton = view.findViewById(R.id.button_finish);
        Button payWithCard = view.findViewById(R.id.button_pay_with_card);
        Button payWithWallet = view.findViewById(R.id.button_pay_with_wallet);
        paymentAmount = view.findViewById(R.id.text_payment_amount);
        walletBalanceView = view.findViewById(R.id.text_wallet_balance);

        // Εμφανίζουμε ποσά και κουμπιά πληρωμής
        paymentAmount.setText(String.format("Ποσό Πληρωμής: %.2f €", totalCost));
        paymentAmount.setVisibility(View.VISIBLE);

        finishButton.setVisibility(View.GONE);
        payWithCard.setVisibility(View.VISIBLE);
        payWithWallet.setVisibility(View.VISIBLE);

        // Επανενεργοποιούμε πάντα τα κουμπιά
        payWithCard.setEnabled(true);
        payWithWallet.setEnabled(true);

        walletBalance = getWalletBalance();
        walletBalanceView.setText(String.format("Υπόλοιπο Wallet: %.2f €", walletBalance));
        walletBalanceView.setVisibility(View.VISIBLE);

        payWithCard.setOnClickListener(v -> {
            PaymentFragment paymentFragment = PaymentFragment.newInstance(
                    sector, address, startTime, plate, email, totalCost, String.valueOf(pricePerHour)
            );
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, paymentFragment)
                    .addToBackStack("stop_parking")
                    .commit();
        });

        payWithWallet.setOnClickListener(v -> {
            walletBalance = getWalletBalance();
            if (walletBalance >= totalCost) {
                setWalletBalance(walletBalance - totalCost);
                Toast.makeText(getContext(), "Πληρωμή με wallet ολοκληρώθηκε επιτυχώς!", Toast.LENGTH_LONG).show();
                payWithCard.setEnabled(false);
                payWithWallet.setEnabled(false);

                requireActivity().getSupportFragmentManager()
                        .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } else {
                Toast.makeText(getContext(), "Ανεπαρκές υπόλοιπο στο wallet.", Toast.LENGTH_LONG).show();
            }
        });

        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle("Τέλος Στάθμευσης");
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    // Βοηθητική μέθοδος για bold label και κανονικό value
    private SpannableString makeStyledLabelValue(String label, String value) {
        SpannableString spannable = new SpannableString(label + value);
        spannable.setSpan(
                new StyleSpan(android.graphics.Typeface.BOLD),
                0,
                label.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        return spannable;
    }

    private double getWalletBalance() {
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
            double hoursRoundedUp = Math.max(1, Math.ceil(hours));
            return hoursRoundedUp * costPerHour;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}
