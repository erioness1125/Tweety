package com.codepath.apps.mytinytwitter.models;

import org.parceler.Parcel;

@Parcel
public class Tweet {

    String createdAt, idStr, text;
    User user;

    // empty constructor needed by the Parceler library
    public Tweet() {

    }

    public Tweet(String createdAt, String idStr, String text, User user) {
        this.createdAt = createdAt;
        this.idStr = idStr;
        this.text = text;
        this.user = user;
    }

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
