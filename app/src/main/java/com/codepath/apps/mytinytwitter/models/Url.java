package com.codepath.apps.mytinytwitter.models;

import org.parceler.Parcel;

@Parcel(analyze = { Url.class })
public class Url {

    String displayUrl;
    String expandedUrl;
    String url;

    public Url() {

    }

    public Url(String displayUrl, String expandedUrl, String url) {
        this.displayUrl = displayUrl;
        this.expandedUrl = expandedUrl;
        this.url = url;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public String getExpandedUrl() {
        return expandedUrl;
    }

    public String getUrl() {
        return url;
    }
}
