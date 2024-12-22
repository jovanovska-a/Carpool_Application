package com.example.carpoolingapplication;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;

public class RideOnMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar toolbar;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ride_on_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        String startLocation = intent.getStringExtra("start_location");
        String endLocation = intent.getStringExtra("end_location");

        if (startLocation != null && endLocation != null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        } else {
            Toast.makeText(this, "Invalid locations", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();
        String startLocation = intent.getStringExtra("start_location");
        String endLocation = intent.getStringExtra("end_location");

        if (startLocation != null && endLocation != null) {
            LatLng startLatLng = getLatLngFromLocation(startLocation);
            LatLng endLatLng = getLatLngFromLocation(endLocation);

            if (mMap != null) {
                mMap.addMarker(new MarkerOptions().position(startLatLng).title("Start Location"));
                mMap.addMarker(new MarkerOptions().position(endLatLng).title("End Location"));

                Polyline polyline = mMap.addPolyline(new PolylineOptions()
                        .add(startLatLng, endLatLng)
                        .width(10)
                        .color(Color.RED));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 15));
            }
        }
    }

    private LatLng getLatLngFromLocation(String location) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();
                return new LatLng(latitude, longitude);
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                return new LatLng(0, 0); // Return a default location
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Geocoder service unavailable", Toast.LENGTH_SHORT).show();
            return new LatLng(0, 0);
        }
    }
}
