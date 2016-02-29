package com.codepath.apps.mytinytwitter.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.mytinytwitter.R;
import com.codepath.apps.mytinytwitter.models.Tweet;
import com.codepath.apps.mytinytwitter.utils.RequestCodes;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TweetActivity extends AppCompatActivity {

    @Bind(R.id.ivTweetProfileImg) ImageView ivTweetProfileImg;
    @Bind(R.id.ivTweetReply) ImageView ivTweetReply;

    @Bind(R.id.tvTweetName) TextView tvTweetName;
    @Bind(R.id.tvTweetScreenName) TextView tvTweetScreenName;
    @Bind(R.id.tvTweetCreatedAt) TextView tvTweetCreatedAt;
    @Bind(R.id.tvTweetText) TextView tvTweetText;
    @Bind(R.id.tvTweetRts) TextView tvTweetRts;
    @Bind(R.id.tvTweetLikes) TextView tvTweetLikes;

    private boolean isRLReplyOn = false;

    private Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        ButterKnife.bind(this);

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

        String rtCount = tweet.getRetweetCount() + " " + getString(R.string.retweets);
        SpannableString spannableString = new SpannableString(rtCount);
        spannableString.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.twitter_black)),
                0,
                String.valueOf(tweet.getRetweetCount()).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTweetRts.setText(spannableString);

        String likesCount = tweet.getFavoriteCount() + " " + getString(R.string.likes);
        spannableString = new SpannableString(likesCount);
        spannableString.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.twitter_black)),
                0,
                String.valueOf(tweet.getFavoriteCount()).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTweetLikes.setText(spannableString);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Tweet repliedTweet = Parcels.unwrap(data.getParcelableExtra("repliedTweet"));
            Toast.makeText(this, "You have successfully replied!", Toast.LENGTH_SHORT).show();
        }
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
            ivTweetReply.setImageAlpha(255);
        }
        else {
            isRLReplyOn = true;
            ivTweetReply.setImageAlpha(100);
        }
    }

    @OnClick(R.id.ivTweetReply)
    void replyToTweet() {
        // create an intent to display the article
        Intent i = new Intent(this, ReplyActivity.class);
        // pass objects to the target activity
        i.putExtra("inReplyToStatusId", tweet.getIdStr());
        i.putExtra("toName", tweet.getUser().getName());
        i.putExtra("toScreenName", tweet.getUser().getScreenName());
        i.putExtra("myProfileImgUrl", getIntent().getStringExtra("myProfileImgUrl"));
        // launch the activity
        startActivityForResult(i, RequestCodes.REQUEST_CODE_REPLY);
    }
}
