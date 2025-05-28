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
    private String pricePerHour;
    private String spotName;

    // Περνάμε ΚΑΙ το spotName πλέον!
    public static SpotChoiceInfoBottomSheet newInstance(String address, String spotName, String pricePerHour) {
        SpotChoiceInfoBottomSheet fragment = new SpotChoiceInfoBottomSheet();
        Bundle args = new Bundle();
        args.putString("address", address);
        args.putString("spot_name", spotName);
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
            pricePerHour = getArguments().getString("price_per_hour");
            spotName = getArguments().getString("spot_name");
        }

        TextView addressText = view.findViewById(R.id.text_address);
        TextView priceText = view.findViewById(R.id.text_price_per_hour);
        TextView spotNumberText = view.findViewById(R.id.text_spot_number);
        Button startButton = view.findViewById(R.id.button_start_parking);

        addressText.setText("Διεύθυνση: " + address);
        priceText.setText("Τιμή/ώρα: " + pricePerHour);
        spotNumberText.setText("Θέση: " + spotName);

        startButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                // Κρύψε όλα τα views του MapActivity
                if (getActivity() instanceof MapsActivity) {
                    ((MapsActivity) getActivity()).toggleMainMapViews(false);
                }

                // Κάνε ορατό το fragment_container
                View fragmentContainer = getActivity().findViewById(R.id.fragment_container);
                fragmentContainer.setVisibility(View.VISIBLE);

                // Κρύψε το χάρτη
//                View mapView = getActivity().findViewById(R.id.map);
//                mapView.setVisibility(View.GONE);

                // Περνάμε ως sector το spotName, ως address τη διεύθυνση (και την τιμή)
                StartParkingFragment fragment = StartParkingFragment.newInstance(spotName, address, pricePerHour);
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
