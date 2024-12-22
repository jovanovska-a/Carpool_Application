package com.example.carpoolingapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DriverDetailsActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText edVehicleBrand, edVehicleModel, edPrice, edLicencePlate;
    Button btnSubmit;
    TextView tvUsername, tvFillDetails;
    int driverId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        driverId = sharedPreferences.getInt("user_id", -1);
        String username = sharedPreferences.getString("username", "");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvUsername = findViewById(R.id.tvUsername);
        tvFillDetails = findViewById(R.id.tvFillDetails);
        edVehicleBrand = findViewById(R.id.edVehicleBrand);
        edVehicleModel = findViewById(R.id.edVehicleModel);
        edPrice = findViewById(R.id.edPrice);
        edLicencePlate = findViewById(R.id.edLicencePlate);
        btnSubmit = findViewById(R.id.btnSubmit);

        Database db = new Database(getApplicationContext(), "carpool", null, 1);
        if (db.isDriverDetailsExist(driverId)){
            Intent intent = new Intent(DriverDetailsActivity.this, DriverIntervalsActivity.class);
            intent.putExtra("source_activity", "DriverDetailsActivity");
            startActivity(intent);
            finish();
        } else {
            btnSubmit.setVisibility(View.VISIBLE);
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String vehicle_brand = edVehicleBrand.getText().toString();
                    String vehicle_model = edVehicleModel.getText().toString();
                    String licence_plate = edLicencePlate.getText().toString();
                    String price = edPrice.getText().toString();

                    if (vehicle_model.isEmpty() || price.isEmpty() || vehicle_brand.isEmpty() || licence_plate.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please fill in all the details", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Driver id" + driverId + "Username" + username + "vehicle_brand" + vehicle_brand + "licence_plate" + licence_plate + "price" + Double.parseDouble(price), Toast.LENGTH_LONG).show();
                        db.insertDriverDetails(driverId, username, vehicle_brand, vehicle_model, licence_plate, Double.parseDouble(price));
                        Toast.makeText(getApplicationContext(), "Details inserted successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(DriverDetailsActivity.this, DriverIntervalsActivity.class);
                        intent.putExtra("source_activity", "DriverDetailsActivity");
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }
}