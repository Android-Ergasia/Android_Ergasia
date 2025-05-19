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

    // Δημιουργία νέου instance του fragment με όρισμα τον τομέα
    public static StartParkingFragment newInstance(String sector) {
        StartParkingFragment fragment = new StartParkingFragment();
        Bundle args = new Bundle();
        args.putString("spot_number", sector);
        fragment.setArguments(args);
        return fragment;
    }

    public StartParkingFragment() {
        // Required empty public constructor
    }

    // Δημιουργεί το view του fragment και επιστρέφει το layout του fragment
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true); // Ενεργοποίηση χειρισμού menu για το βελάκι πίσω
        return inflater.inflate(R.layout.fragment_start_parking, container, false);
    }

    // Εκτελείται όταν το view έχει δημιουργηθεί
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Αντιστοίχιση μεταβλητών με τα στοιχεία του layout
        editPlate = view.findViewById(R.id.edit_plate);
        editEmail = view.findViewById(R.id.edit_email);
        textSector = view.findViewById(R.id.text_sector);
        textStartTime = view.findViewById(R.id.text_start_time);
        Button startButton = view.findViewById(R.id.button_start);

        editPlate.requestFocus();

        // Θέτει τον τίτλο action bar (αν υπάρχει activity)
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setTitle("Έναρξη Στάθμευσης");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Εμφάνιση βελακίου πίσω
        }

        // Ανάγνωση του τομέα από τα arguments
        if (getArguments() != null) {
            sector = getArguments().getString("spot_number", "Άγνωστος");
        }

        // Λήψη και μορφοποίηση της τρέχουσας ημερομηνίας και ώρας
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Εμφάνιση των στοιχείων
        textSector.setText("Τομέας: " + sector);
        textStartTime.setText("Ώρα έναρξης: " + currentTime);

        // Ενέργεια όταν πατηθεί το κουμπί "Έναρξη"
        startButton.setOnClickListener(v -> {

            // Ανάγνωση δεδομένων από τα πεδία εισαγωγής
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

            // Απόκρυψη πληκτρολογίου με το άνοιγμα του StopParkingFragment
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            // Δημιουργία του StopParkingFragment με τα απαραίτητα δεδομένα
            StopParkingFragment stopFragment = StopParkingFragment.newInstance(sector, currentTime, plate, email);

            // Μετάβαση στο StopParkingFragment
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, stopFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    // Έλεγχος εγκυρότητας email με regex pattern
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Χειρισμός επιλογών στο action bar, πχ το βελάκι πίσω
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Όταν πατηθεί το βελάκι πίσω, ανοίγει το SpotChoiceInfoBottomSheet

            if (getActivity() != null) {
                // Δημιουργία του bottom sheet με τα κατάλληλα δεδομένα
                SpotChoiceInfoBottomSheet bottomSheet = SpotChoiceInfoBottomSheet.newInstance(
                        "Διεύθυνση Παράδειγμα", // Μπορείς να βάλεις την πραγματική διεύθυνση εδώ αν την έχεις
                        sector,
                        "2€/ώρα" // Μπορείς να περάσεις την πραγματική τιμή
                );

                // Εμφάνιση του BottomSheet
                bottomSheet.show(getParentFragmentManager(), "spot_choice_info");

                // Αφαίρεση αυτού του fragment από το back stack
                getParentFragmentManager().popBackStack();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
