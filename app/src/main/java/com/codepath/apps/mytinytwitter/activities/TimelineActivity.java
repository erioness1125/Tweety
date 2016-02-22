package com.codepath.apps.mytinytwitter.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.mytinytwitter.R;
import com.codepath.apps.mytinytwitter.TwitterApplication;
import com.codepath.apps.mytinytwitter.TwitterClient;
import com.codepath.apps.mytinytwitter.adapters.TimelineAdapter;
import com.codepath.apps.mytinytwitter.fragments.ComposeDialogFragment;
import com.codepath.apps.mytinytwitter.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mytinytwitter.models.Tweet;
import com.codepath.apps.mytinytwitter.utils.DividerItemDecoration;
import com.codepath.apps.mytinytwitter.utils.MyGson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity {

    @Bind(R.id.rvTweets) RecyclerView rvTweets;
    @Bind(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    private List<Tweet> tweetList;
    private TimelineAdapter adapter;
    private TwitterClient twitterClient;

    private int tweetsCount = 20;
    private String nextMaxId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showComposeDialog();
            }
        });

        ButterKnife.bind(this);

        /********************** SwipeRefreshLayout **********************/
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        /********************** end of SwipeRefreshLayout **********************/

        /********************** RecyclerView **********************/
        tweetList = new ArrayList<>();
        adapter = new TimelineAdapter(tweetList);
        // Attach the adapter to the RecyclerView to populate items
        rvTweets.setAdapter(adapter);
        // Set layout manager to position the items
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        // Add the scroll listener
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Tweet lastTweet = tweetList.get(tweetList.size() - 1);
                nextMaxId = lastTweet.getIdStr();
                populateTimeline(tweetsCount, nextMaxId);
            }
        });
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        rvTweets.addItemDecoration(itemDecoration);
        /********************** end of RecyclerView **********************/

        twitterClient = TwitterApplication.getRestClient(); // singleton client
        // first request
        // from dev.twitter.com:
        // To use max_id correctly, an application’s first request to a timeline endpoint should only specify a count.
        populateTimeline(tweetsCount, null);
    }

    private void fetchTimelineAsync() {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                populateTimeline(tweetsCount, null);

                swipeContainer.setRefreshing(false);
            }
        }, 1000);
    }

    // 1. send an API request to get the timeline json
    // 2. fill the RecyclerView by creating the tweet objects from the json
    private void populateTimeline(int count, String maxId) {
        if (maxId == null || maxId.trim().isEmpty()) {
            adapter.clear();
        }

        twitterClient.getHomeTimeline(count, maxId, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                String responseString = response.toString();
                Type collectionType = new TypeToken<List<Tweet>>(){ }.getType();
                Gson gson = MyGson.getMyGson();
                tweetList = gson.fromJson(responseString, collectionType);

                adapter.addAll(tweetList);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showComposeDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeDialogFragment editNameDialog = ComposeDialogFragment.newInstance("Some Title");
        editNameDialog.show(fm, "fragment_compose");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timeline, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }
}