package com.codepath.apps.mytinytwitter.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.mytinytwitter.R;
import com.codepath.apps.mytinytwitter.TwitterApplication;
import com.codepath.apps.mytinytwitter.TwitterClient;
import com.codepath.apps.mytinytwitter.models.Tweet;
import com.codepath.apps.mytinytwitter.utils.MyGson;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ComposeDialogFragment extends DialogFragment {

    @Bind(R.id.btnTweet) Button btnTweet;
    @Bind(R.id.etNewTweet) EditText etNewTweet;
    @Bind(R.id.ivMyProfileImg) ImageView ivMyProfileImg;
    @Bind(R.id.tvCharCount) TextView tvCharCount;

    private static final int MAX_CHARS = 140;

    private int numChars = MAX_CHARS;
    private String inReplyToStatusId;

    private Context context;
    private TwitterClient twitterClient;

    // 1. Defines the listener interface with a method passing back data result.
    public interface ComposeDialogListener {
        void onFinishComposeDialog(Tweet tweet);
    }

    public ComposeDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ComposeDialogFragment newInstance(String inReplyToStatusId, String toScreenName, String myProfileImgUrl) {
        ComposeDialogFragment fragment = new ComposeDialogFragment();
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        Bundle args = new Bundle();
        if (toScreenName != null && !toScreenName.trim().isEmpty())
            args.putString("toScreenName", toScreenName);
        args.putString("myProfileImgUrl", myProfileImgUrl);
        fragment.setArguments(args);
        fragment.inReplyToStatusId = inReplyToStatusId;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        twitterClient = TwitterApplication.getRestClient();

        String myProfileImgUrl = getArguments().getString("myProfileImgUrl");
        Glide.with(context)
                .load(myProfileImgUrl)
                .asBitmap()
                .fitCenter()
                .into(new BitmapImageViewTarget(ivMyProfileImg) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(5.0f);
                        ivMyProfileImg.setImageDrawable(circularBitmapDrawable);
                    }
                });

        // by default, the btnTweet is disabled
        toggleButton(false);

        String toScreenName = getArguments().getString("toScreenName");
        if (toScreenName != null) {
            toScreenName = getString(R.string.at) + toScreenName;
            etNewTweet.setText(toScreenName);
        }
        // Show soft keyboard automatically and request focus to field
        etNewTweet.requestFocus();
    }

    @OnClick(R.id.btnTweet)
    void onClickBtnTweet() {
        String status = etNewTweet.getText().toString();
        twitterClient.postStatus(status, this.inReplyToStatusId, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String responseString = response.toString();
                Gson gson = MyGson.getMyGson();
                Tweet newTweet = gson.fromJson(responseString, Tweet.class);

                // Return newTweet back to activity through the implemented listener
                ComposeDialogListener listener = (ComposeDialogListener) getActivity();
                listener.onFinishComposeDialog(newTweet);
            }
        });
    }

    @OnTextChanged(R.id.etNewTweet)
    void onTextChanged(CharSequence text) {
        numChars = MAX_CHARS - text.length();
        tvCharCount.setText(String.valueOf(numChars));

        if (numChars > 0) {
            tvCharCount.setTextColor(Color.BLACK);

            if (numChars == MAX_CHARS) {
                toggleButton(false);
            }
            else {
                toggleButton(true);
            }
        }
        else {
            // numChars == 0
            tvCharCount.setTextColor(Color.RED);
        }
    }

    private void toggleButton(boolean isClickable) {
        if (btnTweet != null) {
            if (isClickable) {
                btnTweet.getBackground().setAlpha(255);
                btnTweet.setClickable(true);
            }
            else {
                btnTweet.getBackground().setAlpha(100);
                btnTweet.setClickable(false);
            }
        }
    }
}
