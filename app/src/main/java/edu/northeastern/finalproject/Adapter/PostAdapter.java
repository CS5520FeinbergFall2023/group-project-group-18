package edu.northeastern.finalproject.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.finalproject.R;
import edu.northeastern.finalproject.communityFragment.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;

    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_community_show, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        int leftPosition = position * 2;
        int rightPosition = position * 2 + 1;

        if (leftPosition < posts.size()) {
            Post leftPost = posts.get(leftPosition);
            holder.tvPostContent1.setText(leftPost.getContent());
            holder.tvUserName1.setText(leftPost.getUserName());
        }

        if (rightPosition < posts.size()) {
            Post rightPost = posts.get(rightPosition);
            holder.tvPostContent2.setText(rightPost.getContent());
            holder.tvUserName2.setText(rightPost.getUserName());
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName1;
        private TextView tvPostContent1;
        private TextView tvUserName2;
        private TextView tvPostContent2;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPostContent1 = itemView.findViewById(R.id.editTextPost1);
            tvUserName1 = itemView.findViewById(R.id.usernameTextView1);
            tvPostContent2 = itemView.findViewById(R.id.editTextPost2);
            tvUserName2 = itemView.findViewById(R.id.usernameTextView2);
        }
    }
}
