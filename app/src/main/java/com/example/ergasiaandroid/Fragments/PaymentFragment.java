
package com.example.ergasiaandroid.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.ergasiaandroid.R;
import com.google.android.material.textfield.TextInputEditText;

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

            TextInputEditText cardNumber = view.findViewById(R.id.cardNumber);
            TextInputEditText expiryMonth = view.findViewById(R.id.expiryMonth);
            TextInputEditText expiryYear = view.findViewById(R.id.expiryYearEdit);
            TextInputEditText cvv = view.findViewById(R.id.cvv);
            TextInputEditText cardHolder = view.findViewById(R.id.cardHolder);
            Button payButton = view.findViewById(R.id.payButton);
            Button cancelButton = view.findViewById(R.id.cancelButton);

            payButton.setOnClickListener(v -> {
                // Έλεγχος πεδίων
                if (TextUtils.isEmpty(cardNumber.getText()) ||
                        TextUtils.isEmpty(expiryMonth.getText()) ||
                        TextUtils.isEmpty(expiryYear.getText()) ||
                        TextUtils.isEmpty(cvv.getText()) ||
                        TextUtils.isEmpty(cardHolder.getText())) {

                    Toast.makeText(getContext(), "Συμπληρώστε όλα τα πεδία!", Toast.LENGTH_SHORT).show();
                } else {
                    // Εδώ μπορείς να προσθέσεις έλεγχο ή αποστολή σε API
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
    }
