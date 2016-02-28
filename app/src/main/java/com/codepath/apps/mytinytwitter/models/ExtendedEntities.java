package com.codepath.apps.mytinytwitter.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel(analyze = { ExtendedEntities.class })
public class ExtendedEntities {

    private List<Medium> media = new ArrayList<>();

    public ExtendedEntities() {

    }

    public ExtendedEntities(List<Medium> media) {
        this.media = media;
    }

    public List<Medium> getMedia() {
        return media;
    }
}
