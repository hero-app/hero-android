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
        setContentView(R.layout.activity_challenge);

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
