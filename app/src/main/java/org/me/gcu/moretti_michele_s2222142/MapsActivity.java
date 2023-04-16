//
// Name                 Michele Moretti
// Student ID           S2222142
// Programme of Study   Computing
//
package org.me.gcu.moretti_michele_s2222142;

import static org.me.gcu.moretti_michele_s2222142.MainActivity.earthquakeList;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Button exit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        Button exit_Button = findViewById(R.id.exit_button);
        exit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // close the current activity
            }
        });
    }


    // Earth radius in kilometers
    private static final double EARTH_RADIUS = 6371;

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Glasgow coordinates
        LatLng glasgow = new LatLng(55.86, -4.25);

        // Find nearest earthquake in each direction from Glasgow
        Earthquake nearestNorth = null;
        Earthquake nearestSouth = null;
        Earthquake nearestEast = null;
        Earthquake nearestWest = null;

        // Find earthquake with largest magnitude
        Earthquake largestMagnitude = null;

        for (Earthquake earthquake : earthquakeList) {
            // Earthquake coordinates
            double lat = Double.parseDouble(earthquake.getLatitude());
            double lng = Double.parseDouble(earthquake.getLongitude());

            // Calculate distance between Glasgow and earthquake using Haversine formula
            double dLat = Math.toRadians(lat - glasgow.latitude);
            double dLng = Math.toRadians(lng - glasgow.longitude);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(glasgow.latitude)) * Math.cos(Math.toRadians(lat))
                    * Math.sin(dLng / 2) * Math.sin(dLng / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = EARTH_RADIUS * c;

            // Check north/south direction
            if (nearestNorth == null || lat > Double.parseDouble(nearestNorth.getLatitude())) {
                nearestNorth = earthquake;
            }
            if (nearestSouth == null || lat < Double.parseDouble(nearestSouth.getLatitude())) {
                nearestSouth = earthquake;
            }

            // Check east/west direction
            if (nearestEast == null || lng > Double.parseDouble(nearestEast.getLongitude())) {
                nearestEast = earthquake;
            }
            if (nearestWest == null || lng < Double.parseDouble(nearestWest.getLongitude())) {
                nearestWest = earthquake;
            }

            // Check largest magnitude
            if (largestMagnitude == null || earthquake.getMagnitude() > largestMagnitude.getMagnitude()) {
                largestMagnitude = earthquake;
            }
        }

        // Add markers for nearest earthquakes
        LatLng northLatLng = new LatLng(Double.parseDouble(nearestNorth.getLatitude()), Double.parseDouble(nearestNorth.getLongitude()));
        mMap.addMarker(new MarkerOptions().position(northLatLng).title("Nearest earthquake to Glasgow (north)"));

        LatLng southLatLng = new LatLng(Double.parseDouble(nearestSouth.getLatitude()), Double.parseDouble(nearestSouth.getLongitude()));
        mMap.addMarker(new MarkerOptions().position(southLatLng).title("Nearest earthquake to Glasgow (south)"));

        LatLng eastLatLng = new LatLng(Double.parseDouble(nearestEast.getLatitude()), Double.parseDouble(nearestEast.getLongitude()));
        mMap.addMarker(new MarkerOptions().position(eastLatLng).title("Nearest earthquake to Glasgow (east)"));

        LatLng westLatLng = new LatLng(Double.parseDouble(nearestWest.getLatitude()), Double.parseDouble(nearestWest.getLongitude()));
        mMap.addMarker(new MarkerOptions().position(westLatLng).title("Nearest earthquake to Glasgow (west)"));

        // Add marker for earthquake with largest magnitude
        LatLng largestLatLng = new LatLng(Double.parseDouble(largestMagnitude.getLatitude()), Double.parseDouble(largestMagnitude.getLongitude()));
        mMap.addMarker(new MarkerOptions().position(largestLatLng).title("Largest magnitude earthquake"));
    }

}