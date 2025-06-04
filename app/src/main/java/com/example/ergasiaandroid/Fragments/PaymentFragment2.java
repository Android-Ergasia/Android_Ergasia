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
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentFragment2 extends Fragment {

    // Ποσό προς προσθήκη στο υπόλοιπο
    private int amount;

    // TextInputEditText πεδίων κάρτας
    private TextInputEditText cardNumber, expiryMonth, expiryYear, cardCvv, cardHolder;
    private Button payButton;

    public PaymentFragment2() {}

    // Δημιουργία της διεπαφής του fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true); // Ενεργοποίηση του μενού για υποστήριξη κουμπιού back
        return inflater.inflate(R.layout.fragment_payment2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ανάκτηση των πεδίων εισόδου και του κουμπιού πληρωμής από το layout
        cardNumber   = view.findViewById(R.id.card_number);
        expiryMonth  = view.findViewById(R.id.expiryMonth);
        expiryYear   = view.findViewById(R.id.expiryYearEdit);
        cardCvv      = view.findViewById(R.id.card_cvv);
        cardHolder   = view.findViewById(R.id.cardHolder);
        payButton    = view.findViewById(R.id.pay_button);

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

            // Αποθήκευση στοιχείων κάρτας στη βάση
            saveCardDataToDatabase();

            // Εμφάνιση μηνύματος επιτυχίας
            Toast.makeText(getActivity(), "Το ποσό προστέθηκε στο υπόλοιπο!", Toast.LENGTH_SHORT).show();

            // Επιστροφή στο προηγούμενο fragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });
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

    // Αποθήκευση στοιχείων κάρτας στη βάση μέσω PHP
    private void saveCardDataToDatabase() {
        String url = "http://10.0.2.2/parking_app/save_card_data.php";

        try {
            // Διαβάζουμε το email του χρήστη από τα UserPrefs
            SharedPreferences prefsUser = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String userEmail = prefsUser.getString("user_email", "");

            Map<String, Object> params = new HashMap<>();
            params.put("user_id",     userEmail);
            params.put("card_number", cardNumber.getText().toString().trim());
            params.put("expiry_month", expiryMonth.getText().toString().trim());
            params.put("expiry_year",  expiryYear.getText().toString().trim());
            params.put("cvv",          cardCvv.getText().toString().trim());
            params.put("card_holder",  cardHolder.getText().toString().trim());

            JSONObject jsonObject = new JSONObject(params);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, url, jsonObject,
                    response -> System.out.println("✅ Card saved (wallet): " + response.toString()),
                    error -> {
                        error.printStackTrace();
                        if (error.networkResponse != null) {
                            String body = new String(error.networkResponse.data);
                            System.out.println("⚠️ CARD save error (wallet): " + body);
                        }
                        Toast.makeText(getContext(), "❌ Σφάλμα αποθήκευσης κάρτας", Toast.LENGTH_SHORT).show();
                    });

            Volley.newRequestQueue(requireContext()).add(request);
        } catch (Exception e) {
            e.printStackTrace();
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
}
