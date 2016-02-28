package com.codepath.apps.mytinytwitter.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.parceler.Parcel;

@Table(name = "Users")
@Parcel(analyze = { User.class })
public class User extends Model {

    @Column(name = "isMe") public int isMe; // for getting my "User" (select * from Users where isMe==1)

    @Column(name = "FollowersCount") int followersCount;
    @Column(name = "FriendsCount") int friendsCount;
    @Column(name = "Description") String description;
    @Column(name = "IdStr", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    String idStr; // This is the unique id given by the server
    @Column(name = "Name") String name;
    @Column(name = "ProfileImageUrl") String profileImageUrl;
    @Column(name = "ScreenName") String screenName;

    public User() {

    }

    public User(int followersCount, int friendsCount,
                String description, String idStr, String name, String profileImageUrl, String screenName) {
        this.followersCount = followersCount;
        this.friendsCount = friendsCount;
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
