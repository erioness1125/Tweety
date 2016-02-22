package com.codepath.apps.mytinytwitter.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.mytinytwitter.R;
import com.codepath.apps.mytinytwitter.activities.TimelineActivity;
import com.codepath.apps.mytinytwitter.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    private List<Tweet> tweetList;

    private static long currentTime;

    private static final String UNKNOWN = "UNKNOWN";

    private Context context;

    // Define listener member variable
    private static OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TimelineAdapter(List<Tweet> tweetList) {
        this.tweetList = tweetList;
        currentTime = System.currentTimeMillis();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_tweet, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        Tweet tweet = tweetList.get(position);

        // Set item views based on the data model
        final ImageView ivProfileImg = holder.ivProfileImg;
        Glide.with(context)
                .load(tweet.getUser().getProfileImageUrl())
                .asBitmap()
                .fitCenter()
                .into(new BitmapImageViewTarget(ivProfileImg) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(5.0f);
                        ivProfileImg.setImageDrawable(circularBitmapDrawable);
                    }
                });

        // name of the user
        TextView tvName = holder.tvName;
        tvName.setText(tweet.getUser().getName());

        // screen name, handle, or alias that this user identifies themselves with
        TextView tvScreenName = holder.tvScreenName;
        tvScreenName.setText("@" + tweet.getUser().getScreenName());

        // UTC time when this Tweet was created
        TextView tvCreatedAt = holder.tvCreatedAt;
        tvCreatedAt.setText(getRelativeTimeAgo(tweet.getCreatedAt()));

        // actual UTF-8 text of the status update
        TextView tvText = holder.tvText;
        tvText.setText(tweet.getText());
    }

    @Override
    public int getItemCount() {
        return tweetList.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweetList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Tweet> tweetList) {
        this.tweetList.addAll(tweetList);
        notifyDataSetChanged();
    }

    public void setTweetList(List<Tweet> tweetList) {
        this.tweetList = tweetList;
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

    public static int containerHeight(TimelineActivity timelineActivity) {
        DisplayMetrics dm = new DisplayMetrics();
        timelineActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        return (int) (dm.heightPixels / 5.0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.ivProfileImg) ImageView ivProfileImg;

        @Bind(R.id.tvName) TextView tvName;
        @Bind(R.id.tvScreenName) TextView tvScreenName;
        @Bind(R.id.tvCreatedAt) TextView tvCreatedAt;
        @Bind(R.id.tvText) TextView tvText;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // Setup the click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }
}
