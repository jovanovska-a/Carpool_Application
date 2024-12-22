package com.example.carpoolingapplication;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class DriverIntervalsActivity extends AppCompatActivity {
    Toolbar toolbar;
    Database db;
    int driverId;
    Button edStartInterval, edEndInterval;
    Button btnSubmitInterval, btnSubmitDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_intervals);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Database db = new Database(getApplicationContext(), "carpool", null, 1);
        btnSubmitInterval = findViewById(R.id.btnSubmitInterval);
        btnSubmitDone = findViewById(R.id.btnSubmitDone);
        edEndInterval = findViewById(R.id.edEndInterval);
        edStartInterval = findViewById(R.id.edStartInterval);
        edStartInterval.setOnClickListener(v -> showTimePicker(edStartInterval));
        edEndInterval.setOnClickListener(v -> showTimePicker(edEndInterval));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        driverId = sharedPreferences.getInt("user_id", -1); // Get the driver ID or -1 if not found

        Intent intent = getIntent();
        String sourceActivity = intent.getStringExtra("source_activity");

        if (db.hasDriverIntervals(driverId) && "DriverDetailsActivity".equals(sourceActivity)) {
            startActivity(new Intent(DriverIntervalsActivity.this, DriverInfoActivity.class));
            finish();
        }
        else {
            btnSubmitInterval.setOnClickListener(v -> {
                String startInterval = edStartInterval.getText().toString();
                String endInterval = edEndInterval.getText().toString();

                if (!startInterval.isEmpty() && !endInterval.isEmpty()) {
                    db.addDriverInterval(driverId, startInterval, endInterval);

                    edStartInterval.setText("");
                    edEndInterval.setText("");

                    Toast.makeText(DriverIntervalsActivity.this, "Interval added.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DriverIntervalsActivity.this, "Please fill both fields.", Toast.LENGTH_SHORT).show();
                }
            });

            btnSubmitDone.setOnClickListener(v -> {
                if("ChangeDriverIntervalsActivity".equals(sourceActivity)) {
                    startActivity(new Intent(DriverIntervalsActivity.this, ChangeDriverIntervalsActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(DriverIntervalsActivity.this, DriverInfoActivity.class));
                    finish();
                }
            });
        }
    }

    private void showTimePicker(final Button timeEditText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (TimePicker view, int hourOfDay, int minuteOfHour) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    timeEditText.setText(time);
                }, hour, minute, true);
        timePickerDialog.show();
    }
}