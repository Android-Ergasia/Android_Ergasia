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

public class StopParkingFragment extends Fragment {

    private String sector, startTime, plate, email;

    public static StopParkingFragment newInstance(String sector, String startTime, String plate, String email) {
        StopParkingFragment fragment = new StopParkingFragment();
        Bundle args = new Bundle();
        args.putString("sector", sector);
        args.putString("start_time", startTime);
        args.putString("plate", plate);
        args.putString("email", email);
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
        if (getArguments() != null) {
            sector = getArguments().getString("sector");
            startTime = getArguments().getString("start_time");
            plate = getArguments().getString("plate");
            email = getArguments().getString("email");
        }

        TextView textSector = view.findViewById(R.id.text_sector);
        TextView textStartTime = view.findViewById(R.id.text_start_time);
        TextView textPlate = view.findViewById(R.id.text_plate);
        TextView textEmail = view.findViewById(R.id.text_email);
        Button finishButton = view.findViewById(R.id.button_finish);

        textSector.setText("Τομέας: " + sector);
        textStartTime.setText("Ώρα Έναρξης: " + startTime);
        textPlate.setText("Πινακίδα: " + plate);
        textEmail.setText("Email: " + email);

        finishButton.setOnClickListener(v -> {
            PaymentFragment paymentFragment = PaymentFragment.newInstance(sector, startTime, plate, email);

            // Περνάει σωστά στο επόμενο fragment
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Μόνο όταν βγαίνουμε τελείως επιστρέφουμε στην αρχική κατάσταση!
            getParentFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Δεν κάνουμε εδώ χειρισμό ορατότητας!
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
