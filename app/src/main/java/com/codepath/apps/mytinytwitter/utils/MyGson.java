package com.codepath.apps.mytinytwitter.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MyGson {

    public static Gson getMyGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        // mapping camel case field names
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        Gson gson = gsonBuilder.create();
        return gson;
    }
}
