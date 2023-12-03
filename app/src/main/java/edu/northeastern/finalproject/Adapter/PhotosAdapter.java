package edu.northeastern.finalproject.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.northeastern.finalproject.R;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> {

    private List<String> photoUrls;

    public PhotosAdapter(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item_layout, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        String photoUrl = photoUrls.get(position);
        holder.bind(photoUrl);
    }

    @Override
    public int getItemCount() {
        return photoUrls.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        private ImageView photoImageView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
        }

        public void bind(String photoUrl) {
            if (photoUrl != null && photoImageView != null) {
                Picasso.get()
                        .load(photoUrl)
                        .into(photoImageView);

            }
        }
    }
}
