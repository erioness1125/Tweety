package com.codepath.apps.mytinytwitter.models;

public class Tweet {

    String createdAt, idStr, text;
    User user;

    public String getCreatedAt() {
        return createdAt;
    }

    public String getIdStr() {
        return idStr;
    }

    public String getText() {
        return text;
    }

    public User getUser() {
        return user;
    }
}
