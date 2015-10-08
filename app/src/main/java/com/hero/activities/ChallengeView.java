package com.hero.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.hero.R;
import com.hero.config.Logging;
import com.hero.constants.ChallengeViewExtras;
import com.hero.model.Challenge;
import com.hero.utils.caching.Singleton;

public class ChallengeView extends Activity
{
    Challenge mChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Parse challenge from Intent extras
        unpackChallenge();

        // Initialize app UI
        initializeUI();
    }

    private void initializeUI()
    {
        // Load challenge UI
        setContentView(R.layout.activity_challenge);

        // Cache views

        // Set action bar title to challenge name
        setTitle(mChallenge.title);
    }

    private void unpackChallenge()
    {
        // Get challenge JSON
        String json = getIntent().getStringExtra(ChallengeViewExtras.CHALLENGE_EXTRA);

        try
        {
            // Convert JSON to object
            mChallenge = Singleton.getJackson().readValue(json, Challenge.class);
        }
        catch( Exception exc )
        {
            // Log it
            Log.e(Logging.TAG, "Challenge JSON decoding failed", exc);
        }
    }
}
