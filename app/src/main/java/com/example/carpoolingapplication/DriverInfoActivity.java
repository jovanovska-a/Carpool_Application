package com.example.carpoolingapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class DriverInfoActivity extends AppCompatActivity {
    Toolbar toolbar;
    CardView btnViewRide, btnRateRide, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_info);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btn = findViewById(R.id.logoutButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // This will clear all saved data
                editor.apply(); // Save the changes
                Intent intent = new Intent(DriverInfoActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Drawable overflowIcon = toolbar.getOverflowIcon();
        if (overflowIcon != null) {
            overflowIcon.setTint(Color.WHITE);
        }

        btnViewRide = findViewById(R.id.viewRide);
        btnRateRide = findViewById(R.id.rateRide);
        btnProfile= findViewById(R.id.profile);

        btnViewRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverInfoActivity.this, ViewRideActivity.class);
                startActivity(intent);
            }
        });

        btnRateRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverInfoActivity.this, UnratedRidesActivity.class);
                startActivity(intent);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverInfoActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String[] menuOptions = getResources().getStringArray(R.array.driver_menu_options);

        for (int i = 0; i < menuOptions.length; i++) {
            menu.add(0, i, Menu.NONE, menuOptions[i]);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                changeDriverInfo();
                return true;
            case 1:
                changeDriverIntervals();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changeDriverInfo() {
         Intent intent = new Intent(DriverInfoActivity.this, ChangeDriverInfoActivity.class);
         startActivity(intent);
    }

    public void changeDriverIntervals() {
         Intent intent = new Intent(DriverInfoActivity.this, ChangeDriverIntervalsActivity.class);
         startActivity(intent);
    }

}
