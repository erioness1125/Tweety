package com.codepath.apps.mytinytwitter.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.mytinytwitter.R;
import com.codepath.apps.mytinytwitter.models.Medium;
import com.codepath.apps.mytinytwitter.models.Tweet;
import com.codepath.apps.mytinytwitter.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TEXT = 1;
    private final int TEXT_IMAGE = 2;

    private Context context;
    private List<Tweet> tweetList;
    private User me;

    // Define listener member variable
    private OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onProfilePicClick(View itemView, int position);
        void onTweetContainerClick(View itemView, int position);
        void onReplyClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TimelineAdapter(List<Tweet> tweetList) {
        this.tweetList = tweetList;

        me = new Select()
                .from(User.class)
                .where("isMe = ?", 1)
                .executeSingle();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        RecyclerView.ViewHolder viewHolder = null;
        View view = null;

        switch (viewType) {
            case TEXT:
                // Inflate the custom layout
                view = inflater.inflate(R.layout.item_tweet, parent, false);

                // Return a new holder instance
                viewHolder = new TextViewHolder(view);
                break;
            case TEXT_IMAGE:
                // Inflate the custom layout
                view = inflater.inflate(R.layout.item_tweet_photo, parent, false);

                // Return a new holder instance
                viewHolder = new PhotoViewHolder(view);
                break;
            default:
                // Inflate the custom layout
                view = inflater.inflate(R.layout.item_tweet, parent, false);

                // Return a new holder instance
                viewHolder = new TextViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get the data model based on position
        Tweet tweet = tweetList.get(position);

        switch (holder.getItemViewType()) {
            case TEXT:
                populateTextTweet((TextViewHolder) holder, tweet);
                break;
            case TEXT_IMAGE:
                populatePhotoTweet((PhotoViewHolder) holder, tweet);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return tweetList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Tweet tweet = tweetList.get(position);
        List<Medium> mediaList = tweet.getEntities().getMedia();
        if (mediaList == null || mediaList.isEmpty())
            return TEXT; // -> ViewHolder
        else
            return TEXT_IMAGE; // -> PhotoViewHolder
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

    // Add a list of items
    public void add(int idx, Tweet tweet) {
        this.tweetList.add(idx, tweet);
        notifyItemInserted(idx);
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

    private void populateTextTweet(TextViewHolder holder, Tweet tweet) {
        // Set item views based on the data model
        final ImageView ivProfileImg = holder.ivTProfileImg;
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
        holder.tvTName.setText(tweet.getUser().getName());

        // screen name, handle, or alias that this user identifies themselves with
        holder.tvTScreenName.setText("@" + tweet.getUser().getScreenName());

        // UTC time when this Tweet was created
        holder.tvTCreatedAt.setText(getRelativeTimeAgo(tweet.getCreatedAt()));

        // actual UTF-8 text of the status update
        holder.tvTText.setText(tweet.getText());

        // set retweet and likes count ONLY if not 0
        int retweetCount = tweet.getRetweetCount();
        if (retweetCount != 0)
            holder.tvTRTCnt.setText(String.valueOf(retweetCount));
        int likeCount = tweet.getFavoriteCount();
        if (likeCount != 0)
            holder.tvTLikesCnt.setText(String.valueOf(likeCount));

        // if user is self, disable ivTRT
        if (me != null && tweet.getUser().getIdStr().equals(me.getIdStr())) {
            holder.ivTReTweet.setImageAlpha(100);
            holder.ivTReTweet.setClickable(false);
        }
    }

    private void populatePhotoTweet(PhotoViewHolder holder, Tweet tweet) {
        // Set item views based on the data model
        final ImageView ivTPProfileImg = holder.ivTPProfileImg;
        Glide.with(context)
                .load(tweet.getUser().getProfileImageUrl())
                .asBitmap()
                .fitCenter()
                .into(new BitmapImageViewTarget(ivTPProfileImg) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(5.0f);
                        ivTPProfileImg.setImageDrawable(circularBitmapDrawable);
                    }
                });

        // name of the user
        holder.tvTPName.setText(tweet.getUser().getName());

        // screen name, handle, or alias that this user identifies themselves with
        holder.tvTPScreenName.setText("@" + tweet.getUser().getScreenName());

        // UTC time when this Tweet was created
        holder.tvTPCreatedAt.setText(getRelativeTimeAgo(tweet.getCreatedAt()));

        // actual UTF-8 text of the status update
        holder.tvTPText.setText(tweet.getText());

        // set photo
        final ImageView ivTPPhoto = holder.ivTPPhoto;
        List<Medium> mediaList = tweet.getEntities().getMedia();
        String photoUrl = "";
        for (Medium medium : mediaList) {
            if ("photo".equals(medium.getType())) {
                photoUrl = medium.getMediaUrl();
                break;
            }
        }
        Glide.with(context)
                .load(photoUrl)
                .asBitmap()
                .fitCenter()
                .into(new BitmapImageViewTarget(ivTPPhoto) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(5.0f);
                        ivTPPhoto.setImageDrawable(circularBitmapDrawable);
                    }
                });

        // retweet and likes count
        holder.tvTPRTCnt.setText(String.valueOf(tweet.getRetweetCount()));
        holder.tvTPLikesCnt.setText(String.valueOf(tweet.getFavoriteCount()));
    }

    // tweet text only
    public class TextViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.ivTProfileImg) ImageView ivTProfileImg;

        @Bind(R.id.llTContainer) LinearLayout llTContainer;

        @Bind(R.id.tvTName) TextView tvTName;
        @Bind(R.id.tvTScreenName) TextView tvTScreenName;
        @Bind(R.id.tvTCreatedAt) TextView tvTCreatedAt;
        @Bind(R.id.tvTText) TextView tvTText;

        /************************** elements in rlTIcons ************************/
        @Bind(R.id.ivTReply) ImageView ivTReply;
        @Bind(R.id.ivTReTweet) ImageView ivTReTweet;
        @Bind(R.id.ivTLike) ImageView ivTLike;

        @Bind(R.id.tvTRTCnt) TextView tvTRTCnt;
        @Bind(R.id.tvTLikesCnt) TextView tvTLikesCnt;
        /************************** end of rlTIcons ************************/

        public TextViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // ivProfileImg onClickListener
            ivTProfileImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onProfilePicClick(itemView, getLayoutPosition());
                }
            });

            // Setup the click listener
            llTContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onTweetContainerClick(itemView, getLayoutPosition());
                }
            });

            ivTReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onReplyClick(getLayoutPosition());
                }
            });
        }
    }

    // tweet with photo
    public class PhotoViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.ivTPProfileImg) ImageView ivTPProfileImg;

        @Bind(R.id.llTPContainer) LinearLayout llTPContainer;

        @Bind(R.id.tvTPName) TextView tvTPName;
        @Bind(R.id.tvTPScreenName) TextView tvTPScreenName;
        @Bind(R.id.tvTPCreatedAt) TextView tvTPCreatedAt;
        @Bind(R.id.tvTPText) TextView tvTPText;

        @Bind(R.id.ivTPPhoto) ImageView ivTPPhoto;

        /************************** elements in rlTPIcons ************************/
        @Bind(R.id.ivTPReply) ImageView ivTPReply;
        @Bind(R.id.ivTPReTweet) ImageView ivTPReTweet;
        @Bind(R.id.ivTPLike) ImageView ivTPLike;

        @Bind(R.id.tvTPRTCnt) TextView tvTPRTCnt;
        @Bind(R.id.tvTPLikesCnt) TextView tvTPLikesCnt;
        /************************** end of rlTPIcons ************************/

        public PhotoViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // ivProfileImg onClickListener
            ivTPProfileImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onProfilePicClick(itemView, getLayoutPosition());
                }
            });

            // Setup the click listener
            llTPContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onTweetContainerClick(itemView, getLayoutPosition());
                }
            });

            ivTPReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onReplyClick(getLayoutPosition());
                }
            });
        }
    }
}
