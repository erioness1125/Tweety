package com.codepath.apps.mytinytwitter.models;

import org.parceler.Parcel;

import java.util.List;

@Parcel(analyze = {Entities.class})
public class Entities {

    List<Medium> media;
    List<Url> urls;
    List<UserMention> userMentions;

    // empty constructor needed by the Parceler library
    public Entities() {

    }

    public Entities(List<Medium> media, List<Url> urls, List<UserMention> userMentions) {
        this.media = media;
        this.urls = urls;
        this.userMentions = userMentions;
    }

    public List<Medium> getMedia() {
        return media;
    }

    public List<Url> getUrls() {
        return urls;
    }

    public List<UserMention> getUserMentions() {
        return userMentions;
    }
}
