package com.example.carpoolingapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;

public class DriveActivity extends AppCompatActivity {

    private Spinner timeIntervalsSpinner;
    private TextView driverDetailsTextView, vehicleInfoTextView, vehicleBrandTextView, vehicleModelTextView, licensePlateTextView, priceTextView;
    private Database db;
    private Toolbar toolbar;
    private int driverId; // You would get this from the current logged-in driver or selected driver

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vehicleInfoTextView = findViewById(R.id.tvVehicleInfo);
        vehicleBrandTextView = findViewById(R.id.tvVehicleBrand);
        vehicleModelTextView = findViewById(R.id.tvVehicleModel);
        licensePlateTextView = findViewById(R.id.tvLicensePlate);
        priceTextView = findViewById(R.id.tvPrice);
        timeIntervalsSpinner = findViewById(R.id.spinnerTimeInterval); // Spinner for time intervals

        db = new Database(this, "carpool", null, 1);

        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        int driverId = sharedPreferences.getInt("user_id", -1);

        ArrayList<String> timeIntervals = db.getDriverTimeIntervals(driverId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeIntervals);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeIntervalsSpinner.setAdapter(adapter);

        getDriverDetails(driverId);
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

    public void Home() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        String user_type = db.getUserTypeById(userId);
        if(user_type.equals("Passenger")) {
            Intent intent = new Intent(DriveActivity.this, CustomerActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(DriveActivity.this, DriverInfoActivity.class);
            startActivity(intent);
            finish();
        }

    }


    // Method to get driver details
    private void getDriverDetails(int driverId) {
        SQLiteDatabase database = db.getReadableDatabase();
        String query = "SELECT vehicle_brand, vehicle_model, licence_plate, price FROM driver_details WHERE driver_id = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(driverId)});

        String[] driverDetails = new String[]{"Details not available", "Details not available", "Details not available", "Details not available"}; // Default messages

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String vehicleBrand = cursor.getString(cursor.getColumnIndex("vehicle_brand"));
            @SuppressLint("Range") String vehicleModel = cursor.getString(cursor.getColumnIndex("vehicle_model"));
            @SuppressLint("Range") String licencePlate = cursor.getString(cursor.getColumnIndex("licence_plate"));
            @SuppressLint("Range") double price = cursor.getDouble(cursor.getColumnIndex("price"));

            vehicleBrandTextView.setText(vehicleBrand);
            vehicleModelTextView.setText(vehicleModel);
            licensePlateTextView.setText(licencePlate);
            priceTextView.setText("$ "+price);

        }
        cursor.close();
        db.close();
    }
}
