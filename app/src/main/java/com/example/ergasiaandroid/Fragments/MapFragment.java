// MapFragment.java (τελικός, πλήρως λειτουργικός)
package com.example.ergasiaandroid.Fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ergasiaandroid.ParkingSpot;
import com.example.ergasiaandroid.R;
import com.example.ergasiaandroid.SpotChoiceInfoBottomSheet;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.*;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<ParkingSpot> allSpots;

    private EditText searchBar;
    private Spinner filterSpinner;
    private ListView parkingList;
    private Button btnZoomIn, btnZoomOut;

    public MapFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.example.ergasiaandroid.R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(com.example.ergasiaandroid.R.id.map_fragment, mapFragment)
                .commit();

        mapFragment.getMapAsync(this);

        searchBar = view.findViewById(com.example.ergasiaandroid.R.id.searchBar);
        filterSpinner = view.findViewById(com.example.ergasiaandroid.R.id.availabilityFilter);
        parkingList = view.findViewById(com.example.ergasiaandroid.R.id.parkingList);
        btnZoomIn = view.findViewById(com.example.ergasiaandroid.R.id.btnZoomIn);
        btnZoomOut = view.findViewById(com.example.ergasiaandroid.R.id.btnZoomOut);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        allSpots = createAllSpots();

        LatLng initial = new LatLng(21.3069, -157.8583); // Χονολουλού, Χαβάη
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initial, 15));


        showAllParkingSpots("Όλες");

        btnZoomIn.setOnClickListener(v -> mMap.animateCamera(CameraUpdateFactory.zoomIn()));
        btnZoomOut.setOnClickListener(v -> mMap.animateCamera(CameraUpdateFactory.zoomOut()));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                com.example.ergasiaandroid.R.array.availability_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filter = parent.getItemAtPosition(position).toString();
                showAllParkingSpots(filter);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        searchBar.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String query = searchBar.getText().toString().trim().toLowerCase();
                for (ParkingSpot spot : allSpots) {
                    if (spot.name.toLowerCase().contains(query)) {
                        LatLng pos = new LatLng(spot.lat, spot.lng);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 17));
                        break;
                    }
                }
                return true;
            }
            return false;
        });

        List<String> names = new ArrayList<>();
        for (ParkingSpot spot : allSpots) {
            names.add(spot.name + (spot.isAvailable ? " ✅" : " ❌"));
        }
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, names);
        parkingList.setAdapter(listAdapter);

        parkingList.setOnItemClickListener((parent, view, position, id) -> {
            ParkingSpot selected = allSpots.get(position);
            LatLng loc = new LatLng(selected.lat, selected.lng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 17));
        });

        mMap.setOnMarkerClickListener(marker -> {
            String title = marker.getTitle();
            ParkingSpot matched = null;
            for (ParkingSpot spot : allSpots) {
                if (title.contains(spot.name)) {
                    matched = spot;
                    break;
                }
            }

            if (matched != null) {
                if (!matched.isAvailable) {
                    Toast.makeText(requireContext(), "Η θέση \"" + matched.name + "\" δεν είναι διαθέσιμη.", Toast.LENGTH_LONG).show();
                } else {
                    SpotChoiceInfoBottomSheet bottomSheet = SpotChoiceInfoBottomSheet.newInstance(
                            matched.address,
                            matched.name,
                            matched.pricePerHour
                    );
                    bottomSheet.show(getParentFragmentManager(), "SpotChoiceInfoBottomSheet");
                }
            }
            return true;
        });
    }

    private List<ParkingSpot> createAllSpots() {
        return Arrays.asList(
                new ParkingSpot("Parking 1", 21.3060, -157.8570, true, "2€/ώρα", requireContext()),
                new ParkingSpot("Parking 2", 21.3072, -157.8585, false, "2€/ώρα", requireContext()),
                new ParkingSpot("Parking 3", 21.3081, -157.8591, true, "2€/ώρα", requireContext()),
                new ParkingSpot("Parking 4", 21.3065, -157.8560, false, "2€/ώρα", requireContext()),
                new ParkingSpot("Parking 5", 21.3078, -157.8602, true, "2€/ώρα", requireContext()),
                new ParkingSpot("Parking 6", 21.3059, -157.8548, false, "2€/ώρα", requireContext()),
                new ParkingSpot("Parking 7", 21.3092, -157.8579, true, "2€/ώρα", requireContext()),
                new ParkingSpot("Parking 8", 21.3080, -157.8556, false, "2€/ώρα", requireContext()),
                new ParkingSpot("Parking 9", 21.3067, -157.8537, true, "2€/ώρα", requireContext()),
                new ParkingSpot("Parking 10", 21.3055, -157.8582, true, "2€/ώρα", requireContext())
        );
    }

    private void showAllParkingSpots(String filter) {
        mMap.clear();
        for (ParkingSpot spot : allSpots) {
            if (filter.equals("Όλες") ||
                    (filter.equals("Διαθέσιμες") && spot.isAvailable) ||
                    (filter.equals("Κατειλημμένες") && !spot.isAvailable)) {

                Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(),
                        spot.isAvailable ? com.example.ergasiaandroid.R.drawable.marker_green : R.drawable.marker_red);
                Bitmap scaled = Bitmap.createScaledBitmap(iconBitmap, 100, 150, false);
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(scaled);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(spot.lat, spot.lng))
                        .title(spot.name + (spot.isAvailable ? " - Διαθέσιμο" : " - Κατειλημμένο"))
                        .icon(icon));

                marker.setTag(spot);
            }
        }
    }
}