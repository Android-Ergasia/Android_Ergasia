package com.example.ergasiaandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ergasiaandroid.Fragments.StartParkingFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SpotChoiceInfoBottomSheet extends BottomSheetDialogFragment {

    // Μεταβλητές για αποθήκευση των στοιχείων της θέσης
    private String address;
    private String spotNumber;
    private String pricePerHour;

    // Δημιουργία νέου instance του fragment με τα δεδομένα της θέσης (διεύθυνση, αριθμός, τιμή)
    public static SpotChoiceInfoBottomSheet newInstance(String address, String spotNumber, String pricePerHour) {
        SpotChoiceInfoBottomSheet fragment = new SpotChoiceInfoBottomSheet();
        Bundle args = new Bundle();
        args.putString("address", address);
        args.putString("spot_number", spotNumber);
        args.putString("price_per_hour", pricePerHour);
        fragment.setArguments(args);
        return fragment;
    }

    //Δημιουργία και εμφάνιση του layout για το BottomSheet
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Φορτώνει το layout του BottomSheet
        View view = inflater.inflate(R.layout.fragment_spot_choice_info_bottom_sheet, container, false);

        // Αν υπάρχουν arguments, τα αποθηκεύει στις μεταβλητές
        if (getArguments() != null) {
            address = getArguments().getString("address");
            spotNumber = getArguments().getString("spot_number");
            pricePerHour = getArguments().getString("price_per_hour");
        }

        // Αντιστοίχιση των στοιχείων του layout με τις μεταβλητές
        TextView addressText = view.findViewById(R.id.text_address);
        TextView spotNumberText = view.findViewById(R.id.text_spot_number);
        TextView priceText = view.findViewById(R.id.text_price_per_hour);
        Button startButton = view.findViewById(R.id.button_start_parking);

        // Εμφάνιση των τιμών στα TextView
        addressText.setText("Διεύθυνση: " + address);
        spotNumberText.setText("Αριθμός Θέσης: " + spotNumber);
        priceText.setText("Τιμή/ώρα: " + pricePerHour);

        // Ορισμός ενέργειας για το κουμπί "Έναρξη Στάθμευσης"
        startButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                // Δημιουργία νέου StartParkingFragment με την επιλεγμένη θέση
                StartParkingFragment fragment = StartParkingFragment.newInstance(spotNumber);

                // Αντικατάσταση του τρέχοντος fragment με το StartParkingFragment
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();

                // Κλείνει το BottomSheet
                dismiss();
            }
        });

        return view;
    }
}
