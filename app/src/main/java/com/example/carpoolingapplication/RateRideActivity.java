package com.example.carpoolingapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class RateRideActivity extends AppCompatActivity {
    private Toolbar toolbar;
    int user_who_rates, rideId, rated_user;
    float rating;
    Database db;
    RatingBar ratingBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rate_ride);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new Database(getApplicationContext(), "carpool", null, 1);

        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        user_who_rates = sharedPreferences.getInt("user_id", -1);
        rideId = getIntent().getIntExtra("rideId", -1);
        rated_user = getIntent().getIntExtra("rated_user_id", -1);

        ratingBar = findViewById(R.id.ratingBar);
        Button btn = findViewById(R.id.rateBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = ratingBar.getRating();
                db.addRating(rideId, user_who_rates, rated_user, rating);
                Intent intent = new Intent(RateRideActivity.this, ViewRideActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}