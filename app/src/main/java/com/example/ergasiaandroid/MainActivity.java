package com.example.ergasiaandroid;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ergasiaandroid.Fragments.PaymentFragment;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Φορτώνει το PaymentFragment στο container
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PaymentFragment())
                .commit();
    }
}
