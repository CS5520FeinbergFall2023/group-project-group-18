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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

import edu.northeastern.finalproject.R;

public class CommentCommunityFragment extends DialogFragment {
    private FirebaseFirestore db;
    private Context context;
    private FirebaseAuth mAuth;
    private String currentUserName;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_comment, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            currentUserName = userEmail.split("@")[0];
        }

        Bundle args = getArguments();

        if (args != null) {
            String postId = args.getString("postId");

            EditText etComment = view.findViewById(R.id.editTextComment);

            Button btnFinishComment = view.findViewById(R.id.btnSubmitComment);
            btnFinishComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String commentContent = etComment.getText().toString();
                    updateDocumentCommentsInFirestore(postId, commentContent);
                    dismiss();
                }
            });
        }

        return view;
    }

    private void updateDocumentCommentsInFirestore(String postId, String commentContent) {

        Comment comment = new Comment(currentUserName, commentContent);

        DocumentReference postRef = db.collection("posts").document(postId);

        postRef.update("comments", FieldValue.arrayUnion(comment))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Add comment successfully");
                        } else {
                            System.out.println("Can not add comment");
                        }
                    }
                });
    }
}








