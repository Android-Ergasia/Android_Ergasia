package com.example.ergasiaandroid.Fragments;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
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

import com.example.ergasiaandroid.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class PaymentFragment extends Fragment {

    private String sector, address, startTime, plate, email, spotPriceStr;
    private double amount;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
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
            amount = getArguments().getDouble("amount", 0.0);
            spotPriceStr = getArguments().getString("spot_price");
        }

        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle("Πληρωμή στάθμευσης");
            setHasOptionsMenu(true);
        }

        // Πεδία φόρμας
        TextInputEditText cardNumber = view.findViewById(R.id.cardNumber);
        TextInputEditText expiryMonth = view.findViewById(R.id.expiryMonth);
        TextInputEditText expiryYear = view.findViewById(R.id.expiryYearEdit);
        TextInputEditText cvv = view.findViewById(R.id.cvv);
        TextInputEditText cardHolder = view.findViewById(R.id.cardHolder);
        Button payButton = view.findViewById(R.id.payButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        TextView paymentAmount = view.findViewById(R.id.paymentAmount);

        // Κάρτα πληροφοριών (όπως στο StopParkingFragment)
        TextView textSector = view.findViewById(R.id.text_sector);
        TextView textAddress = view.findViewById(R.id.text_address);
        TextView textStartTime = view.findViewById(R.id.text_start_time);
        TextView textPlate = view.findViewById(R.id.text_plate);
        TextView textEmail = view.findViewById(R.id.text_email);

        textSector.setText(makeStyledLabelValue("Θέση: ", sector));
        textAddress.setText(makeStyledLabelValue("Διεύθυνση: ", address));
        textStartTime.setText(makeStyledLabelValue("Ώρα έναρξης: ", startTime));
        textPlate.setText(makeStyledLabelValue("Πινακίδα: ", plate));
        textEmail.setText(makeStyledLabelValue("Email: ", email));

        String costText = String.format(Locale.getDefault(), "%.2f €", amount);
        paymentAmount.setText("Πληρωτέο Ποσό: " + costText);

        payButton.setOnClickListener(v -> {
            if (validateCard(cardNumber, expiryMonth, expiryYear, cvv, cardHolder)) {
                Toast.makeText(getContext(), "Πληρωμή επιτυχής!", Toast.LENGTH_SHORT).show();

                // Επιστροφή στον χάρτη
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new MapFragment())
                        .commit();
            }
        });


        cancelButton.setOnClickListener(v -> {
            // Επιστροφή στο StopParkingFragment με τα σωστά δεδομένα
            StopParkingFragment stopParkingFragment = StopParkingFragment.newInstance(
                    sector, address, startTime, plate, email,
                    spotPriceStr, true, amount);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, stopParkingFragment)
                    .commit();
        });
    }

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Επιστροφή στο StopParkingFragment με τις παραμέτρους και τη σωστή τιμή
            StopParkingFragment stopParkingFragment = StopParkingFragment.newInstance(
                    sector, address, startTime, plate, email,
                    spotPriceStr, true, amount);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, stopParkingFragment)
                    .commit();

            return true;
        }
        return super.onOptionsItemSelected(item);
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
}
