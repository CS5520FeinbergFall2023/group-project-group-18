package edu.northeastern.finalproject.communityFragment;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Comment {
    private String commenterName;
    private String commentContent;

    public Comment() {
    }

    public Comment(String commenterName, String commentContent) {
        this.commenterName = commenterName;
        this.commentContent = commentContent;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public String getCommentContent() {
        return commentContent;
    }
}
