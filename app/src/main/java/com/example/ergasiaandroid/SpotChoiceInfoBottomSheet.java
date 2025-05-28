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

    private String address;
    private String spotNumber;
    private String pricePerHour;

    public static SpotChoiceInfoBottomSheet newInstance(String address, String spotNumber, String pricePerHour) {
        SpotChoiceInfoBottomSheet fragment = new SpotChoiceInfoBottomSheet();
        Bundle args = new Bundle();
        args.putString("address", address);
        args.putString("spot_number", spotNumber);
        args.putString("price_per_hour", pricePerHour);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spot_choice_info_bottom_sheet, container, false);

        if (getArguments() != null) {
            address = getArguments().getString("address");
            spotNumber = getArguments().getString("spot_number");
            pricePerHour = getArguments().getString("price_per_hour");
        }

        TextView addressText = view.findViewById(R.id.text_address);
        TextView spotNumberText = view.findViewById(R.id.text_spot_number);
        TextView priceText = view.findViewById(R.id.text_price_per_hour);
        Button startButton = view.findViewById(R.id.button_start_parking);

        addressText.setText("Διεύθυνση: " + address);
        spotNumberText.setText("Αριθμός Θέσης: " + spotNumber);
        priceText.setText("Τιμή/ώρα: " + pricePerHour);

        startButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                // >>>>>>> ΝΕΑ ΠΡΟΣΘΗΚΗ: Κρύψε όλα τα views του MapActivity
                if (getActivity() instanceof MapsActivity) {
                    ((MapsActivity) getActivity()).toggleMainMapViews(false);
                }
                // <<<<<<<

                // Κάνε ορατό το fragment_container
                View fragmentContainer = getActivity().findViewById(R.id.fragment_container);
                fragmentContainer.setVisibility(View.VISIBLE);

                // Κρύψε το χάρτη
                View mapView = getActivity().findViewById(R.id.map);
                mapView.setVisibility(View.GONE);

                // Φόρτωσε το StartParkingFragment
                StartParkingFragment fragment = StartParkingFragment.newInstance(spotNumber, address, pricePerHour);
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();

                dismiss();
            }
        });


        return view;
    }
}
