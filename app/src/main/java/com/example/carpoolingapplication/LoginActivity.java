package com.example.carpoolingapplication;

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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    EditText edUsername, edPassword;
    Button driver, passenger;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edUsername = findViewById(R.id.editTextLoginUsername);
        edPassword = findViewById(R.id.editTextLoginPassword);
        driver = findViewById(R.id.driver);
        passenger = findViewById(R.id.passenger);
        tv = findViewById(R.id.textViewNewUser);

        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edUsername.getText().toString();
                String password = edPassword.getText().toString();
                String user_type = "Driver";
                Database db = new Database(getApplicationContext(),"carpool", null, 1);

                if(username.length() == 0 || password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please fill all the details", Toast.LENGTH_SHORT).show();
                } else {
                    if(db.login(username,password, user_type) == 1) {
                        Toast.makeText(getApplicationContext(), "Login Sucess", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        int user_id = db.getUserIdByUsername(username, user_type);
                        editor.putInt("user_id",user_id);
                        editor.putString("username", username);
                        editor.apply();
                        startActivity(new Intent(LoginActivity.this, UserInfoActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edUsername.getText().toString();
                String password = edPassword.getText().toString();
                String user_type = "Passenger";
                Database db = new Database(getApplicationContext(),"carpool", null, 1);

                if(username.length() == 0 || password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please fill all the details", Toast.LENGTH_SHORT).show();
                } else {
                    if(db.login(username,password, user_type) == 1) {
                        Toast.makeText(getApplicationContext(), "Login Sucess", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        int user_id = db.getUserIdByUsername(username, user_type);
                        editor.putInt("user_id",user_id);
                        editor.putString("username", username);
                        editor.apply();
                        startActivity(new Intent(LoginActivity.this, UserInfoActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
}