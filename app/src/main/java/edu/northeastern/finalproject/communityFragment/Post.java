package edu.northeastern.finalproject.communityFragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Post {
    private String userName;
    private String content;
    private String postId;
    private List<Comment> comments;

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
    public String getPostId() {
        return postId;
    }
    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void addComment(Comment comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(comment);
    }
    public void setComments(List<Comment> commentList) {
        this.comments = commentList;
    }

    public List<Comment> getComments() {
        return comments;
    }

    @Exclude
    public static Post fromDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        Post post = new Post();
        post.userName = documentSnapshot.getString("userName");
        post.content = documentSnapshot.getString("content");
        post.postId = documentSnapshot.getId();
        return post;
    }
}
