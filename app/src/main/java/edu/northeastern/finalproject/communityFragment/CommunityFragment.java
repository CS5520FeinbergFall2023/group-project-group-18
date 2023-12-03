package edu.northeastern.finalproject.communityFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.finalproject.R;


public class CommunityFragment extends Fragment {
    private List<Post> postList;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        postList = new ArrayList<>();
//        recyclerView = recyclerView.findViewById();
//        postAdapter = new PostAdapter(postList);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(postAdapter);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        Button btnOpenDialog = view.findViewById(R.id.ic_community_button);
        btnOpenDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display the custom dialog
                showPostDialog();
            }
        });

        return view;
    }

    private void showPostDialog() {
        PostCommunityFragment dialog = new PostCommunityFragment();
        dialog.show(getChildFragmentManager(), "PostDialogFragment");
    }

}