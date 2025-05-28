package com.example.ergasiaandroid.Fragments;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ergasiaandroid.R;
import com.example.ergasiaandroid.SpotChoiceInfoBottomSheet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StartParkingFragment extends Fragment {

    private EditText editPlate, editEmail;
    private TextView textSector, textStartTime;
    private String sector;
    private String address;
    private String price;

    public static StartParkingFragment newInstance(String sector, String address, String price) {
        StartParkingFragment fragment = new StartParkingFragment();
        Bundle args = new Bundle();
        args.putString("spot_number", sector);
        args.putString("spot_address", address);
        args.putString("spot_price", price);
        fragment.setArguments(args);
        return fragment;
    }

    public StartParkingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true); // Enable options menu for back arrow
        return inflater.inflate(R.layout.fragment_start_parking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editPlate = view.findViewById(R.id.edit_plate);
        editEmail = view.findViewById(R.id.edit_email);
        textSector = view.findViewById(R.id.text_sector);
        textStartTime = view.findViewById(R.id.text_start_time);
        Button startButton = view.findViewById(R.id.button_start);

        editPlate.requestFocus();

        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setTitle("Έναρξη Στάθμευσης");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Back arrow
        }

        if (getArguments() != null) {
            sector = getArguments().getString("spot_number", "Άγνωστος");
            address = getArguments().getString("spot_address", "Άγνωστη διεύθυνση");
            price = getArguments().getString("spot_price", "Άγνωστη τιμή");
        }

        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        textSector.setText("Τομέας: " + sector);
        textStartTime.setText("Ώρα έναρξης: " + currentTime);

        startButton.setOnClickListener(v -> {
            String plate = editPlate.getText().toString().trim();
            String email = editEmail.getText().toString().trim();

            if (!isValidEmail(email)) {
                Toast.makeText(getContext(), "Παρακαλώ εισάγετε έγκυρο email.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (plate.isEmpty()) {
                Toast.makeText(getContext(), "Παρακαλώ εισάγετε πινακίδα οχήματος.", Toast.LENGTH_SHORT).show();
                return;
            }

            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            StopParkingFragment stopFragment = StopParkingFragment.newInstance(sector, currentTime, plate, email);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, stopFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {
                SpotChoiceInfoBottomSheet bottomSheet = SpotChoiceInfoBottomSheet.newInstance(address, sector, price);
                bottomSheet.show(getParentFragmentManager(), "spot_choice_info");

                // Επαναφορά ορατότητας map & κρυφτό container
                View mapView = getActivity().findViewById(R.id.map);
                mapView.setVisibility(View.VISIBLE);

                View fragmentContainer = getActivity().findViewById(R.id.fragment_container);
                fragmentContainer.setVisibility(View.GONE);

                getParentFragmentManager().popBackStack();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            View mapView = getActivity().findViewById(R.id.map);
            mapView.setVisibility(View.VISIBLE);

            View fragmentContainer = getActivity().findViewById(R.id.fragment_container);
            fragmentContainer.setVisibility(View.GONE);
        }
    }
}
