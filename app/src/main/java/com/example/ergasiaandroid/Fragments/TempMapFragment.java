package com.example.ergasiaandroid.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ergasiaandroid.R;
import com.example.ergasiaandroid.SpotChoiceInfoBottomSheet;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link TempMapFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class TempMapFragment extends Fragment {

    public TempMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temp_map, container, false);

        Button button = view.findViewById(R.id.button_select_parking);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Παράδειγμα δεδομένων
                String address = "Παύλου Μελά";
                String spotNumber = "1234";
                String pricePerHour = "1.7/ώρα";

                SpotChoiceInfoBottomSheet bottomSheet = SpotChoiceInfoBottomSheet.newInstance(
                        address, spotNumber, pricePerHour
                );
                bottomSheet.show(getParentFragmentManager(), "SpotInfo");
            }
        });

        return view;
    }
}