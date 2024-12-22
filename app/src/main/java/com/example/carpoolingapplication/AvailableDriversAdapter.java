package com.example.carpoolingapplication;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AvailableDriversAdapter extends RecyclerView.Adapter<AvailableDriversAdapter.MyViewHolder> {
    private Context context;
    private ArrayList driver_id, driver_username, vehicle_brand, vehicle_model, licence_plate, price, rating, rated;

    public AvailableDriversAdapter(Context context, ArrayList driver_id, ArrayList driver_username, ArrayList vehicle_brand, ArrayList vehicle_model, ArrayList licence_plate, ArrayList price, ArrayList rating, ArrayList rated) {
        this.context = context;
        this.driver_id = driver_id;
        this.driver_username = driver_username;
        this.vehicle_brand = vehicle_brand;
        this.vehicle_model = vehicle_model;
        this.licence_plate = licence_plate;
        this.price = price;
        this.rating = rating;
        this.rated = rated;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.avilable_driver, parent, false);
        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.driver_username.setText(String.valueOf(driver_username.get(position)));
        holder.vehicle_brand.setText(String.valueOf(vehicle_brand.get(position)));
        holder.vehicle_model.setText(vehicle_model.get(position)+", ");
        holder.licence_plate.setText(String.valueOf(licence_plate.get(position)));
        holder.price.setText("Price: "+price.get(position)+" $");
        holder.ratingBar.setRating(Float.parseFloat(rating.get(position).toString()));
        if((int) rated.get(position) == 0) {
            holder.ratingBar.setVisibility(View.GONE);
            holder.ratingInfo.setVisibility(View.VISIBLE);
        } else {
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.ratingInfo.setVisibility(View.GONE);
        }

        holder.btn.setOnClickListener(v -> {
            int selectedDriverId = (int) driver_id.get(position);
            String selectedDriverUsername = driver_username.get(position).toString();
            double selectedPrice = Double.parseDouble(price.get(position).toString());

            SharedPreferences sharedPreferences = context.getSharedPreferences("shared_prefs", MODE_PRIVATE);
            int userId = sharedPreferences.getInt("user_id", -1);
            String passengerUsername = sharedPreferences.getString("username", "");
            String startLocation = sharedPreferences.getString("start_location", "");
            String endLocation = sharedPreferences.getString("end_location", "");
            String date_time =  new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            Database db = new Database(context, "carpool", null, 1);
            int rideId = (int) db.addRide(selectedDriverId, userId, selectedDriverUsername, passengerUsername, startLocation, endLocation, date_time, selectedPrice);
            Intent intent = new Intent(context, RateRideActivity.class);
            intent.putExtra("rideId", rideId);
            intent.putExtra("rated_user_id", selectedDriverId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return driver_username.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView driver_username, vehicle_brand, vehicle_model, licence_plate, price, ratingInfo;
        Button btn;
        RatingBar ratingBar;
         public MyViewHolder(@NonNull View itemView) {
            super(itemView);
             driver_username = itemView.findViewById(R.id.driverName);
             vehicle_brand = itemView.findViewById(R.id.vehicleBrand);
             vehicle_model = itemView.findViewById(R.id.vehicleModel);
             licence_plate = itemView.findViewById(R.id.licensePlate);
             price = itemView.findViewById(R.id.price);
             btn = itemView.findViewById(R.id.acceptButton);
             ratingBar = itemView.findViewById(R.id.ratingBar);
             ratingInfo = itemView.findViewById(R.id.ratingInfo);
         }
    }
}
