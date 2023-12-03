package edu.northeastern.finalproject.communityFragment;

public class Post {
    private String userName;
    private String content;

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
}
