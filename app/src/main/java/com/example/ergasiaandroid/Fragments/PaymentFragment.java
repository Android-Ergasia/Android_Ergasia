
package com.example.ergasiaandroid.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ergasiaandroid.R;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class PaymentFragment extends Fragment {


        public PaymentFragment() {
            // Required empty public constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_payment, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            // Αλλάζει τον τίτλο στην ActionBar
            if (getActivity() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Πληρωμή στάθμευσης");
            }

            TextInputEditText cardNumber = view.findViewById(R.id.cardNumber);
            TextInputEditText expiryMonth = view.findViewById(R.id.expiryMonth);
            TextInputEditText expiryYear = view.findViewById(R.id.expiryYearEdit);
            TextInputEditText cvv = view.findViewById(R.id.cvv);
            TextInputEditText cardHolder = view.findViewById(R.id.cardHolder);
            Button payButton = view.findViewById(R.id.payButton);
            Button cancelButton = view.findViewById(R.id.cancelButton);
            TextView paymentAmount = view.findViewById(R.id.paymentAmount);
            TextView parkingInfo = view.findViewById(R.id.parkingInfo);


//            //Εμφανίζω τις Λεπτομέριες Συναλλαγής και Ποσό Πληρωμής
//            SQLiteConnection db = new SQLiteConnection(requireContext());
//            Cursor cursor = db.getParkingSessionsByUser(null); // θα κάνουμε query χωρίς φίλτρο user αν είναι null
//
//            String location = "Άγνωστη θέση";
//            String startTime = null;
//            String userId = "Άγνωστη πινακίδα";
//            String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
//            double cost = 0;
//            long minutes = 0;
//
//            if (cursor.moveToFirst()) {
//                do {
//                    String end = cursor.getString(cursor.getColumnIndexOrThrow(ParkingContract.ParkingEntry.COLUMN_END_TIME));
//                    if (end == null) {
//                        userId = cursor.getString(cursor.getColumnIndexOrThrow(ParkingContract.ParkingEntry.COLUMN_USER_ID));
//                        location = cursor.getString(cursor.getColumnIndexOrThrow(ParkingContract.ParkingEntry.COLUMN_LOCATION));
//                        startTime = cursor.getString(cursor.getColumnIndexOrThrow(ParkingContract.ParkingEntry.COLUMN_START_TIME));
//                        break;
//                    }
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//            db.close();

//            if (startTime != null) {
//                cost = calculateCost(startTime, endTime, 1.5);
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                minutes = Duration.between(LocalDateTime.parse(startTime, formatter),
//                        LocalDateTime.parse(endTime, formatter)).toMinutes();
//
//                String fullText = getString(R.string.amount) + "\n" + String.format(Locale.getDefault(), "%.2f €", cost);
//                paymentAmount.setText(fullText);
//                paymentAmount.setGravity(Gravity.CENTER);
//
//                String detailText = "Πληρωμή στάθμευσης - Χονολουλού: " + userId + " • " + location + " • " + minutes + "'";
//                parkingInfo.setText(detailText);
//                parkingInfo.setGravity(Gravity.CENTER);
//            }



            //Εμφανίζω το συνολικο ποσό πληρωμής μαζι με το "Ποσό πληρωμής"
            //String label = getString(R.string.amount); // "Ποσό πληρωμής"
            //  String amount = "3.75$"; // ή ό,τι άλλο δυναμικά
            //String fullText = label + "\n" + amount;
            // paymentAmount.setText(fullText);
            //paymentAmount.setGravity(Gravity.CENTER);  // Κεντράρει το κείμενο

//            SharedPreferences prefs = requireContext().getSharedPreferences("parking", Context.MODE_PRIVATE);
//            String startTime = prefs.getString("start_time", null);
//
//            if (startTime != null) {
//                String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
//                double cost = calculateCost(startTime, endTime, 1.5); // π.χ. 1.5€ / ώρα
//                String label = getString(R.string.amount);
//                String fullText = label + "\n" + String.format(Locale.getDefault(), "%.2f €", cost);
//                paymentAmount.setText(fullText);
//                paymentAmount.setGravity(Gravity.CENTER);  // Κεντράρει το κείμενο
//            }





//            final double finalCost = cost;
//            final String finalUserId = userId;
            payButton.setOnClickListener(v -> {
                // Έλεγχος πεδίων
                if (TextUtils.isEmpty(cardNumber.getText()) ||
                        TextUtils.isEmpty(expiryMonth.getText()) ||
                        TextUtils.isEmpty(expiryYear.getText()) ||
                        TextUtils.isEmpty(cvv.getText()) ||
                        TextUtils.isEmpty(cardHolder.getText())) {

                    Toast.makeText(getContext(), "Συμπληρώστε όλα τα πεδία!", Toast.LENGTH_SHORT).show();
                } else {
                    // Εδώ μπορεί να προστεθεί έλεγχος ή αποστολή σε API
                    String finalEndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());



                    Toast.makeText(getContext(), "Η πληρωμή ολοκληρώθηκε!", Toast.LENGTH_LONG).show();
                }
            });

            cancelButton.setOnClickListener(v -> {
                cardNumber.setText("");
                expiryMonth.setText("");
                expiryYear.setText("");
                cvv.setText("");
                cardHolder.setText("");
            });


        }


        //Συνάρτηση υπολογισμού κόστους
        private double calculateCost(String startTimeStr, String endTimeStr, double costPerHour) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDateTime start = LocalDateTime.parse(startTimeStr, formatter);
            LocalDateTime end = LocalDateTime.parse(endTimeStr, formatter);

            Duration duration = Duration.between(start, end);
            long minutes = duration.toMinutes();
            double hours = minutes / 60.0;

            return hours * costPerHour;
        }

    }


