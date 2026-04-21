package com.example.fitzone;

public class PostItem {

    private final long id;
    private final String userName;
    private final String text;
    private final String imagePath;
    private final String date;
    private final String avatarUri;
    private final int likeCount;
    private final int commentCount;
    private final boolean likedByCurrentUser;

    public PostItem(long id,
                    String userName,
                    String text,
                    String imagePath,
                    String date,
                    String avatarUri,
                    int likeCount,
                    int commentCount,
                    boolean likedByCurrentUser) {
        this.id = id;
        this.userName = userName;
        this.text = text;
        this.imagePath = imagePath;
        this.date = date;
        this.avatarUri = avatarUri;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getText() {
        return text;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDate() {
        return date;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }
}
