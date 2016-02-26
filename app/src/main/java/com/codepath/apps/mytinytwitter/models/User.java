package com.codepath.apps.mytinytwitter.models;

import org.parceler.Parcel;

@Parcel
public class User {

    int followersCount, friendsCount;
    String description, idStr, name, profileImageUrl, screenName;

    public User() {

    }

    public User(int followersCount, int friendsCount,
                String description, String idStr, String name, String profileImageUrl, String screenName) {
        this.description = description;
        this.idStr = idStr;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.screenName = screenName;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public String getDescription() {
        return description;
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
