package edu.northeastern.finalproject.RecordFragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.finalproject.R;
import edu.northeastern.finalproject.data.UserDailyRecord;

public class HeartRateHistoryAdapter extends RecyclerView.Adapter<HeartRateHistoryAdapter.ViewHolder> {

    private List<UserDailyRecord> heartRateList;

    public HeartRateHistoryAdapter(List<UserDailyRecord> heartRateList) {
        this.heartRateList = heartRateList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.heart_rate_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserDailyRecord record = heartRateList.get(position);
        holder.dateTextView.setText(DateFormat.format("yyyy-MM-dd", record.getDate())); // Format the date as needed
        holder.heartRateTextView.setText(String.valueOf((int)record.getHeartRate()));
    }

    @Override
    public int getItemCount() {
        return heartRateList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, heartRateTextView;
        ImageView heartImageView;

        public ViewHolder(View view) {
            super(view);
            dateTextView = view.findViewById(R.id.dateTextView);
            heartRateTextView = view.findViewById(R.id.heartRateTextView);
            heartImageView = view.findViewById(R.id.heartImageView);
        }
    }
}

