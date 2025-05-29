package com.example.ergasiaandroid;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ergasiaandroid.Fragments.MapFragment;
import com.example.ergasiaandroid.Fragments.StatisticsFragment;
import com.example.ergasiaandroid.Fragments.WalletFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MapsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_map:
                    selectedFragment = new MapFragment();
                    break;
                case R.id.nav_wallet:
                    selectedFragment = new WalletFragment();
                    break;
                case R.id.nav_stats:
                    selectedFragment = new StatisticsFragment();
                    break;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });


        // Default να φορτώνεται ο χάρτης
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MapFragment())
                    .commit();
        }
    }

    public void toggleMainMapViews(boolean b) {
        int visibility = b ? View.VISIBLE : View.GONE;
        findViewById(R.id.searchBar).setVisibility(visibility);
        findViewById(R.id.availabilityFilter).setVisibility(visibility);
        findViewById(R.id.parkingList).setVisibility(visibility);
        findViewById(R.id.btnZoomIn).setVisibility(visibility);
        findViewById(R.id.btnZoomOut).setVisibility(visibility);
    }
}
