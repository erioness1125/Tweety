package com.codepath.apps.mytinytwitter.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;

import com.activeandroid.query.Select;
import com.codepath.apps.mytinytwitter.TwitterApplication;
import com.codepath.apps.mytinytwitter.TwitterClient;
import com.codepath.apps.mytinytwitter.models.User;
import com.codepath.apps.mytinytwitter.utils.MyGson;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.IOException;

public class MyBaseFragment extends Fragment {

    protected final User[] me = new User[1];

    protected TwitterClient twitterClient;
    protected View view;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view = getView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        twitterClient = TwitterApplication.getRestClient(); // singleton client
    }

    protected void getMyUserInfo() {
        me[0] = new Select()
                .from(User.class)
                .where("isMe = ?", 1)
                .executeSingle();

        if (me[0] == null) {
            twitterClient.getUserAccount(new JsonHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (statusCode == 429) {
                        // 429 Too Many Requests
                        Snackbar.make(view, "Sent too many requests exceeding Twitter Rate limit! Try again later",
                                Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(view, "Problem with loading timeline... Try again later",
                                Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    String responseString = response.toString();
                    Gson gson = MyGson.getMyGson();
                    me[0] = gson.fromJson(responseString, User.class);

                    // persist my user info
                    me[0].isMe = 1;
                    me[0].save();
                }
            });
        }
    }

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    protected boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
