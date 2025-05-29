package com.example.ergasiaandroid.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;

import com.example.ergasiaandroid.R;

public class WalletFragment extends Fragment {

    private TextView balanceText;

    public WalletFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true); // Σημαντικό για να "πιάνει" το βελάκι!
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        balanceText = view.findViewById(R.id.balance_amount);
        Button btn10 = view.findViewById(R.id.btn_10);
        Button btn20 = view.findViewById(R.id.btn_20);
        Button btn50 = view.findViewById(R.id.btn_50);

        updateBalance();

        btn10.setOnClickListener(v -> goToPayment(10));
        btn20.setOnClickListener(v -> goToPayment(20));
        btn50.setOnClickListener(v -> goToPayment(50));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBalance();
        // Εμφάνιση βελακίου επιστροφής όταν είμαστε εδώ
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setTitle("Το Wallet μου");
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Απόκρυψη βελακίου αν φύγεις από το fragment
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    // Εδώ διαχειρίζεσαι το "πάτημα" του βελακίου
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Πήγαινε πίσω στο MapFragment (ή όπου ήταν πριν το wallet)
            requireActivity().getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateBalance() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE);
        float balance = prefs.getFloat("balance", 0f);
        if (balanceText != null) {
            balanceText.setText("€ " + String.format("%.2f", balance));
        }
    }

    private void goToPayment(int amount) {
        Bundle bundle = new Bundle();
        bundle.putInt("amount", amount);

        PaymentFragment2 paymentFragment2 = new PaymentFragment2();
        paymentFragment2.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, paymentFragment2)
                .addToBackStack(null)
                .commit();
    }
}
