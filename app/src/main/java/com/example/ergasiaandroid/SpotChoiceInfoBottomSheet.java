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

    private String address;
    private String pricePerHour;
    private String spotName;

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
            spotName = getArguments().getString("spot_name");
            pricePerHour = getArguments().getString("price_per_hour");
        }

        TextView addressText = view.findViewById(R.id.text_address);
        TextView priceText = view.findViewById(R.id.text_price_per_hour);
        TextView spotNumberText = view.findViewById(R.id.text_spot_number);
        Button startButton = view.findViewById(R.id.button_start_parking);

        // Set formatted, single-line label + value text
        spotNumberText.setText(makeStyledLabelValue("Θέση: ", spotName));
        addressText.setText(makeStyledLabelValue("Διεύθυνση: ", address));
        priceText.setText(makeStyledLabelValue("Τιμή/ώρα: ", pricePerHour));

        startButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                if (getActivity() instanceof MapsActivity) {
                    ((MapsActivity) getActivity()).toggleMainMapViews(false);
                }

                View fragmentContainer = getActivity().findViewById(R.id.fragment_container);
                fragmentContainer.setVisibility(View.VISIBLE);

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

    // Helper method for label-value text with bold label
    private SpannableString makeStyledLabelValue(String label, String value) {
        SpannableString spannable = new SpannableString(label + value);
        spannable.setSpan(
                new StyleSpan(android.graphics.Typeface.BOLD),
                0,
                label.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        return spannable;
    }
}
