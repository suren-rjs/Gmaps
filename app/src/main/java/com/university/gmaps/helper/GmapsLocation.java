package com.university.gmaps.helper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GmapsLocation {
    private static GmapsLocation gmapsLocation;
    public GoogleMap mMap;
    public Marker mCurrentLocationMarker;
    public LatLng latLng;

    public static GmapsLocation getInstance() {
        if (gmapsLocation == null)
            gmapsLocation = new GmapsLocation();

        return gmapsLocation;
    }

    public void addCurrentLocationMarker(LatLng latLng) {
        this.latLng = latLng;
        if (gmapsLocation.mCurrentLocationMarker != null) {
            gmapsLocation.mCurrentLocationMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        gmapsLocation.mCurrentLocationMarker = gmapsLocation.mMap.addMarker(markerOptions);
    }
}
