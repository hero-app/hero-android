package com.hero.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hero.R;
import com.hero.config.Logging;
import com.hero.logic.challenges.ChallangesAPI;
import com.hero.model.Challenge;
import com.hero.ui.AlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class Feed extends Fragment
{
    boolean mIsReloading;

    ProgressBar mLoading;

    List<Challenge> mUpdatedChallenges;
    List<Challenge> mDisplayedChallenges;

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
    }

    public class LoadFeedAsync extends AsyncTask<Integer, String, Exception>
    {
        public LoadFeedAsync()
        {
            // Prevent concurrent reload
            mIsReloading = true;

            // Show the circular progress
            mLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Exception doInBackground(Integer... params)
        {
            List<Challenge> challenges;

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
            mIsReloading = true;

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
