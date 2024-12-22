package com.example.carpoolingapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewRideActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<String> start_locations, end_locations, dates_times;
    ArrayList<Double> prices;
    ArrayList<Integer> ride_ids, driver_ids, passenger_ids;
    Database db;
    RidesAdapter adapter;
    private Toolbar toolbar;
    String activity;
    SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_ride);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        db = new Database(getApplicationContext(), "carpool", null, 1);

        ride_ids = new ArrayList<>();
        driver_ids = new ArrayList<>();
        passenger_ids = new ArrayList<>();
        start_locations = new ArrayList<>();
        end_locations = new ArrayList<>();
        dates_times = new ArrayList<>();
        prices = new ArrayList<>();

        activity = "ViewRideActivity";
        recyclerView = findViewById(R.id.rv_rides);
        adapter = new RidesAdapter(this, ride_ids, driver_ids, passenger_ids, start_locations, end_locations, dates_times, prices, activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        displaydata();
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
        int userId = sharedPreferences.getInt("user_id", -1);
        if (db.getUserTypeById(userId).equals("Driver")) {
            Intent intent = new Intent(ViewRideActivity.this, DriverInfoActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(ViewRideActivity.this, CustomerActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void displaydata() {
        int userId = sharedPreferences.getInt("user_id", -1);
        Cursor cursor= db.getRidesData(userId, activity);
        if(cursor.getCount() == 0) {
            findViewById(R.id.information).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else {
            while(cursor.moveToNext()) {
                ride_ids.add(cursor.getInt(0));
                driver_ids.add(cursor.getInt(1));
                passenger_ids.add(cursor.getInt(2));
                start_locations.add(cursor.getString(3));
                end_locations.add(cursor.getString(4));
                dates_times.add(cursor.getString(5));
                prices.add(cursor.getDouble(6));
            }
            findViewById(R.id.information).setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}