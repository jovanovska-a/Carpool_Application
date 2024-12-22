package com.example.carpoolingapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;
import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class TakeRideActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Toolbar toolbar;
    private TextView startLocationTextView, destinationLocationTextView;
    private GoogleMap gMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Button btn;
    private static final int LOCATION_REQUEST_CODE = 100;
    private double distance;
    private String startLocation, endLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_take_ride);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Drawable overflowIcon = toolbar.getOverflowIcon();
        if (overflowIcon != null) {
            overflowIcon.setTint(Color.WHITE);
        }

        fusedLocationProviderClient = (FusedLocationProviderClient) LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        destinationLocationTextView = findViewById(R.id.destination_location);
        getCurrentLocation();

        btn = findViewById(R.id.see_available_drivers_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH", Locale.getDefault());
                String currentHour = sdf.format(new java.util.Date());

                Database db = new Database(getApplicationContext(), "carpool", null, 1);
                if (distance == 0.0) {
                    Toast.makeText(TakeRideActivity.this, "You haven't chosen a location.", Toast.LENGTH_LONG).show();
                } else {
                    db.updateAvailableDrivers(currentHour, distance);
                    SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("start_location",startLocation);
                    editor.putString("end_location",endLocation);
                    editor.apply();
                   startActivity(new Intent(TakeRideActivity.this, AvailableDriversActivity.class));
                }
            }
        });
    }

    public interface LocationCallback {
        void onLocationRetrieved(LatLng currentLocation);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        enableLocation();
        gMap.setOnMapClickListener(latLng -> {
            gMap.clear();
            gMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));
            getAddressFromLatLng(latLng);
            getCurrentCoordinates(new LocationCallback() {
                @Override
                public void onLocationRetrieved(LatLng currentLocation) {
                    distance = calculateDistance(currentLocation, latLng);
                    distance = Math.round(distance * 10.0) / 10.0;
                }
            });
        });

    }

    private void getCurrentCoordinates(LocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            LatLng currLatLng = new LatLng(latitude, longitude);
                            callback.onLocationRetrieved(currLatLng);
                        } else {
                            Toast.makeText(TakeRideActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
        }
    }


    private double calculateDistance(LatLng start, LatLng destination) {
        final int R = 6371;
        double lat1 = Math.toRadians(start.latitude);
        double lon1 = Math.toRadians(start.longitude);
        double lat2 = Math.toRadians(destination.latitude);
        double lon2 = Math.toRadians(destination.longitude);

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;

    }

    private void enableLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }
        gMap.setMyLocationEnabled(true);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                gMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(TakeRideActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String destinationAddress = address.getAddressLine(0);
                endLocation = destinationAddress;
                destinationLocationTextView.setText(destinationAddress);
            } else {
                destinationLocationTextView.setText("No address found for this location");
            }
        } catch (IOException e) {
            e.printStackTrace();
            destinationLocationTextView.setText("Unable to get address");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation();
            } else {
                Toast.makeText(this, "Location permission is required to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String[] menuOptions = getResources().getStringArray(R.array.menu_options);
        for (int i = 0; i < menuOptions.length; i++) {
            menu.add(0, i, Menu.NONE, menuOptions[i]);
            if (i == 0) {
                menu.getItem(i).setIcon(R.drawable.baseline_apps_24);
                menu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Home();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(TakeRideActivity.this, Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    if (addresses != null && !addresses.isEmpty()) {
                                        Address address = addresses.get(0);
                                        String currentLocation = address.getAddressLine(0);
                                        startLocation = currentLocation;
                                    } else {
                                        startLocationTextView.setText("Unable to retrieve address");
                                    }
                                } catch (IOException e) {
                                    startLocationTextView.setText("Error retrieving address");
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(TakeRideActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
        }
    }
    public void Home() {
        Intent intent = new Intent(TakeRideActivity.this, CustomerActivity.class);
        startActivity(intent);
    }

}