package com.example.aaron.maptest2;

/*
 * Created by Zachary Aaron Greene on 4/12/2018.
 *
 * This activity displays a fetched address on the map and zooms to it.
 * It was plenty difficult even getting THAT working. I spent at least 6 hours hitting my head against the wall,
 *      trying to figure out how to get this to work initially. Eventually I just imported everything into an entirely
 *      new project where it just magically started to display the map. A lot of time was wasted on this bug.
 *
 * Stuff used for help:
 * https://www.androidauthority.com/get-location-address-android-app-628764/
 * https://github.com/obaro/SimpleGeocodeApp/blob/master/app/src/main/java/com/sample/foo/simplegeocodeapp/MainActivityWithAsyncTask.java
 * https://www.journaldev.com/15676/android-geocoder-reverse-geocoding
 * https://www.androidauthority.com/get-location-address-android-app-628764/
 * https://stackoverflow.com/questions/14827532/waiting-till-the-async-task-finish-its-work
 *
 *
*/


import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    //doubles for addressees location data
    private String firstName;
    private String lastName;
    private double contactLat;
    private double contactLng;
    private double userLat;
    private double userLng;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // get intent data
        Bundle recievedData = new Bundle();
        recievedData = getIntent().getExtras();

        //load intent data into strings
        firstName = recievedData.getString("firstName");
        lastName = recievedData.getString("lastName");

        contactLat = recievedData.getDouble("latitude");
        contactLng = recievedData.getDouble("longitude");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng contactAddress = new LatLng(contactLat, contactLng);
        mMap.addMarker(new MarkerOptions().position(contactAddress).title(firstName + " " + lastName + "'s Address"));

        // if user's location cannot be found
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(contactAddress,15));        // go to address marker
        mMap.animateCamera(CameraUpdateFactory.zoomIn());               // zoom to address marker
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        //if user's location CAN be found
        //zoomFit(contactAddress, userAddress, mMap);
    }

    // an internal class for zooming the camera to fit the addresses.
    private void zoomFit(LatLng address1, LatLng address2, GoogleMap googlemap)
    {
        //create builder for bounds
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //include parameters for builder (addresses)
        builder.include(address1);
        builder.include(address2);

        //build bounds & parameters for camera factory
        LatLngBounds bounds = builder.build();
        int padding = 0; // offset from edges of the map in pixels

        // camera update complete
        CameraUpdate zoom = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        // move and zoom camera to fit these new points, all animated and nice
        googlemap.moveCamera(zoom); // go to address markers
        googlemap.animateCamera(zoom);  // zoom to fit address markers
    }
}
