package com.example.carpoolingapplication;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IntervalAdapter extends RecyclerView.Adapter<IntervalAdapter.IntervalViewHolder> {

    private List<String> start_intervals, end_intervals;
    private Context context;
    private Database db;
    private SharedPreferences sharedPreferences;
    int driverId;

    public interface OnIntervalDeleteListener {
        void onIntervalDelete(int position);
    }

    public IntervalAdapter(Context context, List<String> start_intervals, List<String> end_intervals) {
        this.start_intervals = start_intervals;
        this.end_intervals = end_intervals;
        this.context = context;
        this.db = new Database(context, "carpool", null, 1);
        this.sharedPreferences = context.getSharedPreferences("shared_prefs", MODE_PRIVATE);
        this.driverId = sharedPreferences.getInt("user_id", -1);
    }

    @NonNull
    @Override
    public IntervalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.delete_interval, parent, false);
        return new IntervalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IntervalViewHolder holder, int position) {
        String start_interval = start_intervals.get(position);
        String end_interval = end_intervals.get(position);
        holder.tvInterval.setText(start_interval+" - "+end_interval);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteDriverInterval(driverId, start_interval, end_interval);
                start_intervals.remove(position);
                end_intervals.remove(position);

                notifyItemRemoved(position);
                notifyItemRangeChanged(position, start_intervals.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return start_intervals.size();
    }

    public static class IntervalViewHolder extends RecyclerView.ViewHolder {
        TextView tvInterval;
        ImageButton btnDelete;

        public IntervalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInterval = itemView.findViewById(R.id.tvInterval);
            btnDelete = itemView.findViewById(R.id.btnDeleteInterval);
        }
    }
}

