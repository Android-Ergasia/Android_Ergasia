package com.example.ergasiaandroid;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ergasiaandroid.Fragments.PaymentFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteConnection db = new SQLiteConnection(this); // ή requireContext() αν είσαι σε Fragment

        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        db.insertParkingSession("TEST1234", "12399", startTime, null, 0.0);
        db.close();


        // Φορτώνει το PaymentFragment στο container
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PaymentFragment())
                .commit();
    }
}
