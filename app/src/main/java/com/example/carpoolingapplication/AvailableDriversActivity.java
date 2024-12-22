package com.example.carpoolingapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class AvailableDriversActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<String> driver_usernames, vehicle_brands, vehicle_models, licence_plates;
    ArrayList<Double> prices, ratings;
    ArrayList<Integer> driver_ids, rated;
    Database db;
    AvailableDriversAdapter adapter;
    Toolbar toolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_available_drivers);
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

        db = new Database(getApplicationContext(), "carpool", null, 1);
        driver_usernames = new ArrayList<>();
        driver_ids = new ArrayList<>();
        vehicle_brands = new ArrayList<>();
        vehicle_models = new ArrayList<>();
        licence_plates = new ArrayList<>();
        prices = new ArrayList<>();
        ratings = new ArrayList<>();
        rated = new ArrayList<>();
        recyclerView = findViewById(R.id.availableDriversRecyclerView);
        adapter = new AvailableDriversAdapter(this, driver_ids ,driver_usernames, vehicle_brands,vehicle_models, licence_plates, prices, ratings, rated);
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
        Intent intent = new Intent(AvailableDriversActivity.this, CustomerActivity.class);
        startActivity(intent);
    }

    private void displaydata() {
        Cursor cursor = db.getDriversData();
        if(cursor.getCount() == 0) {
            findViewById(R.id.information).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else {
            while(cursor.moveToNext()) {
                driver_ids.add(cursor.getInt(1));
                driver_usernames.add(cursor.getString(2));
                vehicle_brands.add(cursor.getString(3));
                vehicle_models.add(cursor.getString(4));
                licence_plates.add(cursor.getString(5));
                prices.add(cursor.getDouble(6));
                ratings.add(cursor.getDouble(7));
                rated.add(cursor.getInt(8));
            }
            findViewById(R.id.information).setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}