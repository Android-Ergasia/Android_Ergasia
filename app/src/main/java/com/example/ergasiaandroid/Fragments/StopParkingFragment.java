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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StopParkingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StopParkingFragment extends Fragment {

    // Μεταβλητές για τα δεδομένα που θα περαστούν στο fragment
    private String sector, startTime, plate, email;

    // Δημιουργία νέου instance του fragment με τα δεδομένα που απαιτούνται
    public static StopParkingFragment newInstance(String sector, String startTime, String plate, String email) {
        StopParkingFragment fragment = new StopParkingFragment();
        Bundle args = new Bundle();
        args.putString("sector", sector);
        args.putString("startTime", startTime);
        args.putString("plate", plate);
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    public StopParkingFragment() {
        // Required empty public constructor
    }

    // Προσθέτει στο fragment τη δυνατότητα να χειρίζεται menu events
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);  // Ενεργοποιεί το μενού για το fragment
    }

    // Δημιουργεί το view του fragment και επιστρέφει το layout του fragment
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stop_parking, container, false);
    }

    // Καλείται όταν το view έχει δημιουργηθεί
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Αντιστοίχιση των arguments που πέρασαν κατά τη δημιουργία του fragment με τις μεταβλητές
        if (getArguments() != null) {
            sector = getArguments().getString("sector");
            startTime = getArguments().getString("startTime");
            plate = getArguments().getString("plate");
            email = getArguments().getString("email");
        }

        // Αντιστοίχιση νέων μεταβλητών με τα στοιχεία του layout
        TextView textSector = view.findViewById(R.id.text_sector);
        TextView textStartTime = view.findViewById(R.id.text_start_time);
        TextView textPlate = view.findViewById(R.id.text_plate);
        TextView textEmail = view.findViewById(R.id.text_email);
        Button finishButton = view.findViewById(R.id.button_finish);

        // Εμφάνιση των πληροφοριών στάθμευσης στην οθόνη
        textSector.setText("Τομέας: " + sector);
        textStartTime.setText("Ώρα Έναρξης: " + startTime);
        textPlate.setText("Πινακίδα: " + plate);
        textEmail.setText("Email: " + email);

        // Ορισμός λειτουργίας κουμπιού ολοκλήρωσης
        finishButton.setOnClickListener(v -> {
            PaymentFragment paymentFragment = PaymentFragment.newInstance(sector, startTime, plate, email);

            // Μετάβαση στο PaymentFragment, αντικαθιστώντας το περιεχόμενο της οθόνης
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, paymentFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Ρύθμιση του τίτλου και εμφάνιση του βελάκι πίσω στο action bar
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setTitle("Τέλος Στάθμευσης");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Εμφάνιση βελάκι πίσω
        }
    }

    // Διαχείριση κλικ στο βελάκι πίσω του Action Bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Πίσω στο προηγούμενο fragment (πιθανόν StartParkingFragment)
            getParentFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
