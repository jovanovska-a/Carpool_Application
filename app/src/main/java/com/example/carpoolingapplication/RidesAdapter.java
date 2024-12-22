package com.example.carpoolingapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.RideViewHolder> {
    private Context context;
    private ArrayList ride_ids, driver_ids, passenger_ids, start_locations, end_locations, prices, dates_times;
    String activity;
    public RidesAdapter(Context context, ArrayList ride_ids, ArrayList driver_ids, ArrayList passenger_ids, ArrayList start_locations, ArrayList end_locations, ArrayList dates_times, ArrayList prices, String activity) {
        this.context = context;
        this.ride_ids = ride_ids;
        this.driver_ids = driver_ids;
        this.passenger_ids = passenger_ids;
        this.start_locations = start_locations;
        this.end_locations = end_locations;
        this.prices = prices;
        this.dates_times = dates_times;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.taken_ride, parent, false);
        return new RidesAdapter.RideViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        holder.end_location.setText(String.valueOf(end_locations.get(position)));
        holder.price.setText("Price: $"+prices.get(position));
        holder.date_time.setText(String.valueOf(dates_times.get(position)));
        int passenger_id = (int) passenger_ids.get(position);
        int ride_id = (int) ride_ids.get(position);
        holder.viewRideButton.setOnClickListener(v -> {
            if(activity.equals("UnratedRidesActivity")) {
                Intent intent = new Intent(context, RateRideActivity.class);
                intent.putExtra("rideId", ride_id);
                intent.putExtra("rated_user_id", passenger_id);
                context.startActivity(intent);
            }
            if(activity.equals("ViewRideActivity")) {
                Intent intent = new Intent(context, RideOnMapActivity.class);
                intent.putExtra("start_location", start_locations.get(position).toString());
                intent.putExtra("end_location", end_locations.get(position).toString());
                context.startActivity(intent);
            }

        });
    }

    @Override
    public int getItemCount() {
        return ride_ids.size();
    }

    public class RideViewHolder extends RecyclerView.ViewHolder {
        TextView end_location, price, date_time;
        Button viewRideButton;
        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            end_location = itemView.findViewById(R.id.tv_end_location);
            date_time = itemView.findViewById(R.id.date_time);
            price = itemView.findViewById(R.id.tv_price);
            viewRideButton = itemView.findViewById(R.id.viewRideButton);
            if(activity.equals("UnratedRidesActivity")) {
                viewRideButton.setText("Rate this ride");
            }
        }
    }
}
