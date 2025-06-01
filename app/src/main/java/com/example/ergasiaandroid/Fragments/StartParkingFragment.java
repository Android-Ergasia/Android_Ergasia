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

import com.example.ergasiaandroid.R;
import com.example.ergasiaandroid.SpotChoiceInfoBottomSheet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StartParkingFragment extends Fragment {

    // Δηλώσεις μεταβλητών για UI και στοιχεία θέσης
    private EditText editPlate, editEmail;
    private TextView textSector, textAddress, textStartTime;
    private String sector;
    private String address;
    private String price;

    private String currentTime;

    // Μέθοδος δημιουργίας του Fragment με παραμέτρους
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

    // Δημιουργεί το layout του fragment
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);  // Δηλώνει ότι υπάρχει μενού (για back κουμπί)
        return inflater.inflate(R.layout.fragment_start_parking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Σύνδεση των στοιχείων UI με μεταβλητές
        editPlate = view.findViewById(R.id.edit_plate);
        editEmail = view.findViewById(R.id.edit_email);
        textSector = view.findViewById(R.id.text_sector);
        textAddress = view.findViewById(R.id.text_address);
        textStartTime = view.findViewById(R.id.text_start_time);
        Button startButton = view.findViewById(R.id.button_start);

        // Εστίαση στο πεδίο πινακίδας
        editPlate.requestFocus();

        // Ορισμός τίτλου action bar και ενεργοποίηση back κουμπιού
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle("Έναρξη Στάθμευσης");
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        // Ανάγνωση παραμέτρων που πέρασαν στο fragment
        if (getArguments() != null) {
            sector = getArguments().getString("spot_number", "Άγνωστο");
            address = getArguments().getString("spot_address", "Άγνωστη διεύθυνση");
            price = getArguments().getString("spot_price", "1.5");
        }

        // Υπολογισμός και αποθήκευση της τρέχουσας ώρας (μία φορά)
        currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Εμφάνιση τιμών στα TextViews
        textSector.setText("Θέση: " + sector);
        textAddress.setText("Διεύθυνση: " + address);
        textStartTime.setText("Ώρα έναρξης: " + currentTime);

        // Ενέργεια όταν ο χρήστης πατήσει το κουμπί "Έναρξη"
        startButton.setOnClickListener(v -> {
            String plate = editPlate.getText().toString().trim();
            String email = editEmail.getText().toString().trim();

            // Έλεγχος εγκυρότητας email
            if (!isValidEmail(email)) {
                Toast.makeText(getContext(), "Παρακαλώ εισάγετε έγκυρο email.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Έλεγχος αν έχει εισαχθεί πινακίδα
            if (plate.isEmpty()) {
                Toast.makeText(getContext(), "Παρακαλώ εισάγετε πινακίδα οχήματος.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Απόκρυψη πληκτρολογίου
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            // Μετατροπή της τιμής σε αριθμητική μορφή (π.χ. 1,5 → 1.5)
            String priceNumeric = price.replaceAll(",", ".").replaceAll("[^0-9.]", "");
            if (priceNumeric.isEmpty()) priceNumeric = "1.5";
            if (priceNumeric.endsWith(".")) priceNumeric = priceNumeric.substring(0, priceNumeric.length() - 1);

            // Μετάβαση στο StopParkingFragment με τα στοιχεία
            StopParkingFragment stopFragment = StopParkingFragment.newInstance(
                    sector, address, currentTime, plate, email, priceNumeric, false, null
            );

            // Εναλλαγή των fragments στην οθόνη
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, stopFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    // Έλεγχος εγκυρότητας email
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Ενέργεια όταν ο χρήστης πατήσει το back κουμπί στο action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {

                // Επιστροφή στο spot info bottom sheet με τις πληροφορίες θέσης
                SpotChoiceInfoBottomSheet bottomSheet = SpotChoiceInfoBottomSheet.newInstance(address, sector, price);
                bottomSheet.show(getParentFragmentManager(), "spot_choice_info");

                // Αφαίρεση του fragment από το backstack
                getParentFragmentManager().popBackStack();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
