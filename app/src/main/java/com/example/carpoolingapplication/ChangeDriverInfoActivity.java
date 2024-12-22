package com.example.carpoolingapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChangeDriverInfoActivity extends AppCompatActivity {

    EditText vehicleBrand, vehicleModel, licencePlate, price;
    Button btnUpdateDetails;
    Database db;
    SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    int driverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_driver_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vehicleBrand = findViewById(R.id.edUpdateVehicleBrand);
        vehicleModel = findViewById(R.id.edUpdateVehicleModel);
        licencePlate = findViewById(R.id.edUpdateLicencePlate);
        price = findViewById(R.id.edUpdatePrice);
        btnUpdateDetails = findViewById(R.id.btnUpdateDetails);

        db = new Database(getApplicationContext(), "carpool", null, 1);
        sharedPreferences = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        driverId = sharedPreferences.getInt("user_id", -1);

        loadDriverDetails();

        btnUpdateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDriverDetails();
            }
        });
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
       Intent intent = new Intent(ChangeDriverInfoActivity.this, DriverInfoActivity.class);
       startActivity(intent);
       finish();
    }

    private void loadDriverDetails() {
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT vehicle_brand, vehicle_model, licence_plate, price FROM driver_details WHERE driver_id = ?", new String[]{String.valueOf(driverId)});

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String vehicleBrandName = cursor.getString(cursor.getColumnIndex("vehicle_brand"));
            @SuppressLint("Range") String vehicleModelName = cursor.getString(cursor.getColumnIndex("vehicle_model"));
            @SuppressLint("Range") String licencePlateName = cursor.getString(cursor.getColumnIndex("licence_plate"));
            @SuppressLint("Range") double priceValue = cursor.getDouble(cursor.getColumnIndex("price"));

            vehicleBrand.setText(vehicleBrandName);
            vehicleModel.setText(vehicleModelName);
            licencePlate.setText(licencePlateName);
            price.setText(String.valueOf(priceValue));
            cursor.close();
        }
    }

    private void updateDriverDetails() {
        String updatedVehicleBrand = vehicleBrand.getText().toString().trim();
        String updatedVehicleModel = vehicleModel.getText().toString().trim();
        String updatedLicencePlate = licencePlate.getText().toString().trim();
        String updatedPrice = price.getText().toString().trim();

        if (updatedVehicleBrand.isEmpty() || updatedVehicleModel.isEmpty() || updatedLicencePlate.isEmpty() || updatedPrice.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase database = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("vehicle_brand", updatedVehicleBrand);
        values.put("vehicle_model", updatedVehicleModel);
        values.put("licence_plate", updatedLicencePlate);
        values.put("price", Double.parseDouble(updatedPrice));

        long result = database.update("driver_details", values, "driver_id = ?", new String[]{String.valueOf(driverId)});
        if (result != -1) {
            Toast.makeText(this, "Details updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update details", Toast.LENGTH_SHORT).show();
        }

        database.close();

        Intent intent = new Intent(ChangeDriverInfoActivity.this, DriverInfoActivity.class);
        startActivity(intent);
        finish();
    }
}
