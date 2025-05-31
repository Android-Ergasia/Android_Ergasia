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

    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        // Έλεγχος αν μπήκαμε ως admin
        if (getIntent() != null && getIntent().hasExtra("isAdmin")) {
            isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        }

        if (isAdmin) {
            bottomNav.setVisibility(View.GONE);
        } else {
            bottomNav.setVisibility(View.VISIBLE);

            bottomNav.setOnItemSelectedListener(item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.nav_map:
                        selectedFragment = createMapFragmentWithAdminFlag(false);
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
        }

        // Default: Φόρτωσε MapFragment και πέρασε isAdmin flag
        if (savedInstanceState == null) {
            MapFragment mapFragment = createMapFragmentWithAdminFlag(isAdmin);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mapFragment)
                    .commit();
        }
    }

    private MapFragment createMapFragmentWithAdminFlag(boolean isAdmin) {
        MapFragment mapFragment = new MapFragment();
        Bundle args = new Bundle();
        args.putBoolean("isAdmin", isAdmin);
        mapFragment.setArguments(args);
        return mapFragment;
    }

    public void toggleMainMapViews(boolean b) {
        int visibility = b ? View.VISIBLE : View.GONE;
        View searchBar = findViewById(R.id.searchBar);
        View availabilityFilter = findViewById(R.id.availabilityFilter);
        View parkingList = findViewById(R.id.parkingList);
        View btnZoomIn = findViewById(R.id.btnZoomIn);
        View btnZoomOut = findViewById(R.id.btnZoomOut);

        if (searchBar != null) searchBar.setVisibility(visibility);
        if (availabilityFilter != null) availabilityFilter.setVisibility(visibility);
        if (parkingList != null) parkingList.setVisibility(visibility);
        if (btnZoomIn != null) btnZoomIn.setVisibility(visibility);
        if (btnZoomOut != null) btnZoomOut.setVisibility(visibility);
    }
}
