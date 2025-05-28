package com.example.ergasiaandroid.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ergasiaandroid.R;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StopParkingFragment extends Fragment {

    private String sector, startTime, plate, email, price;
    private TextView paymentAmount;

    public static StopParkingFragment newInstance(String sector, String startTime, String plate, String email, String price) {
        StopParkingFragment fragment = new StopParkingFragment();
        Bundle args = new Bundle();
        args.putString("sector", sector);
        args.putString("start_time", startTime);
        args.putString("plate", plate);
        args.putString("email", email);
        args.putString("spot_price", price);
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
            startTime = getArguments().getString("start_time");
            plate = getArguments().getString("plate");
            email = getArguments().getString("email");
            price = getArguments().getString("spot_price");
        }

        TextView textSector = view.findViewById(R.id.text_sector);
        TextView textStartTime = view.findViewById(R.id.text_start_time);
        TextView textPlate = view.findViewById(R.id.text_plate);
        TextView textEmail = view.findViewById(R.id.text_email);
        paymentAmount = view.findViewById(R.id.text_payment_amount);
        Button finishButton = view.findViewById(R.id.button_finish);

        textSector.setText("Τομέας: " + sector);
        textStartTime.setText("Ώρα Έναρξης: " + startTime);
        textPlate.setText("Πινακίδα: " + plate);
        textEmail.setText("Email: " + email);

        String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        double costPerHour = 1.5; // default τιμή

        try {
            if (price != null) {
                costPerHour = Double.parseDouble(price);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        double cost = calculateCost(startTime, endTime, costPerHour);

        paymentAmount.setText(String.format("Ποσό Πληρωμής: %.2f €", cost));

        finishButton.setOnClickListener(v -> {
            PaymentFragment paymentFragment = PaymentFragment.newInstance(sector, startTime, plate, email);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, paymentFragment)
                    .addToBackStack(null)
                    .commit();
        });

        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle("Τέλος Στάθμευσης");
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private double calculateCost(String startTimeStr, String endTimeStr, double costPerHour) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startTimeStr, formatter);
            LocalDateTime end = LocalDateTime.parse(endTimeStr, formatter);

            long seconds = Duration.between(start, end).getSeconds();
            double hoursRoundedUp = Math.ceil(seconds / 3600.0);

            return hoursRoundedUp * costPerHour;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
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
