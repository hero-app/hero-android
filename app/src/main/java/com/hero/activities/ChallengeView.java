package com.hero.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hero.R;
import com.hero.config.Logging;
import com.hero.constants.ChallengeViewExtras;
import com.hero.model.Challenge;
import com.hero.model.Participant;
import com.hero.ui.images.CircularImageTransformation;
import com.hero.utils.caching.Singleton;
import com.hero.utils.formatting.StringUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class ChallengeView extends Activity
{
    Challenge mChallenge;

    TextView mRules;
    TextView mTitle;
    TextView mDate;
    TextView mDescription;
    TextView mCategory;
    TextView mCreatorName;

    ImageView mImage;
    ImageView mCreatorImage;

    ProgressBar mLoading;
    LinearLayout mVideoContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Parse challenge from Intent extras
        unpackChallenge();

        // Initialize app UI
        initializeUI();

        // Load info into elements
        displayChallenge();
    }

    private void initializeUI()
    {
        // Load challenge UI
        setContentView(R.layout.activity_challenge);

        // Cache the view resources
        mDate = (TextView) findViewById(R.id.date);
        mTitle = (TextView) findViewById(R.id.title);
        mRules = (TextView) findViewById(R.id.rules);
        mImage = (ImageView) findViewById(R.id.image);
        mCategory = (TextView) findViewById(R.id.category);
        mLoading = (ProgressBar) findViewById(R.id.loading);
        mDescription = (TextView) findViewById(R.id.description);
        mCreatorName = (TextView) findViewById(R.id.creatorName);
        mCreatorImage = (ImageView) findViewById(R.id.creatorImage);
        mVideoContainer = (LinearLayout) findViewById(R.id.videoContainer);

        // Change progress bar color (we need @color/accent to be grey for sidebar)
        mLoading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.primary), android.graphics.PorterDuff.Mode.SRC_IN);
    }
    
    private void displayChallenge()
    {
        // Set action bar title to challenge name
        setTitle(mChallenge.title);

        // Set metadata
        mRules.setText(mChallenge.rules);
        mTitle.setText(mChallenge.title);
        mCategory.setText(mChallenge.category);
        mDescription.setText(mChallenge.description);

        // Convert to human-readable date
        String date = DateFormat.getDateFormat(this).format(new Date(mChallenge.creationDate));

        // Display it
        mDate.setText(date);

        // Got a creator?
        if ( mChallenge.creator != null )
        {
            // Set creator name
            mCreatorName.setText(mChallenge.creator.name);

            // Challenge has an image?
            if (!StringUtils.stringIsNullOrEmpty(mChallenge.image))
            {
                // Load image with Picasso
                Picasso.with(this).load(mChallenge.image).into(mImage, new Callback()
                {
                    @Override
                    public void onSuccess()
                    {
                        // Hide loading
                        mLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError()
                    {
                        // Log it
                        Log.e(Logging.TAG, "Image load failed");
                    }
                });
            }
        }

        // Creator has an image?
        if ( !StringUtils.stringIsNullOrEmpty(mChallenge.creator.image))
        {
            // Load image with Picasso
            Picasso.with(this).load(mChallenge.creator.image).transform(new CircularImageTransformation()).into(mCreatorImage);
        }

        // Handle participants and video display
        displayParticipantVideos();
    }

    private void displayParticipantVideos()
    {
        // No participants? GTFO.
        if ( mChallenge.participants.size() == 0 )
        {
            return;
        }

        

        // Traverse participants
        for ( Participant participant : mChallenge.participants )
        {
            // Inflate participant layout
        }
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
