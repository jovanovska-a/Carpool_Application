package com.example.carpoolingapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChangeDriverIntervalsActivity extends AppCompatActivity {
    Toolbar toolbar;
    private RecyclerView intervalsRecyclerView;
    private Button btnAddNewInterval;
    private List<String> start_intervals, end_intervals;
    private IntervalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_driver_intervals);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Database db = new Database(getApplicationContext(), "carpool", null, 1);
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        int driverId = sharedPreferences.getInt("user_id", -1);

        intervalsRecyclerView = findViewById(R.id.intervalsRecyclerView);
        btnAddNewInterval = findViewById(R.id.btnAddNewInterval);

        start_intervals = db.getDriverStartIntervals(driverId);
        end_intervals = db.getDriverEndIntervals(driverId);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new IntervalAdapter(this, start_intervals, end_intervals);

        intervalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        intervalsRecyclerView.setAdapter(adapter);

        btnAddNewInterval.setOnClickListener(v -> {
            Intent intent = new Intent(ChangeDriverIntervalsActivity.this, DriverIntervalsActivity.class);
            intent.putExtra("source_activity", "ChangeDriverIntervalsActivity");
            startActivity(intent);
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
        Intent intent = new Intent(ChangeDriverIntervalsActivity.this, DriverInfoActivity.class);
        startActivity(intent);
        finish();
    }

}