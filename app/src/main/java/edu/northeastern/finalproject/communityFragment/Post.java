package edu.northeastern.finalproject.communityFragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Post {
    private String userName;
    private String content;

    public Post() {
    }

    public Post(String userName, String content) {
        this.userName = userName;
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }

    @Exclude
    public static Post fromDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        Post post = new Post();
        post.userName = documentSnapshot.getString("userName");
        post.content = documentSnapshot.getString("content");
        return post;
    }
}
