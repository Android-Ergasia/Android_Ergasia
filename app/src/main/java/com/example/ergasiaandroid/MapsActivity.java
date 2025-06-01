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
        // Σύνδεση με το layout XML
        setContentView(R.layout.activity_maps);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav); // Πλοήγηση κάτω (Map / Wallet / Stats)

        // Έλεγχος αν λάβαμε το isAdmin flag από το προηγούμενο activity (MainActivity)
        if (getIntent() != null && getIntent().hasExtra("isAdmin")) {
            isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        }

        // Αν είναι admin (isAdmin == true), τότε κρύβουμε την κάτω μπάρα πλοήγησης (δεν έχει wallet/stats)
        if (isAdmin) {
            bottomNav.setVisibility(View.GONE);
        } else {
            // Αν είναι χρήστης(isAdmin == false), εμφανίζουμε την μπάρα και ενεργοποιούμε την πλοήγηση
            bottomNav.setVisibility(View.VISIBLE);

            bottomNav.setOnItemSelectedListener(item -> {
                Fragment selectedFragment = null;

                // Ανάλογα με το τι πατήθηκε από κάτω (Map / Wallet / Stats) μεταβαίνουμε στο αντίστοιχο fragment
                switch (item.getItemId()) {
                    case R.id.nav_map:
                        selectedFragment = createMapFragmentWithAdminFlag(false); // Χάρτης
                        break;
                    case R.id.nav_wallet:
                        selectedFragment = new WalletFragment(); // Πορτοφόλι
                        break;
                    case R.id.nav_stats:
                        selectedFragment = new StatisticsFragment(); // Στατιστικά
                        break;
                }

                // Αν επιλέχθηκε κάτι, το εμφανίζουμε
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            // Αντικαθιστούμε το fragment
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true;
                }
                return false;
            });
        }

        // Όταν ανοίξει για πρώτη φορά, να δείχνει πάντα πρώτα τον χάρτη (με βάση το isAdmin)
        if (savedInstanceState == null) {
            MapFragment mapFragment = createMapFragmentWithAdminFlag(isAdmin);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mapFragment)
                    .commit();
        }
    }

    // Βοηθητική μέθοδος για να φτιάξουμε MapFragment και να του περάσουμε το αν είναι admin
    private MapFragment createMapFragmentWithAdminFlag(boolean isAdmin) {
        MapFragment mapFragment = new MapFragment();
        Bundle args = new Bundle();
        args.putBoolean("isAdmin", isAdmin); // Περνάμε στο fragment το flag
        mapFragment.setArguments(args);
        return mapFragment;
    }

    // Ενεργοποιεί ή απενεργοποιεί την εμφάνιση UI στοιχείων στο χάρτη
    public void toggleMainMapViews(boolean b) {
        int visibility = b ? View.VISIBLE : View.GONE;

        // Παίρνουμε όλα τα views που θέλουμε να κρύψουμε/εμφανίσουμε
        View searchBar = findViewById(R.id.searchBar);
        View availabilityFilter = findViewById(R.id.availabilityFilter);
        View parkingList = findViewById(R.id.parkingList);
        View btnZoomIn = findViewById(R.id.btnZoomIn);
        View btnZoomOut = findViewById(R.id.btnZoomOut);

        // Ελέγχουμε αν υπάρχουν και τα κρύβουμε ή τα εμφανίζουμε ανάλογα
        if (searchBar != null) searchBar.setVisibility(visibility);
        if (availabilityFilter != null) availabilityFilter.setVisibility(visibility);
        if (parkingList != null) parkingList.setVisibility(visibility);
        if (btnZoomIn != null) btnZoomIn.setVisibility(visibility);
        if (btnZoomOut != null) btnZoomOut.setVisibility(visibility);
    }
}
