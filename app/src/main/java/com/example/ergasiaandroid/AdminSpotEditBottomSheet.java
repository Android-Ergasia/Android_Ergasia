package com.example.ergasiaandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AdminSpotEditBottomSheet extends BottomSheetDialogFragment {

    public static AdminSpotEditBottomSheet newInstance(ParkingSpot spot) {
        AdminSpotEditBottomSheet sheet = new AdminSpotEditBottomSheet();
        Bundle args = new Bundle();
        args.putString("name", spot.name);
        args.putString("address", spot.address);
        args.putBoolean("available", spot.isAvailable);
        args.putString("price", spot.pricePerHour);
        args.putDouble("lat", spot.lat);
        args.putDouble("lng", spot.lng);
        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_admin_edit_spot, container, false);

        TextView txtName = view.findViewById(R.id.spot_name);
        TextView txtAddress = view.findViewById(R.id.spot_address);
        TextView txtAvailable = view.findViewById(R.id.spot_available);
        TextView txtPrice = view.findViewById(R.id.spot_price);
        Button btnEdit = view.findViewById(R.id.btn_edit_spot);

        Bundle args = getArguments();
        if (args != null) {
            txtName.setText(args.getString("name", ""));
            txtAddress.setText(args.getString("address", ""));
            txtAvailable.setText(args.getBoolean("available") ? "Ναι" : "Όχι");
            txtPrice.setText(args.getString("price", ""));
        }

        btnEdit.setOnClickListener(v -> {
            ParkingSpot spot = new ParkingSpot(
                    args.getString("name", ""),
                    args.getDouble("lat", 0),
                    args.getDouble("lng", 0),
                    args.getBoolean("available"),
                    args.getString("price", ""),
                    requireContext()
            );

            Intent intent = new Intent(requireContext(), AdminPanelActivity.class);
            intent.putExtra("spot", spot);
            startActivity(intent);

            dismiss();
        });

        return view;
    }
}
