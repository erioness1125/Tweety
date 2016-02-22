package com.codepath.apps.mytinytwitter.models;

import org.parceler.Parcel;

@Parcel
public class User {

    String idStr, name, profileImageUrl, screenName;

    public User() {

    }

    public User(String idStr, String name, String profileImageUrl, String screenName) {
        this.idStr = idStr;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.screenName = screenName;
    }

    public String getIdStr() {
        return idStr;
    }

    public String getName() {
        return name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getScreenName() {
        return screenName;
    }
}
