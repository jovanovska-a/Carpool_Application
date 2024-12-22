package com.example.carpoolingapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvUsername, tvFirstName, tvLastName, tvBirthDate, tvAddress, tvEmail, tvRating;
    Button btn;
    Database db;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvUsername = findViewById(R.id.tvUsername);
        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvBirthDate = findViewById(R.id.tvBirthDate);
        tvAddress = findViewById(R.id.tvAddress);
        tvEmail = findViewById(R.id.tvEmail);
        tvRating = findViewById(R.id.tvRating);
        btn = findViewById(R.id.btnViewDriverInfo);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        db = new Database(getApplicationContext(), "carpool", null, 1);
        String user_type = db.getUserTypeById(userId);
        if(user_type.equals("Passenger")) {
            btn.setVisibility(View.GONE);
        } else {
            btn.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, DriveActivity.class);
                startActivity(intent);
            });
        }

        setProfileData(userId);
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
            Intent intent = new Intent(ProfileActivity.this, CustomerActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(ProfileActivity.this, DriverInfoActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void setProfileData(int userId) {
        Cursor cursor = db.getUserData(userId);

        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(0);
            String email = cursor.getString(1);
            String firstName = cursor.getString(2);
            String lastName = cursor.getString(3);
            String birthDate = cursor.getString(4);
            String address = cursor.getString(5);
            String rating = cursor.getString(6);

            tvUsername.setText(username);
            tvFirstName.setText(firstName);
            tvLastName.setText(lastName);
            tvBirthDate.setText(birthDate);
            tvAddress.setText(address);
            tvEmail.setText(email);
            tvRating.setText(rating);

            cursor.close();
        }
    }

}