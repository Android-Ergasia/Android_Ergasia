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
        // Επιτρέπει την προβολή του back κουμπιού στο ActionBar
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        // Αρχικοποίηση στοιχείων διεπαφής
        balanceText = view.findViewById(R.id.balance_amount);
        Button btn10 = view.findViewById(R.id.btn_10);
        Button btn20 = view.findViewById(R.id.btn_20);
        Button btn50 = view.findViewById(R.id.btn_50);

        // Ενημέρωση υπολοίπου με την τελευταία αποθηκευμένη τιμή
        updateBalance();

        // Ρυθμίσεις για κάθε κουμπί ώστε να ανοίγει το fragment πληρωμής με το αντίστοιχο ποσό
        btn10.setOnClickListener(v -> goToPayment(10));
        btn20.setOnClickListener(v -> goToPayment(20));
        btn50.setOnClickListener(v -> goToPayment(50));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBalance();

        // Εμφάνιση βελάκι και τίτλος στο ActionBar
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

        // Απόκρυψη βελακίου όταν το fragment δεν είναι πλέον ενεργό
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    // Διαχείριση του πατήματος του βελακίου επιστροφής
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Επιστροφή στο προηγούμενο fragment (π.χ. MapFragment)
            requireActivity().getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Διαβάζει το υπόλοιπο από τα SharedPreferences και το εμφανίζει
    private void updateBalance() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE);
        float balance = prefs.getFloat("balance", 0f); // Αν δεν υπάρχει, default είναι 0

        if (balanceText != null) {
            balanceText.setText("€ " + String.format("%.2f", balance));
        }
    }

    // Εκκίνηση του PaymentFragment2 με ποσό προς κατάθεση
    private void goToPayment(int amount) {
        Bundle bundle = new Bundle();
        bundle.putInt("amount", amount);

        PaymentFragment2 paymentFragment2 = new PaymentFragment2();
        paymentFragment2.setArguments(bundle);

        // Εναλλαγή στο fragment πληρωμής με δυνατότητα επιστροφής
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, paymentFragment2)
                .addToBackStack(null)
                .commit();
    }
}
