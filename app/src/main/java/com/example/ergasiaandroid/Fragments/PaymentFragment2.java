package com.example.ergasiaandroid.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.ergasiaandroid.R;

public class PaymentFragment2 extends Fragment {

    private int amount;

    public PaymentFragment2() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment2, container, false);

        TextView amountText = view.findViewById(R.id.amount_to_pay);
        EditText cardNumber = view.findViewById(R.id.card_number);
        EditText cardExpiry = view.findViewById(R.id.card_expiry);
        EditText cardCvv = view.findViewById(R.id.card_cvv);
        Button payButton = view.findViewById(R.id.pay_button);

        if (getArguments() != null) {
            amount = getArguments().getInt("amount", 0);
            amountText.setText("Ποσό: €" + amount);
        }

        payButton.setOnClickListener(v -> {
            // Προσποίηση πληρωμής - δεν κάνουμε έλεγχο
            saveNewBalance(amount);
            Toast.makeText(getActivity(), "Το ποσό προστέθηκε στο υπόλοιπο!", Toast.LENGTH_SHORT).show();

            // Επιστροφή στο WalletFragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    private void saveNewBalance(int addedAmount) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE);
        float currentBalance = prefs.getFloat("balance", 0f);
        prefs.edit().putFloat("balance", currentBalance + addedAmount).apply();
    }
}