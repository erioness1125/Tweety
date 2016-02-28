package com.codepath.apps.mytinytwitter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import com.codepath.apps.mytinytwitter.activities.ProfileActivity;
import com.codepath.apps.mytinytwitter.activities.TweetActivity;
import com.codepath.apps.mytinytwitter.adapters.TimelineAdapter;
import com.codepath.apps.mytinytwitter.models.Tweet;
import com.codepath.apps.mytinytwitter.models.User;
import com.codepath.apps.mytinytwitter.utils.DividerItemDecoration;
import com.codepath.apps.mytinytwitter.utils.MyGson;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TweetsListFragment extends Fragment {

    @Bind(R.id.rvTweets) RecyclerView rvTweets;
    @Bind(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    protected List<Tweet> mTweetList;
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

        /********************** RecyclerView **********************/
        mTweetList = new ArrayList<>();
        adapter = new TimelineAdapter(mTweetList);
        adapter.setOnItemClickListener(new TimelineAdapter.OnItemClickListener() {
            @Override
            public void onProfilePicClick(View itemView, int position) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                // get the article to display
                Tweet tweet = mTweetList.get(position);
                // pass objects to the target activity
                i.putExtra(getString(R.string.userid), tweet.getIdStr());
                // launch the activity
                startActivity(i);
            }

            @Override
            public void onTweetContainerClick(View itemView, int position) {
                // create an intent to display the article
                Intent i = new Intent(getContext(), TweetActivity.class);
                // get the article to display
                Tweet tweet = mTweetList.get(position);
                // pass objects to the target activity
                i.putExtra("tweet", Parcels.wrap(tweet));
                i.putExtra("me", Parcels.wrap(me[0]));
                // launch the activity
                startActivity(i);
            }
        });
        // Attach the adapter to the RecyclerView to populate items
        rvTweets.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
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

        twitterClient = TwitterApplication.getRestClient(); // singleton client

        // get my user info
        getMyUserInfo();
    }

    protected void addAllToAdapter(List<Tweet> tweetList) {
        adapter.addAll(tweetList);
        adapter.notifyItemRangeInserted(mTweetList.size()-1, tweetList.size());
    }

    protected void clearAdapter() {
        adapter.clear();
    }

    protected void getMyUserInfo() {
        twitterClient.getUserAccount(new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (statusCode == 429) {
                    // 429 Too Many Requests
                    Snackbar.make(getView(),
                            "Sent too many requests exceeding Twitter Rate limit! Try again later",
                            Snackbar.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getContext(), "Failed to load my info", Toast.LENGTH_SHORT).show();
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