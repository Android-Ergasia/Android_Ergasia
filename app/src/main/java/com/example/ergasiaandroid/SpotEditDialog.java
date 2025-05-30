//package com.example.ergasiaandroid;
//
//import android.app.Dialog;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.DialogFragment;
//
//public class SpotEditDialog extends DialogFragment {
//
//    private ParkingLocation spot;
//
//    public static SpotEditDialog newInstance(ParkingLocation spot) {
//        SpotEditDialog dialog = new SpotEditDialog();
//        Bundle args = new Bundle();
//        args.putSerializable("spot", spot);
//        dialog.setArguments(args);
//        return dialog;
//    }
//
//    @Nullable
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_spot, null);
//
//        spot = (ParkingLocation) getArguments().getSerializable("spot");
//
//        EditText etHours = view.findViewById(R.id.etHours);
//        EditText etCost = view.findViewById(R.id.etCost);
//        CheckBox cbActive = view.findViewById(R.id.cbActive);
//        Button btnSave = view.findViewById(R.id.btnSave);
//
//        etHours.setText(spot.hours);
//        etCost.setText(String.valueOf(spot.cost));
//        cbActive.setChecked(spot.isActive);
//
//        btnSave.setOnClickListener(v -> {
//            spot.hours = etHours.getText().toString();
//            spot.cost = Double.parseDouble(etCost.getText().toString());
//            spot.isActive = cbActive.isChecked();
//
//            // Optional: save to SharedPreferences here
//            dismiss();
//        });
//
//        Dialog dialog = new Dialog(requireContext());
//        dialog.setContentView(view);
//        dialog.setTitle("Επεξεργασία Θέσης");
//        return dialog;
//    }
//}
