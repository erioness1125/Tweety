package com.codepath.apps.mytinytwitter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.codepath.apps.mytinytwitter.R;
import com.codepath.apps.mytinytwitter.activities.ProfileActivity;
import com.codepath.apps.mytinytwitter.activities.ReplyActivity;
import com.codepath.apps.mytinytwitter.activities.TweetActivity;
import com.codepath.apps.mytinytwitter.adapters.TimelineAdapter;
import com.codepath.apps.mytinytwitter.models.Tweet;
import com.codepath.apps.mytinytwitter.utils.DividerItemDecoration;
import com.codepath.apps.mytinytwitter.utils.RequestCodes;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TweetsListFragment extends MyBaseFragment {

    @Bind(R.id.llError) LinearLayout llError;
    @Bind(R.id.rvTweets) RecyclerView rvTweets;
    @Bind(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    protected List<Tweet> mTweetList;
    protected TimelineAdapter adapter;

    protected int tweetsCount = 20;
    protected String nextMaxId;

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
                i.putExtra(getString(R.string.userid), tweet.getUser().getIdStr());
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
                i.putExtra("user", Parcels.wrap(tweet.getUser()));
                // launch the activity
                startActivity(i);
            }

            @Override
            public void onReplyClick(int position) {
                // create an intent to display the article
                Intent i = new Intent(getActivity(), ReplyActivity.class);
                // get the article to display
                Tweet tweet = mTweetList.get(position);
                // pass objects to the target activity
                i.putExtra("inReplyToStatusId", tweet.getIdStr());
                i.putExtra("toName", tweet.getUser().getName());
                i.putExtra("toScreenName", tweet.getUser().getScreenName());
                i.putExtra("myProfileImgUrl", me[0].getProfileImageUrl());
                // launch the activity
                startActivityForResult(i, RequestCodes.REQUEST_CODE_REPLY);
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

        // get my user info
        getMyUserInfo();
    }

    public void add(int idx, Tweet tweet) {
        adapter.add(idx, tweet);
    }

    public void addAllToAdapter(List<Tweet> tweetList) {
        adapter.addAll(tweetList);
    }

    public void clearAdapter() {
        adapter.clear();
    }

    public RecyclerView getRvTweets() {
        return rvTweets;
    }
}