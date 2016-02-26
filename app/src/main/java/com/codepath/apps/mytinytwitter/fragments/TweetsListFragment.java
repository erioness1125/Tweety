package com.codepath.apps.mytinytwitter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.mytinytwitter.R;
import com.codepath.apps.mytinytwitter.TwitterApplication;
import com.codepath.apps.mytinytwitter.TwitterClient;
import com.codepath.apps.mytinytwitter.adapters.TimelineAdapter;
import com.codepath.apps.mytinytwitter.models.Tweet;
import com.codepath.apps.mytinytwitter.models.User;
import com.codepath.apps.mytinytwitter.utils.DividerItemDecoration;
import com.codepath.apps.mytinytwitter.utils.MyGson;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TweetsListFragment extends Fragment {

    @Bind(R.id.rvTweets) RecyclerView rvTweets;
    @Bind(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    protected Context context;
    protected List<Tweet> tweetList;
    protected TimelineAdapter adapter;
    protected TwitterClient twitterClient;

    protected int tweetsCount = 20;
    protected String nextMaxId;
    protected final User[] me = new User[1];

    // inflation logic
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        ButterKnife.bind(this, view);
        context = getContext();

        /********************** RecyclerView **********************/
        tweetList = new ArrayList<>();
        adapter = new TimelineAdapter(tweetList);
        // Attach the adapter to the RecyclerView to populate items
        rvTweets.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST);
        rvTweets.addItemDecoration(itemDecoration);
        /********************** end of RecyclerView **********************/

        /********************** SwipeRefreshLayout **********************/
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        /********************** end of SwipeRefreshLayout **********************/

        return view;
    }

    // creation lifecycle event
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweetList = new ArrayList<>();
        adapter = new TimelineAdapter(tweetList);

        twitterClient = TwitterApplication.getRestClient(); // singleton client

        // get my user info
        getMyUserInfo();
    }

    protected void addAllToAdapter(List<Tweet> tweetList) {
        adapter.addAll(tweetList);
    }

    protected void clearAdapter() {
        adapter.clear();
    }

    protected void getMyUserInfo() {
        twitterClient.getUserAccount(new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, "Failed to load my info", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String responseString = response.toString();
                Gson gson = MyGson.getMyGson();
                me[0] = gson.fromJson(responseString, User.class);
            }
        });
    }
}