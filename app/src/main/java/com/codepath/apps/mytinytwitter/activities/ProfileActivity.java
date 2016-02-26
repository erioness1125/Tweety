package com.codepath.apps.mytinytwitter.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.mytinytwitter.R;
import com.codepath.apps.mytinytwitter.TwitterApplication;
import com.codepath.apps.mytinytwitter.TwitterClient;
import com.codepath.apps.mytinytwitter.fragments.UserTimelineFragment;
import com.codepath.apps.mytinytwitter.models.User;
import com.codepath.apps.mytinytwitter.utils.MyGson;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    @Bind(R.id.ivProfilePic) ImageView ivProfilePic;
    @Bind(R.id.tvProfileName) TextView tvProfileName;
    @Bind(R.id.tvProfileScreenName) TextView tvProfileScreenName;
    @Bind(R.id.tvProfileDescription) TextView tvProfileDescription;
    @Bind(R.id.tvProfileFollowing) TextView tvProfileFollowing;
    @Bind(R.id.tvProfileFollowers) TextView tvProfileFollowers;

    private TwitterClient twitterClient;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        twitterClient = TwitterApplication.getRestClient();
        twitterClient.getUserAccount(new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(), "Failed to load my info", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String responseString = response.toString();
                Gson gson = MyGson.getMyGson();
                user = gson.fromJson(responseString, User.class);
                getSupportActionBar().setTitle("@" + user.getScreenName());
                populateUserProfileHeader(user);
            }
        });

        // get userId
        String userId = getIntent().getStringExtra(getString(R.string.userid));

        if (savedInstanceState == null) {
            // create UserTimelineFragment
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(userId);

            // display userTimelineFragment within this activity (dynamically)
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();
        }
    }

    private void populateUserProfileHeader(User user) {
        // Set item views based on the data model
        Glide.with(this)
                .load(user.getProfileImageUrl())
                .asBitmap()
                .fitCenter()
                .into(new BitmapImageViewTarget(ivProfilePic) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(5.0f);
                        ivProfilePic.setImageDrawable(circularBitmapDrawable);
                    }
                });

        tvProfileName.setText(user.getName());
        tvProfileScreenName.setText(user.getScreenName());
        tvProfileDescription.setText(user.getDescription());
        tvProfileFollowers.setText(user.getFollowersCount() + " " + getString(R.string.followers));
        tvProfileFollowing.setText(user.getFollowersCount() + " " + getString(R.string.following));
    }
}
