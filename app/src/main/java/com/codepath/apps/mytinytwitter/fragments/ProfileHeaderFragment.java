package com.codepath.apps.mytinytwitter.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.mytinytwitter.R;
import com.codepath.apps.mytinytwitter.TwitterApplication;
import com.codepath.apps.mytinytwitter.TwitterClient;
import com.codepath.apps.mytinytwitter.models.User;
import com.codepath.apps.mytinytwitter.utils.MyGson;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileHeaderFragment extends Fragment {

    @Bind(R.id.ivProfHdPic) ImageView ivProfHdPic;

    @Bind(R.id.tvProfHdName) TextView tvProfHdName;
    @Bind(R.id.tvProfHdScreenName) TextView tvProfHdScreenName;
    @Bind(R.id.tvProfHdDesc) TextView tvProfHdDesc;
    @Bind(R.id.tvProfHdFollowers) TextView tvProfHdFollowers;
    @Bind(R.id.tvProfHdFollowing) TextView tvProfHdFollowing;

    public static ProfileHeaderFragment newInstance(String userId) {
        ProfileHeaderFragment fragment = new ProfileHeaderFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    // inflation logic
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_header, container, false);
        ButterKnife.bind(this, view);
        getUserProfile();
        return view;
    }

    private void getUserProfile() {
        TwitterClient twitterClient = TwitterApplication.getRestClient();

        String userId = getArguments().getString("userId");
        if (userId == null || userId.isEmpty()) {
            // my account
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
                    User user = gson.fromJson(responseString, User.class);
                    populateUserProfileHeader(user);
                }
            });
        }
        else {
            twitterClient.getUser(userId, new JsonHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (statusCode == 429) {
                        // 429 Too Many Requests
                        Snackbar.make(getView(),
                                "Sent too many requests exceeding Twitter Rate limit! Try again later",
                                Snackbar.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(getContext(), "Failed to load the user info", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    String responseString = response.toString();
                    Gson gson = MyGson.getMyGson();
                    User user = gson.fromJson(responseString, User.class);
                    populateUserProfileHeader(user);
                }
            });
        }
    }

    private void populateUserProfileHeader(User user) {
        // set actionbar title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("@" + user.getScreenName());

        // Set item views based on the data model
        Glide.with(getContext())
                .load(user.getProfileImageUrl())
                .asBitmap()
                .fitCenter()
                .into(new BitmapImageViewTarget(ivProfHdPic) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(5.0f);
                        ivProfHdPic.setImageDrawable(circularBitmapDrawable);
                    }
                });

        tvProfHdName.setText(user.getName());
        tvProfHdScreenName.setText(user.getScreenName());
        tvProfHdDesc.setText(user.getDescription());
        tvProfHdFollowers.setText(user.getFollowersCount() + " " + getString(R.string.followers));
        tvProfHdFollowing.setText(user.getFollowersCount() + " " + getString(R.string.following));
    }
}