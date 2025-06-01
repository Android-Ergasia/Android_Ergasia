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
    // Χάρτης Google
    private GoogleMap mMap;

    // Λίστα όλων των σημείων στάθμευσης
    private List<ParkingSpot> allSpots = new ArrayList<>();

    // UI στοιχεία
    private EditText searchBar;
    private Spinner filterSpinner;
    private ListView parkingList;
    private Button btnZoomIn, btnZoomOut;

    // Έλεγχος αν είναι admin
    private boolean isAdmin = false;

    public MapFragment() {}

    // Δημιουργία του layout του fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Φορτώνουμε το XML layout του fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Παίρνουμε το isAdmin flag (αν υπάρχει)
        if (getArguments() != null) {
            isAdmin = getArguments().getBoolean("isAdmin", false);
        }

        // Εισάγουμε το SupportMapFragment (κλάση που παρέχεται από τη βιβλιοθήκη Google Maps) μέσα στο fragment
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.map_fragment, mapFragment)
                .commit();

        // Ορίζουμε το callback που θα καλέσει όταν φορτώσει ο χάρτης
        mapFragment.getMapAsync(this);

        // Συνδέουμε τα στοιχεία του UI
        searchBar = view.findViewById(R.id.searchBar);
        filterSpinner = view.findViewById(R.id.availabilityFilter);
        parkingList = view.findViewById(R.id.parkingList);
        btnZoomIn = view.findViewById(R.id.btnZoomIn);
        btnZoomOut = view.findViewById(R.id.btnZoomOut);

        return view;
    }

    // Εκτελείται κάθε φορά που επιστρέφουμε σε αυτό το Fragment
    @Override
    public void onResume() {
        super.onResume();

        // Παίρνουμε όλα τα σημεία στάθμευσης από τον server
        fetchAllSpotsFromServer(() -> showAllParkingSpots("Όλες"));

        // Τίτλος στο action bar
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle("Χάρτης Στάθμευσης");
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    // Όταν είναι έτοιμος ο χάρτης, εκτελείται ο παρακάτω κώδικας
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Κεντράρουμε τον χάρτη στην αρχική θέση (στη Χονολουλού)
        LatLng initial = new LatLng(21.3069, -157.8583);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initial, 15));

        // Φόρτωση όλων των θέσεων
        fetchAllSpotsFromServer(() -> showAllParkingSpots("Όλες"));

        // Κουμπιά zoom
        btnZoomIn.setOnClickListener(v -> mMap.animateCamera(CameraUpdateFactory.zoomIn()));
        btnZoomOut.setOnClickListener(v -> mMap.animateCamera(CameraUpdateFactory.zoomOut()));

        // Ρύθμιση φίλτρου διαθεσιμότητας
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.availability_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        // Όταν ο χρήστης επιλέγει ένα φίλτρο από το Spinner (π.χ. διαθέσιμες), δείχνουμε μόνο τις αντίστοιχες θέσεις στο χάρτη
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filter = parent.getItemAtPosition(position).toString();
                showAllParkingSpots(filter);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Όταν ο χρήστης πατήσει το κουμπί "Αναζήτηση" ή το Enter στο πληκτρολόγιο
        searchBar.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {

            // Ελέγχουμε αν η ενέργεια είναι "Αναζήτηση" ή πάτημα του Enter
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                // Παίρνουμε το κείμενο που πληκτρολόγησε ο χρήστης (και το μετατρέπουμε σε πεζά)
                String query = searchBar.getText().toString().trim().toLowerCase();

                // Ψάχνει μέσα σε όλες τις θέσεις στάθμευσης για να βρει κάποια που να περιέχει το κείμενο αναζήτησης.
                // Αν βρεθεί, μετακινεί τον χάρτη ώστε να δείξει εκείνο το σημείο με κοντινό ζουμ.
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


        // Όταν ο χρήστης πατήσει (tap) σε μια θέση από τη λίστα των αποτελεσμάτων
        // μετακινούμε την κάμερα του χάρτη σε αυτή τη θέση με μεγαλύτερο ζουμ.
        parkingList.setOnItemClickListener((parent, view, position, id) -> {
            ParkingSpot selected = allSpots.get(position);
            LatLng loc = new LatLng(selected.lat, selected.lng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 17));
        });

        // Ψάχνουμε στη λίστα όλων των θέσεων ποια αντιστοιχεί στον marker που πατήσαμε και την αποθηκεύουμε
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
                    // Αν είναι admin, ανοίγει το admin bottom sheet.
                    AdminSpotEditBottomSheet.newInstance(matched)
                            .show(getParentFragmentManager(), "AdminSpotEditBottomSheet");
                } else {
                    // Αν είναι χρήστης
                    // Αν η θέση δεν είναι διαθέσιμη, δείχνουμε μήνυμα "Κατειλημμένη θέση".
                    if (!matched.isAvailable) {
                        Toast.makeText(requireContext(), "Η θέση \"" + matched.name + "\" δεν είναι διαθέσιμη.", Toast.LENGTH_LONG).show();
                    } else {
                        // Αν η θέση είναι διαθέσιμη, ανοίγει το spot info bottom sheet με πληροφορίες για τη θέση.
                        SpotChoiceInfoBottomSheet bottomSheet = SpotChoiceInfoBottomSheet.newInstance(
                                matched.address, matched.name, matched.pricePerHour);
                        bottomSheet.show(getParentFragmentManager(), "SpotChoiceInfoBottomSheet");
                    }
                }
            }

            return true;
        });
    }

    // Συνάρτηση που ανακτά όλα τα σημεία στάθμευσης από τον server
    private void fetchAllSpotsFromServer(Runnable callback) {
        // Δημιουργούμε νέο νήμα ώστε η δικτυακή κλήση να μην μπλοκάρει το UI thread
        new Thread(() -> {
            HttpHandler handler = new HttpHandler();
            // Κάνουμε HTTP GET αίτηση στο τοπικό backend
            String response = handler.makeGetRequest("http://10.0.2.2/parking_app/get_all_spots.php");

            if (response == null) return;

            List<ParkingSpot> fetchedSpots = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(response);

                // Για κάθε στοιχείο του JSON Array
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    // Δημιουργούμε ένα ParkingSpot με τα δεδομένα του JSON για κάθε στοιχείο του JSON Array
                    ParkingSpot spot = new ParkingSpot(
                            obj.getInt("id"),
                            obj.getString("name"),
                            obj.getDouble("lat"),
                            obj.getDouble("lng"),
                            obj.getInt("is_available") == 1,
                            obj.getString("price_per_hour"),
                            obj.getString("address")
                    );

                    // Προσθέτουμε τη θέση στη λίστα που μόλις δημιουργήσαμε
                    fetchedSpots.add(spot);
                }

                // Ενημερώνουμε την λίστα όλων των θέσεων (allSpots) στο κύριο νήμα (UI thread)
                requireActivity().runOnUiThread(() -> {
                    allSpots = fetchedSpots;
                    callback.run();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Προβολή θέσεων στο χάρτη και τη λίστα, με βάση φίλτρο
    private void showAllParkingSpots(String filter) {
        // Καθαρίζουμε προηγούμενα markers
        mMap.clear();

        List<String> names = new ArrayList<>();
        for (ParkingSpot spot : allSpots) {
            if (filter.equals("Όλες") ||
                    (filter.equals("Διαθέσιμες") && spot.isAvailable) ||
                    (filter.equals("Κατειλημμένες") && !spot.isAvailable)) {

                // Επιλογή εικονιδίου με βάση διαθεσιμότητα
                Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(),
                        spot.isAvailable ? R.drawable.marker_green : R.drawable.marker_red);
                Bitmap scaled = Bitmap.createScaledBitmap(iconBitmap, 100, 150, false);
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(scaled);

                // Προσθήκη marker στο χάρτη
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(spot.lat, spot.lng))
                        .title(spot.name + (spot.isAvailable ? " - Διαθέσιμο" : " - Κατειλημμένο"))
                        .icon(icon));

                marker.setTag(spot);

                names.add(spot.address + (spot.isAvailable ? " ✅" : " ❌"));
            }
        }

        // Ενημέρωση του ListView
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, names);
        parkingList.setAdapter(listAdapter);
    }
}
