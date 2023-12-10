package edu.northeastern.finalproject.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

import edu.northeastern.finalproject.R;
import edu.northeastern.finalproject.communityFragment.Comment;
import edu.northeastern.finalproject.communityFragment.CommentCommunityFragment;
import edu.northeastern.finalproject.communityFragment.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;
    private Context context;

    public PostAdapter(List<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_community_show, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        int singlePosition = position;

        if (singlePosition < posts.size()) {
            Post post = posts.get(singlePosition);
            holder.tvPostContent1.setText(post.getContent());
            holder.tvUserName1.setText(post.getUserName());

            String postId = post.getPostId();

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommentCommunityFragment fragment = new CommentCommunityFragment();
                    Bundle args = new Bundle();
                    args.putString("postId", postId);
                    fragment.setArguments(args);

                    fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "CommentCommunityFragment");
                }
            });

            List<Comment> comments = post.getComments();
            if (comments != null && !comments.isEmpty()) {
                CommentAdapter commentAdapter = new CommentAdapter(comments);
                holder.recyclerViewComments.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
                holder.recyclerViewComments.setAdapter(commentAdapter);
            }
        }
    }

    private void showCommentDialog(Context context, int position) {
        CommentCommunityFragment dialog = new CommentCommunityFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        dialog.setArguments(bundle);
        dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "CommentCommunityFragment");
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
        private CardView cardView1;
        private CardView cardView2;

        private RecyclerView recyclerViewComments;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView1 = itemView.findViewById(R.id.cardView1);
            tvPostContent1 = itemView.findViewById(R.id.editTextPost1);
            tvUserName1 = itemView.findViewById(R.id.usernameTextView1);
            recyclerViewComments = itemView.findViewById(R.id.recyclerViewComments);
        }
    }

}
