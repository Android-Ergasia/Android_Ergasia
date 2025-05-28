package com.example.ergasiaandroid;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<ParkingSpot> allSpots;

    class ParkingSpot {
        String name;
        double lat, lng;
        boolean isAvailable;
        String address;
        String pricePerHour;

        ParkingSpot(String name, double lat, double lng, boolean isAvailable, String address, String pricePerHour) {
            this.name = name;
            this.lat = lat;
            this.lng = lng;
            this.isAvailable = isAvailable;
            this.address = address;
            this.pricePerHour = pricePerHour;
        }
    }

    private List<ParkingSpot> getAllSpots() {
        return Arrays.asList(
                new ParkingSpot("Parking 1", 21.3060, -157.8570, true,"123 Aloha St", "2€/ώρα"),
                new ParkingSpot("Parking 2", 21.3072, -157.8585, false, "123 Aloha St", "2€/ώρα"),
                new ParkingSpot("Parking 3", 21.3081, -157.8591, true, "123 Aloha St", "2€/ώρα"),
                new ParkingSpot("Parking 4", 21.3065, -157.8560, false, "123 Aloha St", "2€/ώρα"),
                new ParkingSpot("Parking 5", 21.3078, -157.8602, true, "123 Aloha St", "2€/ώρα"),
                new ParkingSpot("Parking 6", 21.3059, -157.8548, false, "123 Aloha St", "2€/ώρα"),
                new ParkingSpot("Parking 7", 21.3092, -157.8579, true, "123 Aloha St", "2€/ώρα"),
                new ParkingSpot("Parking 8", 21.3080, -157.8556, false, "123 Aloha St", "2€/ώρα"),
                new ParkingSpot("Parking 9", 21.3067, -157.8537, true, "123 Aloha St", "2€/ώρα"),
                new ParkingSpot("Parking 10", 21.3055, -157.8582, true, "123 Aloha St", "2€/ώρα")
        );
    }

    private void showAllParkingSpots(String filter) {
        mMap.clear();
        for (ParkingSpot spot : allSpots) {
            if (filter.equals("Όλες") ||
                    (filter.equals("Διαθέσιμες") && spot.isAvailable) ||
                    (filter.equals("Κατειλημμένες") && !spot.isAvailable)) {

                BitmapDescriptor icon;

                if (spot.isAvailable) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_green);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 150, false);
                    icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);
                } else {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_red);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, false);
                    icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);
                }

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(spot.lat, spot.lng))
                        .title(spot.name + (spot.isAvailable ? " - Διαθέσιμο" : " - Κατειλημμένο"))
                        .icon(icon));

                marker.setTag(spot);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Button btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(v -> Toast.makeText(MapsActivity.this,
                "Η εφαρμογή δείχνει τις διαθέσιμες θέσεις στάθμευσης στον χάρτη", Toast.LENGTH_LONG).show());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        allSpots = getAllSpots();

        LatLng honolulu = new LatLng(21.3069, -157.8583);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(honolulu, 15));

        showAllParkingSpots("Όλες");

        Button btnZoomIn = findViewById(R.id.btnZoomIn);
        Button btnZoomOut = findViewById(R.id.btnZoomOut);

        btnZoomIn.setOnClickListener(v -> mMap.animateCamera(CameraUpdateFactory.zoomIn()));
        btnZoomOut.setOnClickListener(v -> mMap.animateCamera(CameraUpdateFactory.zoomOut()));

        Spinner filterSpinner = findViewById(R.id.availabilityFilter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filter = parent.getItemAtPosition(position).toString();
                showAllParkingSpots(filter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String query = searchBar.getText().toString().trim().toLowerCase();
                for (ParkingSpot spot : allSpots) {
                    if (spot.name.toLowerCase().contains(query)) {
                        LatLng position = new LatLng(spot.lat, spot.lng);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
                        break;
                    }
                }
                return true;
            }
            return false;
        });

        ListView parkingList = findViewById(R.id.parkingList);
        List<String> names = new ArrayList<>();
        for (ParkingSpot spot : allSpots) {
            names.add(spot.name + (spot.isAvailable ? " ✅" : " ❌"));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        parkingList.setAdapter(adapter);

        parkingList.setOnItemClickListener((parent, view, position, id) -> {
            ParkingSpot selected = allSpots.get(position);
            LatLng loc = new LatLng(selected.lat, selected.lng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 17));
        });

        // MARKER CLICK LISTENER
        mMap.setOnMarkerClickListener(marker -> {
            String title = marker.getTitle();
            ParkingSpot matchedSpot = null;
            for (ParkingSpot spot : allSpots) {
                if (title.contains(spot.name)) {
                    matchedSpot = spot;
                    break;
                }
            }

            if (matchedSpot != null) {
                SpotChoiceInfoBottomSheet bottomSheet = SpotChoiceInfoBottomSheet.newInstance(
                        matchedSpot.address,
                        matchedSpot.name,
                        matchedSpot.pricePerHour
                );
                bottomSheet.show(getSupportFragmentManager(), "SpotChoiceInfoBottomSheet");
            }
            return true;
        });
    }

}
