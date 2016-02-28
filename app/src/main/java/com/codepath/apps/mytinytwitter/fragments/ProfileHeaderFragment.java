package com.codepath.apps.mytinytwitter.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.mytinytwitter.R;
import com.codepath.apps.mytinytwitter.models.User;
import com.codepath.apps.mytinytwitter.utils.MyGson;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileHeaderFragment extends MyBaseFragment {

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
        String userId = getArguments().getString("userId");
        if (userId == null || userId.isEmpty()) {
            // my account
            if (me[0] == null)
                getMyUserInfo();
            populateUserProfileHeader(me[0]);
        }
        else {
            // try to load persisted user
            User user = new Select()
                    .from(User.class)
                    .where("IdStr = ?", userId)
                    .executeSingle();
            if (user == null && isOnline()) {
                twitterClient.getUser(userId, new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (statusCode == 429) {
                            // 429 Too Many Requests
                            Snackbar.make(view,
                                    "Sent too many requests exceeding Twitter Rate limit! Try again later",
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(view,
                                    "Failed to load the user info... Try again later",
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        String responseString = response.toString();
                        Gson gson = MyGson.getMyGson();
                        User user = gson.fromJson(responseString, User.class);
                        populateUserProfileHeader(user);

                        // persist user
                        user.save();
                    }
                });
            }
            else
                populateUserProfileHeader(user);
        }
    }

    private void populateUserProfileHeader(User user) {
        String screenNameWithAt = getString(R.string.at) + user.getScreenName();

        // set actionbar title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(screenNameWithAt);

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
        tvProfHdScreenName.setText(screenNameWithAt);

        // change color of screenname or hashtags
        String description = user.getDescription();
        SpannableString descSpan = new SpannableString(description);
        List<String> allScreenNameAndHashTags = new ArrayList<>();
        Matcher m = Pattern.compile("\\s?[@#](\\w+|\\W+)").matcher(description);
        while (m.find()) {
            allScreenNameAndHashTags.add(m.group());
        }
        for (String s : allScreenNameAndHashTags) {
            int idx = description.indexOf(s);
            descSpan.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.twitter_blue)),
                    idx,
                    idx + s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tvProfHdDesc.setText(descSpan);

        String followers = user.getFollowersCount() + " " + getString(R.string.followers);
        SpannableString followSpan = new SpannableString(followers);
        followSpan.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.twitter_black)),
                0,
                String.valueOf(user.getFollowersCount()).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvProfHdFollowers.setText(followSpan);

        String following = user.getFriendsCount() + " " + getString(R.string.following);
        followSpan = new SpannableString(following);
        followSpan.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.twitter_black)),
                0,
                String.valueOf(user.getFriendsCount()).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvProfHdFollowing.setText(followSpan);
    }
}