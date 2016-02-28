package com.codepath.apps.mytinytwitter.models;

import org.parceler.Parcel;

@Parcel(analyze = { Medium.class })
public class Medium {

    String displayUrl;
    String expandedUrl;
    String idStr;
    String mediaUrl;
    String url;
    String type;

    public Medium() {

    }

    public Medium(String displayUrl, String expandedUrl, String idStr, String mediaUrl, String url, String type) {
        this.displayUrl = displayUrl;
        this.expandedUrl = expandedUrl;
        this.idStr = idStr;
        this.mediaUrl = mediaUrl;
        this.url = url;
        this.type = type;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public String getExpandedUrl() {
        return expandedUrl;
    }

    public String getIdStr() {
        return idStr;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }
}
