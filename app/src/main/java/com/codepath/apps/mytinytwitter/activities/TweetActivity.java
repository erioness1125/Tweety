package com.codepath.apps.mytinytwitter.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.mytinytwitter.R;
import com.codepath.apps.mytinytwitter.TwitterApplication;
import com.codepath.apps.mytinytwitter.TwitterClient;
import com.codepath.apps.mytinytwitter.models.Tweet;
import com.codepath.apps.mytinytwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TweetActivity extends AppCompatActivity {

    @Bind(R.id.btnReply) Button btnReply;

    @Bind(R.id.etTweetReply) EditText etTweetReply;

    @Bind(R.id.ivTweetMyProfileImg) ImageView ivTweetMyProfileImg;
    @Bind(R.id.ivTweetProfileImg) ImageView ivTweetProfileImg;
    @Bind(R.id.ivTweetReply) ImageView ivTweetReply;

    @Bind(R.id.rlReply) RelativeLayout rlReply;

    @Bind(R.id.tvTweetName) TextView tvTweetName;
    @Bind(R.id.tvTweetScreenName) TextView tvTweetScreenName;
    @Bind(R.id.tvTweetCreatedAt) TextView tvTweetCreatedAt;
    @Bind(R.id.tvTweetText) TextView tvTweetText;

    private boolean isRLReplyOn = false;

    private Tweet tweet;

    private TwitterClient twitterClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        ButterKnife.bind(this);

        twitterClient = TwitterApplication.getRestClient();

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        // Set item views based on the data model
        Glide.with(this)
                .load(tweet.getUser().getProfileImageUrl())
                .asBitmap()
                .fitCenter()
                .into(new BitmapImageViewTarget(ivTweetProfileImg) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(5.0f);
                        ivTweetProfileImg.setImageDrawable(circularBitmapDrawable);
                    }
                });

        // name of the user
        tvTweetName.setText(tweet.getUser().getName());

        // screen name, handle, or alias that this user identifies themselves with
        tvTweetScreenName.setText("@" + tweet.getUser().getScreenName());

        // UTC time when this Tweet was created
        tvTweetCreatedAt.setText(getRelativeTimeAgo(tweet.getCreatedAt()));

        // actual UTF-8 text of the status update
        tvTweetText.setText(tweet.getText());

        User me = (User) Parcels.unwrap(getIntent().getParcelableExtra("me"));
        Glide.with(this)
                .load(me.getProfileImageUrl())
                .asBitmap()
                .fitCenter()
                .into(new BitmapImageViewTarget(ivTweetMyProfileImg) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(5.0f);
                        ivTweetMyProfileImg.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    private String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    @OnClick(R.id.ivTweetReply)
    void showReplyPanel() {
        if (isRLReplyOn) {
            isRLReplyOn = false;
            rlReply.setVisibility(View.INVISIBLE);
            ivTweetReply.setImageAlpha(255);
        }
        else {
            isRLReplyOn = true;
            rlReply.setVisibility(View.VISIBLE);
            ivTweetReply.setImageAlpha(100);
        }
    }

    @OnClick(R.id.btnReply)
    void replyToTweet() {
        String reply = etTweetReply.getText().toString();


        twitterClient.postStatus(reply, tweet.getIdStr(), new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                String responseString = response.toString();
//                Gson gson = MyGson.getMyGson();
//                Tweet newTweet = gson.fromJson(responseString, Tweet.class);

                Toast.makeText(getApplicationContext(), "Reply has been successfully posted!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
