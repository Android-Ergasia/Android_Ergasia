package com.example.ergasiaandroid.Fragments;

import android.os.Bundle;
import android.util.Patterns;
import android.view.*;
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

import com.example.ergasiaandroid.HttpHandler;
import com.example.ergasiaandroid.R;
import com.example.ergasiaandroid.SpotChoiceInfoBottomSheet;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// ... (εισαγωγές όπως τις έχεις)

public class StartParkingFragment extends Fragment {

    private EditText editPlate, editEmail;
    private TextView textSector, textAddress, textStartTime;
    private String sector;
    private String address;
    private String price;
    private String currentTime;

    public static StartParkingFragment newInstance(String sector, String address, String price) {
        StartParkingFragment fragment = new StartParkingFragment();
        Bundle args = new Bundle();
        args.putString("spot_number", sector);
        args.putString("spot_address", address);
        args.putString("spot_price", price);
        fragment.setArguments(args);
        return fragment;
    }

    public StartParkingFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_start_parking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editPlate = view.findViewById(R.id.edit_plate);
        editEmail = view.findViewById(R.id.edit_email);
        textSector = view.findViewById(R.id.text_sector);
        textAddress = view.findViewById(R.id.text_address);
        textStartTime = view.findViewById(R.id.text_start_time);
        Button startButton = view.findViewById(R.id.button_start);

        editPlate.requestFocus();

        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle("Έναρξη Στάθμευσης");
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        if (getArguments() != null) {
            sector = getArguments().getString("spot_number", "Άγνωστο");
            address = getArguments().getString("spot_address", "Άγνωστη διεύθυνση");
            price = getArguments().getString("spot_price", "1.5");
        }

        currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        textSector.setText("Θέση: " + sector);
        textAddress.setText("Διεύθυνση: " + address);
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

            String priceNumeric = price.replaceAll(",", ".").replaceAll("[^0-9.]", "");
            if (priceNumeric.isEmpty()) priceNumeric = "1.5";
            if (priceNumeric.endsWith(".")) priceNumeric = priceNumeric.substring(0, priceNumeric.length() - 1);

            // Ενημέρωση της βάσης: η θέση γίνεται μη διαθέσιμη
            setSpotUnavailable(sector);

            // Μετάβαση στο StopParkingFragment
            StopParkingFragment stopFragment = StopParkingFragment.newInstance(
                    sector, address, currentTime, plate, email, priceNumeric, false, null
            );

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

    private void setSpotUnavailable(String spotName) {
        new Thread(() -> {
            try {
                String url = "http://10.0.2.2/parking_app/update_availability.php";
                String postData = "spot_name=" + URLEncoder.encode(spotName, StandardCharsets.UTF_8.name());

                String response = HttpHandler.post(url, postData);
                if (response != null && response.contains("success")) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Η θέση έγινε μη διαθέσιμη.", Toast.LENGTH_SHORT).show()
                    );
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Σφάλμα ενημέρωσης θέσης στη βάση.", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Σφάλμα σύνδεσης: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {
                SpotChoiceInfoBottomSheet bottomSheet = SpotChoiceInfoBottomSheet.newInstance(address, sector, price);
                bottomSheet.show(getParentFragmentManager(), "spot_choice_info");
                getParentFragmentManager().popBackStack();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
