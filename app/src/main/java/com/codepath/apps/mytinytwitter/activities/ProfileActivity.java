package com.codepath.apps.mytinytwitter.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.codepath.apps.mytinytwitter.R;
import com.codepath.apps.mytinytwitter.fragments.ProfileHeaderFragment;
import com.codepath.apps.mytinytwitter.fragments.UserTimelineFragment;

public class ProfileActivity extends AppCompatActivity {

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // get userId
        String userId = getIntent().getStringExtra(getString(R.string.userid));

        if ( savedInstanceState == null
                || !(userId.equals(this.userId))) {
            this.userId = userId;

            // create ProfileHeaderFragment
            ProfileHeaderFragment profileHeaderFragment = ProfileHeaderFragment.newInstance(userId);

            // display ProfileHeaderFragment within this activity (dynamically)
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainerProfHd, profileHeaderFragment);
            ft.commit();

            // create UserTimelineFragment
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(userId);

            // display userTimelineFragment within this activity (dynamically)
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();
        }
    }
}
