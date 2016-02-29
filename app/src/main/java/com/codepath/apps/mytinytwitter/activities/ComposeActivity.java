package com.codepath.apps.mytinytwitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.codepath.apps.mytinytwitter.R;
import com.codepath.apps.mytinytwitter.fragments.ComposeDialogFragment;
import com.codepath.apps.mytinytwitter.models.Tweet;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ComposeActivity extends AppCompatActivity implements ComposeDialogFragment.ComposeDialogListener {

    @Bind(R.id.ivComposeCancel) ImageView ivComposeCancel;

    String myProfileImgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.bind(this);

        myProfileImgUrl = getIntent().getStringExtra("myProfileImgUrl");

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.flComposeContainer, ComposeDialogFragment.newInstance(null, null, myProfileImgUrl));
        ft.addToBackStack(null); // activities are automatically added to the backstack, but fragments must be manually added.
        ft.commit();
    }

    @OnClick(R.id.ivComposeCancel)
    void onComposeCancel() {
        finish();
    }

    @Override
    public void onFinishComposeDialog(Tweet tweet) {
        // Prepare data intent
        Intent result = new Intent();

        // Pass relevant data back as a result
        result.putExtra("composedTweet", Parcels.wrap(tweet));

        // Activity finished ok, return the data
        setResult(RESULT_OK, result); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }
}
