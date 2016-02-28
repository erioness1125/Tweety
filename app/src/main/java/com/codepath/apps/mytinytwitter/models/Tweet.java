package com.codepath.apps.mytinytwitter.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.parceler.Parcel;

@Table(name = "Tweets")
@Parcel(analyze = { Tweet.class })
public class Tweet extends Model {

    Entities entities;
    ExtendedEntities extendedEntities;

    int favoriteCount; // likes
    int retweetCount;

    @Column(name = "CreatedAt") String createdAt;
    @Column(name = "IdStr", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    String idStr; // This is the unique id given by the server
    @Column(name = "Text") String text;

    @Column(name = "User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    User user; // This is an association to another activeandroid model

    // empty constructor needed by the Parceler library
    public Tweet() {

    }

    public Tweet(Entities entities, ExtendedEntities extendedEntities,
                 int favoriteCount, int retweetCount,
                 String createdAt, String idStr, String text, User user) {
        this.entities = entities;
        this.extendedEntities = extendedEntities;
        this.favoriteCount = favoriteCount;
        this.retweetCount = retweetCount;
        this.createdAt = createdAt;
        this.idStr = idStr;
        this.text = text;
        this.user = user;
    }

    public ExtendedEntities getExtendedEntities() {
        return extendedEntities;
    }

    public void setExtendedEntities(ExtendedEntities extendedEntities) {
        this.extendedEntities = extendedEntities;
    }

    public Entities getEntities() {
        return entities;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public int getRetweetCount() {
        return retweetCount;
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
