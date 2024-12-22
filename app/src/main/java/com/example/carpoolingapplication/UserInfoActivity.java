package com.example.carpoolingapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class UserInfoActivity extends AppCompatActivity {
    private EditText edFirstName, edLastName, edBirthDate, edAddress;
    private Button btnSubmit;
    private TextView tvUsername;
    Database db;
    private Toolbar toolbar;
    private String user_type;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvUsername = findViewById(R.id.tvUsername);
        edFirstName = findViewById(R.id.edFirstName);
        edLastName = findViewById(R.id.edLastName);
        edBirthDate = findViewById(R.id.edBirthDate);
        edAddress = findViewById(R.id.edAddress);
        btnSubmit = findViewById(R.id.btnSubmit);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new Database(getApplicationContext(), "carpool", null, 1);
        sharedPreferences = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        int userId =  sharedPreferences.getInt("user_id", -1);
        String username = sharedPreferences.getString("username", "");
        tvUsername.setText("Welcome, " + username);
        user_type = db.getUserTypeById(userId);

        if (db.ifUserInfoExist(userId)){
            if(user_type.equals("Driver")) {
                Intent intent = new Intent(UserInfoActivity.this, DriverDetailsActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(UserInfoActivity.this, CustomerActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            btnSubmit.setOnClickListener(view -> {
                String firstName = edFirstName.getText().toString().trim();
                String lastName = edLastName.getText().toString().trim();
                String birthDate = edBirthDate.getText().toString().trim();
                String address = edAddress.getText().toString().trim();

                if (firstName.isEmpty() || lastName.isEmpty() || birthDate.isEmpty() || address.isEmpty()) {
                    Toast.makeText(UserInfoActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    db.insertUserInfo(userId, firstName, lastName, birthDate, address);
                    Toast.makeText(getApplicationContext(), "Details inserted successfully", Toast.LENGTH_LONG).show();
                    if(user_type.equals("Driver")) {
                        Intent intent = new Intent(UserInfoActivity.this, DriverDetailsActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(UserInfoActivity.this, CustomerActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }
}