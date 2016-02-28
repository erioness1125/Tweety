package com.codepath.apps.mytinytwitter.models;

import org.parceler.Parcel;

@Parcel(analyze = { UserMention.class })
public class UserMention {

    String idStr;
    String name;
    String screenName;

    public UserMention() {

    }

    public UserMention(String idStr, String name, String screenName) {
        this.idStr = idStr;
        this.name = name;
        this.screenName = screenName;
    }

    public String getIdStr() {
        return idStr;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }
}
