package com.hero.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.hero.R;
import com.hero.activities.Challenge;
import com.hero.config.Logging;
import com.hero.constants.ChallengeActivityExtras;
import com.hero.fragments.adapters.ChallengeAdapter;
import com.hero.logic.challenges.ChallangesAPI;
import com.hero.ui.AlertDialogBuilder;
import com.hero.utils.caching.Singleton;

import java.util.ArrayList;
import java.util.List;

public class Feed extends Fragment
{
    boolean mIsReloading;

    ProgressBar mLoading;
    SwipeRefreshLayout mSwipeContainer;

    ListView mChallengeList;
    ChallengeAdapter mChallengeAdapter;

    List<com.hero.model.Challenge> mUpdatedChallenges;
    List<com.hero.model.Challenge> mDisplayedChallenges;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Member initialization
        mUpdatedChallenges = new ArrayList<>();
        mDisplayedChallenges = new ArrayList<>();

        // UI initialization
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        // View caching
        mLoading = (ProgressBar)rootView.findViewById(R.id.loading);
        mChallengeList = (ListView)rootView.findViewById(R.id.challenges);

        // Swipe to refresh
        mSwipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                // Reload feed
                if ( ! mIsReloading )
                {
                    new LoadFeedAsync().execute();
                }
            }
        });


        // Change progress bar color (we need @color/accent to be grey for sidebar)
        mLoading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.primary), android.graphics.PorterDuff.Mode.SRC_IN);

        // Adapter init
        mChallengeAdapter = new ChallengeAdapter(getActivity(), mDisplayedChallenges, R.layout.list_item_challenge);

        // Link to list
        mChallengeList.setAdapter(mChallengeAdapter);

        // Handle challenge list click
        mChallengeList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                // Get pressed item
                com.hero.model.Challenge item = mDisplayedChallenges.get(position);

                // Prepare JSON
                String json = "";

                try
                {
                    // Convert challenge to JSON
                    json = Singleton.getJackson().writeValueAsString(item);
                }
                catch( Exception exc )
                {
                    // Log it
                    Log.e(Logging.TAG, "Challenge JSON decoding failed", exc);
                }

                // Prepare view intent
                Intent challengeView = new Intent(getActivity(), Challenge.class);

                // Insert challenge
                challengeView.putExtra(ChallengeActivityExtras.CHALLENGE_EXTRA, json);

                // Open challenge view
                startActivity(challengeView);
            }
        });

        // All done
        return rootView;
    }


    @Override
    public void onResume()
    {
        super.onResume();

        // Reload feed
        if ( ! mIsReloading )
        {
            new LoadFeedAsync().execute();
        }
    }

    private void updateChallengesList()
    {
        // Clear global list
        mDisplayedChallenges.clear();

        // Add all the updated challenges
        mDisplayedChallenges.addAll(mUpdatedChallenges);

        // Items changed
        mChallengeAdapter.notifyDataSetChanged();

        // Hide pull to refresh
        mSwipeContainer.setRefreshing(false);
    }

    public class LoadFeedAsync extends AsyncTask<Integer, String, Exception>
    {
        public LoadFeedAsync()
        {
            // Prevent concurrent reload
            mIsReloading = true;

            // Show the circular progress (if no challenges)
            if ( mDisplayedChallenges.size() == 0 )
            {
                mLoading.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Exception doInBackground(Integer... params)
        {
            List<com.hero.model.Challenge> challenges;

            try
            {
                // Get feed challenges
                challenges = ChallangesAPI.getFeed(getActivity());
            }
            catch (Exception exc)
            {
                // Return exception to onPostExecute
                return exc;
            }

            // Clear global list
            mUpdatedChallenges.clear();

            // Add all the new challenges
            mUpdatedChallenges.addAll(challenges);

            // We're good!
            return null;
        }

        @Override
        protected void onPostExecute(Exception exc)
        {
            // No longer reloading
            mIsReloading = false;

            // Activity dead?
            if (getActivity() == null || getActivity().isFinishing())
            {
                return;
            }

            // Hide loading
            mLoading.setVisibility(View.GONE);

            // Success?
            if ( exc == null )
            {
                updateChallengesList();
            }
            else
            {
                // Log it
                Log.e(Logging.TAG, "/feed failed", exc);

                // Build the dialog
                AlertDialogBuilder.showGenericDialog(getString(R.string.error), exc.toString(), getActivity(), null);
            }
        }
    }
}
