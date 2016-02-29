package com.codepath.apps.mytinytwitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.activeandroid.query.Select;
import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mytinytwitter.R;
import com.codepath.apps.mytinytwitter.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.apps.mytinytwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.mytinytwitter.fragments.MentionsTimelineFragment;
import com.codepath.apps.mytinytwitter.fragments.TweetsListFragment;
import com.codepath.apps.mytinytwitter.models.Tweet;
import com.codepath.apps.mytinytwitter.models.User;
import com.codepath.apps.mytinytwitter.utils.RequestCodes;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity {

    @Bind(R.id.viewpager) ViewPager viewPager;
    @Bind(R.id.tabs) PagerSlidingTabStrip tabStrip;

    MenuItem miActionProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get viewpager and find pager sliding tabs
        ButterKnife.bind(this);

        // set adapter for viewpager
        viewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));

        // attach the tabs to the viewpager
        tabStrip.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timeline, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgress = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgress);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            int index = viewPager.getCurrentItem();
            TweetsPagerAdapter adapter = ((TweetsPagerAdapter) viewPager.getAdapter());
            TweetsListFragment fragment = (TweetsListFragment) adapter.getRegisteredFragment(index);
            Tweet tweet = null;

            if (requestCode == RequestCodes.REQUEST_CODE_COMPOSE) {
                tweet = Parcels.unwrap(data.getParcelableExtra("composedTweet"));
                fragment.add(0, tweet);
            }
            else if (requestCode == RequestCodes.REQUEST_CODE_REPLY) {
                tweet = Parcels.unwrap(data.getParcelableExtra("repliedTweet"));
                fragment.add(0, tweet);
            }

            fragment.getRvTweets().getLayoutManager().scrollToPosition(0);
        }
    }

    public void onProfileView(MenuItem item) {
        // launch the profile view
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void onComposeTweet(MenuItem item) {
        // launch the Compose view
        Intent intent = new Intent(this, ComposeActivity.class);

        User me = new Select()
                .from(User.class)
                .where("isMe = ?", 1)
                .executeSingle();

        intent.putExtra("myProfileImgUrl", me.getProfileImageUrl());
        startActivityForResult(intent, RequestCodes.REQUEST_CODE_COMPOSE);
    }

    // return the order of the fragments in the viewpager
    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {

        private String[] tabTitles = { "Home", "Mentions" };

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HomeTimelineFragment();
                case 1:
                    return new MentionsTimelineFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}