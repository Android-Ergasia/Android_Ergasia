package com.example.ergasiaandroid.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
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

import com.example.ergasiaandroid.R;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class PaymentFragment extends Fragment {

    private String sector, startTime, plate, email;

    public static PaymentFragment newInstance(String sector, String startTime, String plate, String email) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();
        args.putString("sector", sector);
        args.putString("start_time", startTime);
        args.putString("plate", plate);
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    public PaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Λήψη τιμών από arguments
        if (getArguments() != null) {
            sector = getArguments().getString("sector");
            startTime = getArguments().getString("start_time");
            plate = getArguments().getString("plate");
            email = getArguments().getString("email");
        }

        // Τίτλος ActionBar
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Πληρωμή στάθμευσης");
        }

        // View references
        TextInputEditText cardNumber = view.findViewById(R.id.cardNumber);
        TextInputEditText expiryMonth = view.findViewById(R.id.expiryMonth);
        TextInputEditText expiryYear = view.findViewById(R.id.expiryYearEdit);
        TextInputEditText cvv = view.findViewById(R.id.cvv);
        TextInputEditText cardHolder = view.findViewById(R.id.cardHolder);
        Button payButton = view.findViewById(R.id.payButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        TextView paymentAmount = view.findViewById(R.id.paymentAmount);
        TextView parkingInfo = view.findViewById(R.id.parkingInfo);

        // Υπολογισμός πληρωμής

        String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        double cost = calculateCost(startTime, endTime, 1.5); // π.χ. 1.5€/ώρα

        String costText = getString(R.string.amount) + "\n" + String.format(Locale.getDefault(), "%.2f €", cost);
        paymentAmount.setText(costText);
        paymentAmount.setGravity(Gravity.CENTER);

        String info = "Πινακίδα: " + plate + "\nΤομέας: " + sector + "\nEmail: " + email + "\nΏρα έναρξης: " + startTime;
        parkingInfo.setText(info);
        parkingInfo.setGravity(Gravity.CENTER);

        payButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(cardNumber.getText()) ||
                    TextUtils.isEmpty(expiryMonth.getText()) ||
                    TextUtils.isEmpty(expiryYear.getText()) ||
                    TextUtils.isEmpty(cvv.getText()) ||
                    TextUtils.isEmpty(cardHolder.getText())) {

                Toast.makeText(getContext(), "Συμπληρώστε όλα τα πεδία!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Η πληρωμή ολοκληρώθηκε!", Toast.LENGTH_LONG).show();
                // Εδώ μπορείς να προσθέσεις αποθήκευση/αποστολή δεδομένων
            }
        });

        cancelButton.setOnClickListener(v -> {
            cardNumber.setText("");
            expiryMonth.setText("");
            expiryYear.setText("");
            cvv.setText("");
            cardHolder.setText("");
        });
    }

    private double calculateCost(String startTimeStr, String endTimeStr, double costPerHour) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startTimeStr, formatter);
            LocalDateTime end = LocalDateTime.parse(endTimeStr, formatter);

            double hours = Duration.between(start, end).toMinutes() / 60.0;
            return hours * costPerHour;

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Σφάλμα υπολογισμού κόστους: λάθος ώρα έναρξης", Toast.LENGTH_LONG).show();
            return 0;
        }
    }

}
