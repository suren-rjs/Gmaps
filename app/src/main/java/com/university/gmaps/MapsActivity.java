package com.university.gmaps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.university.gmaps.helper.GmapsLocation;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, StreetViewPanorama.OnStreetViewPanoramaChangeListener {
    private final GmapsLocation gmapsLocation = GmapsLocation.getInstance();
    private StreetViewPanoramaView streetView;
    private StreetViewPanorama streetViewPanorama;
    private boolean removeMapView = false;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.university.gmaps.databinding.ActivityMapsBinding binding = com.university.gmaps.databinding.ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.locationMap);
        mapFragment.getMapAsync(this);

        // Initialize the FusedLocationProviderClient
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up the location request
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000); // Request location updates every 5 seconds

        LocationCallback mLocationCallback = new LocationCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // Handle location updates
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update the map with the new location
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng latLng = new LatLng(latitude, longitude);
                    gmapsLocation.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));
                    gmapsLocation.addCurrentLocationMarker(latLng);

                    // Use the Geocoder class to get the current address
                    Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addresses != null && addresses.size() > 0) {
                        ConstraintLayout constraintLayout = findViewById(R.id.locationId);
                        Address currentAddress = addresses.get(0);
                        TextView addressText = constraintLayout.findViewById(R.id.address);
                        TextView lat = constraintLayout.findViewById(R.id.lat);
                        TextView lon = constraintLayout.findViewById(R.id.lon);
                        addressText.setText("Address : " + currentAddress.getAddressLine(0));
                        lat.setText("Latitude : " + latitude);
                        lon.setText("Longitude : " + longitude);
                    }
                }
            }
        };

        // Request location updates
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }

        streetView = findViewById(R.id.street_view);
        streetView.onCreate(savedInstanceState);
        streetView.getStreetViewPanoramaAsync(streetViewPanorama -> {
            // Use the streetViewPanorama object here
            this.streetViewPanorama = streetViewPanorama;
            this.streetViewPanorama.setOnStreetViewPanoramaChangeListener(MapsActivity.this);
            this.streetViewPanorama.setPosition(gmapsLocation.latLng);
            this.streetViewPanorama.setPanningGesturesEnabled(true);
            this.streetViewPanorama.setUserNavigationEnabled(true);
            this.streetViewPanorama.setZoomGesturesEnabled(true);
            this.streetViewPanorama.setStreetNamesEnabled(true);
        });
//        streetView.setVisibility(View.GONE);

        Button streetViewButton = findViewById(R.id.street_view_btn);

        streetViewButton.setOnClickListener(v -> {
            removeMapView = !removeMapView;
            if (removeMapView) {
                mapFragment.getView().setVisibility(View.GONE);
                streetView.setVisibility(View.VISIBLE);
                streetViewButton.setText("Show Map View");
            } else {
                streetView.setVisibility(View.GONE);
                mapFragment.getView().setVisibility(View.VISIBLE);
                streetViewButton.setText("Show Street View");
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gmapsLocation.mMap = googleMap;
    }

    @Override
    public void onStreetViewPanoramaChange(@NonNull StreetViewPanoramaLocation streetViewPanoramaLocation) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
