package com.example.ergasiaandroid;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
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

    // Μεταβλητές για τα στοιχεία της θέσης
    private String address;
    private String pricePerHour;
    private String spotName;

    //Δημιουργία ενός νέου στιγμιότυπου του BottomSheet με παραμέτρους
    public static SpotChoiceInfoBottomSheet newInstance(String address, String spotName, String pricePerHour) {
        SpotChoiceInfoBottomSheet fragment = new SpotChoiceInfoBottomSheet();
        Bundle args = new Bundle();
        args.putString("address", address);
        args.putString("spot_name", spotName);
        args.putString("price_per_hour", pricePerHour);
        fragment.setArguments(args);
        return fragment;
    }

    //Δημιουργεί το View του BottomSheet
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Φόρτωση του layout XML του BottomSheet
        View view = inflater.inflate(R.layout.fragment_spot_choice_info_bottom_sheet, container, false);

        // Αν υπάρχουν ορίσματα, τα αποθηκεύουμε στις μεταβλητές
        if (getArguments() != null) {
            address = getArguments().getString("address");
            spotName = getArguments().getString("spot_name");
            pricePerHour = getArguments().getString("price_per_hour");
        }

        // Συνδέουμε τα views από το layout με τις μεταβλητές
        TextView addressText = view.findViewById(R.id.text_address);
        TextView priceText = view.findViewById(R.id.text_price_per_hour);
        TextView spotNumberText = view.findViewById(R.id.text_spot_number);
        Button startButton = view.findViewById(R.id.button_start_parking);

        // Ορισμός κειμένου με στυλ (έντονη γραφή στο label)
        spotNumberText.setText(makeStyledLabelValue("Θέση: ", spotName));
        addressText.setText(makeStyledLabelValue("Διεύθυνση: ", address));
        priceText.setText(makeStyledLabelValue("Τιμή/ώρα: ", pricePerHour));

        // Ενέργεια όταν ο χρήστης πατήσει το κουμπί "Έναρξη Στάθμευσης"
        startButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                // Αν είναι σε MapsActivity, κρύβει άλλα views του χάρτη
                if (getActivity() instanceof MapsActivity) {
                    ((MapsActivity) getActivity()).toggleMainMapViews(false);
                }

                // Δημιουργία και εμφάνιση του fragment για την έναρξη στάθμευσης
                StartParkingFragment fragment = StartParkingFragment.newInstance(
                        spotName, address, pricePerHour
                );

                // Εναλλαγή fragment στο container
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null) // για να μπορεί να επιστρέψει πίσω
                        .commit();

                // Κλείσιμο του BottomSheet
                dismiss();
            }
        });

        return view;
    }

    // Δημιουργεί ένα SpannableString που εμφανίζει το label έντονα (bold) και το value κανονικά
    private SpannableString makeStyledLabelValue(String label, String value) {
        SpannableString spannable = new SpannableString(label + value);
        spannable.setSpan(
                new StyleSpan(android.graphics.Typeface.BOLD), // κάνει bold μόνο το label
                0,
                label.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        return spannable;
    }
}
