package com.example.ergasiaandroid.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ergasiaandroid.R;
import com.google.android.material.textfield.TextInputEditText;

public class PaymentFragment2 extends Fragment {

    private int amount;

    public PaymentFragment2() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true); // Σημαντικό για να πιάσουμε το back button
        View view = inflater.inflate(R.layout.fragment_payment2, container, false);

        TextInputEditText cardNumber = view.findViewById(R.id.card_number);
        TextInputEditText expiryMonth = view.findViewById(R.id.expiryMonth);
        TextInputEditText expiryYear = view.findViewById(R.id.expiryYearEdit);
        TextInputEditText cardCvv = view.findViewById(R.id.card_cvv);
        TextInputEditText cardHolder = view.findViewById(R.id.cardHolder);
        Button payButton = view.findViewById(R.id.pay_button);

        if (getArguments() != null) {
            amount = getArguments().getInt("amount", 0);
            String text = String.format("Ποσό: %.2f €", (float) amount);
            ((TextView) view.findViewById(R.id.amount_to_pay)).setText(text);
        }

        // Show ActionBar back arrow
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle("Κατάθεση Ποσού");
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        payButton.setOnClickListener(v -> {
            if (!validate(cardNumber, expiryMonth, expiryYear, cardCvv, cardHolder)) return;
            saveNewBalance(amount);
            Toast.makeText(getActivity(), "Το ποσό προστέθηκε στο υπόλοιπο!", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    // Πιάσε το back button του ActionBar
    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Επαναφέρε το βελάκι αν πρέπει (προαιρετικό)
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validate(TextInputEditText cardNumber, TextInputEditText expiryMonth,
                             TextInputEditText expiryYear, TextInputEditText cardCvv,
                             TextInputEditText cardHolder) {
        if (TextUtils.isEmpty(cardNumber.getText())) {
            cardNumber.setError("Απαιτείται αριθμός κάρτας");
            return false;
        }
        if (TextUtils.isEmpty(expiryMonth.getText())) {
            expiryMonth.setError("Απαιτείται μήνας");
            return false;
        }
        if (TextUtils.isEmpty(expiryYear.getText())) {
            expiryYear.setError("Απαιτείται έτος");
            return false;
        }
        if (TextUtils.isEmpty(cardCvv.getText())) {
            cardCvv.setError("Απαιτείται CVV");
            return false;
        }
        if (TextUtils.isEmpty(cardHolder.getText())) {
            cardHolder.setError("Απαιτείται όνομα κατόχου");
            return false;
        }
        return true;
    }

    private void saveNewBalance(int addedAmount) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE);
        float currentBalance = prefs.getFloat("balance", 0f);
        prefs.edit().putFloat("balance", currentBalance + addedAmount).apply();
    }
}
