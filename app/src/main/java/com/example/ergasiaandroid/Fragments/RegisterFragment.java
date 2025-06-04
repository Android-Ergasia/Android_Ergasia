package com.example.ergasiaandroid.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ergasiaandroid.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {

    private EditText editEmail, editPassword, editConfirmPassword;
    private Spinner roleSpinner;
    private Button btnRegister;
    private TextView txtError;

    public RegisterFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editEmail = view.findViewById(R.id.editEmail);
        editPassword = view.findViewById(R.id.editPassword);
        editConfirmPassword = view.findViewById(R.id.editConfirmPassword);
        roleSpinner = view.findViewById(R.id.roleSpinner);
        btnRegister = view.findViewById(R.id.btnRegister);
        txtError = view.findViewById(R.id.txtError);

        // Γέμισμα του spinner με ρόλους
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new String[]{"user", "admin"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirm = editConfirmPassword.getText().toString().trim();
        String role = roleSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm)) {
            txtError.setText("Συμπλήρωσε όλα τα πεδία");
            txtError.setVisibility(View.VISIBLE);
            return;
        }

        if (!password.equals(confirm)) {
            txtError.setText("Οι κωδικοί δεν ταιριάζουν");
            txtError.setVisibility(View.VISIBLE);
            return;
        }

        String url = "http://10.0.2.2/parking_app/register.php";

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("role", role);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, new JSONObject(params),
                response -> {
                    if (response.optBoolean("success")) {
                        Toast.makeText(getContext(), "Εγγραφή επιτυχής!", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack(); // επιστροφή στο login
                    } else {
                        txtError.setText(response.optString("message", "Σφάλμα εγγραφής"));
                        txtError.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    txtError.setText("Σφάλμα σύνδεσης");
                    txtError.setVisibility(View.VISIBLE);
                    error.printStackTrace();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }
}
