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

    // Ποσό προς προσθήκη στο υπόλοιπο
    private int amount;

    public PaymentFragment2() {}

    // Δημιουργία της διεπαφής του fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true); // Ενεργοποίηση του μενού για υποστήριξη κουμπιού back

        View view = inflater.inflate(R.layout.fragment_payment2, container, false);

        // Ανάκτηση των πεδίων εισόδου και του κουμπιού πληρωμής από το layout
        TextInputEditText cardNumber = view.findViewById(R.id.card_number);
        TextInputEditText expiryMonth = view.findViewById(R.id.expiryMonth);
        TextInputEditText expiryYear = view.findViewById(R.id.expiryYearEdit);
        TextInputEditText cardCvv = view.findViewById(R.id.card_cvv);
        TextInputEditText cardHolder = view.findViewById(R.id.cardHolder);
        Button payButton = view.findViewById(R.id.pay_button);

        // Λήψη του ποσού από τα arguments του fragment
        if (getArguments() != null) {
            amount = getArguments().getInt("amount", 0);
            String text = String.format("Ποσό: %.2f €", (float) amount);
            ((TextView) view.findViewById(R.id.amount_to_pay)).setText(text);
        }

        // Ρύθμιση του ActionBar με τίτλο και back button
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle("Κατάθεση Ποσού");
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        // Ενέργεια στο πάτημα του κουμπιού πληρωμής
        payButton.setOnClickListener(v -> {
            // Έλεγχος εγκυρότητας των πεδίων
            if (!validate(cardNumber, expiryMonth, expiryYear, cardCvv, cardHolder)) return;

            // Αποθήκευση του νέου υπολοίπου
            saveNewBalance(amount);

            // Εμφάνιση μηνύματος επιτυχίας
            Toast.makeText(getActivity(), "Το ποσό προστέθηκε στο υπόλοιπο!", Toast.LENGTH_SHORT).show();

            // Επιστροφή στο προηγούμενο fragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    // Ρύθμιση για το back button του ActionBar όταν επανέρχεται το fragment
    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
    }

    // Καθαρισμός του back κουμπιού όταν καταστρέφεται το view του fragment
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    // Διαχείριση του πατήματος του back κουμπιού στο ActionBar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Έλεγχος εγκυρότητας των στοιχείων κάρτας
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

    // Ενημέρωση του υπολοίπου πορτοφολιού
    private void saveNewBalance(int addedAmount) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE);
        float currentBalance = prefs.getFloat("balance", 0f);
        prefs.edit().putFloat("balance", currentBalance + addedAmount).apply();
    }
}
