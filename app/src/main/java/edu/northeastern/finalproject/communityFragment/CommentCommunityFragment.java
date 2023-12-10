package edu.northeastern.finalproject.communityFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import edu.northeastern.finalproject.R;

public class CommentCommunityFragment extends DialogFragment {
    private FirebaseFirestore db;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_comment, container, false);

        db = FirebaseFirestore.getInstance();

        Bundle args = getArguments();

        if (args != null) {
            String postId = args.getString("postId");

            EditText etComment = view.findViewById(R.id.editTextComment);

            // TODO “完成”按钮的点击事件
            Button btnFinishComment = view.findViewById(R.id.btnSubmitComment);
            btnFinishComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String commentContent = etComment.getText().toString();
                    updatePostCommentsInFirestore(postId, commentContent);
                    dismiss();
                }
            });
        }

        return view;
    }

//    private void addCommentToPost(int position, String commentContent) {
//        Post post = postList.get(position);
//
//        Comment comment = new Comment("", commentContent);
//
//        post.addComment(comment);
//
//        updatePostCommentsInFirestore(post);
//    }

    private void updatePostCommentsInFirestore(String postId, String commentContent) {
        Map<String, Object> commentData = new HashMap<>();
        // TODO modify current username
        commentData.put("commenterName", "CURRENT USERNAME");
        commentData.put("commentContent", commentContent);

        db.collection("posts")
                .document(postId)
                .collection("comments")
                .add(commentData)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Add comment successfully");
                        } else {
                            System.out.println("Can not add comment");
                        }
                    }
                });
    }
}
