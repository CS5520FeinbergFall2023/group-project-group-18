package edu.northeastern.finalproject.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.northeastern.finalproject.R;
import edu.northeastern.finalproject.data.UserDailyRecord;

public class DatesAdapter extends RecyclerView.Adapter<DatesAdapter.DateViewHolder> {

    private List<UserDailyRecord> records;

    public DatesAdapter(List<UserDailyRecord> records) {
        this.records =  new ArrayList<>();
    }

    @Override
    public DateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date_photos, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DateViewHolder holder, int position) {
        UserDailyRecord record = records.get(position);
        holder.bind(record);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }
    public void setRecords(List<UserDailyRecord> records) {
        this.records = records != null ? records : new ArrayList<>();
    }
    public static class DateViewHolder extends RecyclerView.ViewHolder {

        private TextView dateTextView;
        private RecyclerView photosRecyclerView;

        public DateViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            photosRecyclerView = itemView.findViewById(R.id.photosRecyclerView);
            photosRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }

        public void bind(UserDailyRecord record) {
            // Format the date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(record.getDate());

            // Set the formatted date to the TextView
            dateTextView.setText(formattedDate);

            // Set up the PhotosAdapter for the RecyclerView
            PhotosAdapter photosAdapter = new PhotosAdapter(record.getPhotoUrls());
            photosRecyclerView.setAdapter(photosAdapter);
        }


    }
}
