package com.example.ergasiaandroid.Fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ergasiaandroid.HttpHandler;
import com.example.ergasiaandroid.ParkingSpot;
import com.example.ergasiaandroid.R;
import com.example.ergasiaandroid.SpotChoiceInfoBottomSheet;
import com.example.ergasiaandroid.AdminSpotEditBottomSheet;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<ParkingSpot> allSpots = new ArrayList<>();

    private EditText searchBar;
    private Spinner filterSpinner;
    private ListView parkingList;
    private Button btnZoomIn, btnZoomOut;

    private boolean isAdmin = false;

    public MapFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        if (getArguments() != null) {
            isAdmin = getArguments().getBoolean("isAdmin", false);
        }

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.map_fragment, mapFragment)
                .commit();

        mapFragment.getMapAsync(this);

        searchBar = view.findViewById(R.id.searchBar);
        filterSpinner = view.findViewById(R.id.availabilityFilter);
        parkingList = view.findViewById(R.id.parkingList);
        btnZoomIn = view.findViewById(R.id.btnZoomIn);
        btnZoomOut = view.findViewById(R.id.btnZoomOut);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchAllSpotsFromServer(() -> showAllParkingSpots("Όλες"));
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle("Χάρτης Στάθμευσης");
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng initial = new LatLng(21.3069, -157.8583);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initial, 15));

        fetchAllSpotsFromServer(() -> showAllParkingSpots("Όλες"));

        btnZoomIn.setOnClickListener(v -> mMap.animateCamera(CameraUpdateFactory.zoomIn()));
        btnZoomOut.setOnClickListener(v -> mMap.animateCamera(CameraUpdateFactory.zoomOut()));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.availability_options, android.R.layout.simple_spinner_item);
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
                if (isAdmin) {
                    AdminSpotEditBottomSheet.newInstance(matched).show(getParentFragmentManager(), "AdminSpotEditBottomSheet");
                } else {
                    if (!matched.isAvailable) {
                        Toast.makeText(requireContext(), "Η θέση \"" + matched.name + "\" δεν είναι διαθέσιμη.", Toast.LENGTH_LONG).show();
                    } else {
                        SpotChoiceInfoBottomSheet bottomSheet = SpotChoiceInfoBottomSheet.newInstance(
                                matched.address, matched.name, matched.pricePerHour);
                        bottomSheet.show(getParentFragmentManager(), "SpotChoiceInfoBottomSheet");
                    }
                }
            }
            return true;
        });
    }

    private void fetchAllSpotsFromServer(Runnable callback) {
        new Thread(() -> {
            HttpHandler handler = new HttpHandler();
            String response = handler.makeGetRequest("http://10.0.2.2/parking_app/get_all_spots.php");

            if (response == null) return;

            List<ParkingSpot> fetchedSpots = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    ParkingSpot spot = new ParkingSpot(
                            obj.getInt("id"),
                            obj.getString("name"),
                            obj.getDouble("lat"),
                            obj.getDouble("lng"),
                            obj.getInt("is_available") == 1,
                            obj.getString("price_per_hour"),
                            obj.getString("address")
                    );
                    fetchedSpots.add(spot);
                }

                requireActivity().runOnUiThread(() -> {
                    allSpots = fetchedSpots;
                    callback.run();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showAllParkingSpots(String filter) {
        mMap.clear();
        List<String> names = new ArrayList<>();
        for (ParkingSpot spot : allSpots) {
            if (filter.equals("Όλες") ||
                    (filter.equals("Διαθέσιμες") && spot.isAvailable) ||
                    (filter.equals("Κατειλημμένες") && !spot.isAvailable)) {

                Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(),
                        spot.isAvailable ? R.drawable.marker_green : R.drawable.marker_red);
                Bitmap scaled = Bitmap.createScaledBitmap(iconBitmap, 100, 150, false);
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(scaled);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(spot.lat, spot.lng))
                        .title(spot.name + (spot.isAvailable ? " - Διαθέσιμο" : " - Κατειλημμένο"))
                        .icon(icon));

                marker.setTag(spot);
                names.add(spot.address + (spot.isAvailable ? " ✅" : " ❌"));
            }
        }

        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, names);
        parkingList.setAdapter(listAdapter);
    }
}
