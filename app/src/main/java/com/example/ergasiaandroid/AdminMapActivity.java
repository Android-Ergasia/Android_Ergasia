//package com.example.ergasiaandroid;
//
//import android.os.Bundle;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.ergasiaandroid.Fragments.MapFragment;
//
//public class AdminMapActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_admin_map);
//
//        // Φόρτωση του χάρτη σε admin mode
//        MapFragment mapFragment = MapFragment.newInstance(true); // true = admin mode
//
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.admin_map_fragment_container, MapFragment.newInstance(true))
//                .commit();
//    }
//}
