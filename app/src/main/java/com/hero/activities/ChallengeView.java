package com.hero.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.hero.model.ParticipantVideo;
import com.hero.ui.AlertDialogBuilder;
import com.hero.ui.images.CircularImageTransformation;
import com.hero.utils.caching.Singleton;
import com.hero.utils.formatting.StringUtils;
import com.hero.utils.media.VideoDecoder;
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

        // Get inflater service
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Traverse participants
        for ( final Participant participant : mChallenge.participants )
        {
            // Inflate the layout
            View participantView = layoutInflater.inflate(R.layout.activity_challenge_participant, null);

            // Cache views
            TextView name = (TextView)participantView.findViewById(R.id.name);
            ImageView image = (ImageView)participantView.findViewById(R.id.image);
            ImageView preview = (ImageView)participantView.findViewById(R.id.videoPreview);
            ProgressBar loading = (ProgressBar)participantView.findViewById(R.id.loading);

            // Change progress bar color (we need @color/accent to be grey for sidebar)
            loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.primary), android.graphics.PorterDuff.Mode.SRC_IN);

            // Set metadata
            name.setText(participant.user.name);

            // Load video preview (if exists)
            if ( ! StringUtils.stringIsNullOrEmpty(participant.video.preview))
            {
                // Show loading
                mLoading.setVisibility(View.VISIBLE);

                // Actually load preview
                Picasso.with(this).load(participant.video.preview).into(preview, new Callback()
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

            // Load participant image
            Picasso.with(this).load(participant.user.image).transform(new CircularImageTransformation()).into(image);

            // Handle video click
            preview.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    // Launch video
                    new DecodeVideoAsync().execute(participant.video);
                }
            });

            // Add to video container as a child
            mVideoContainer.addView(participantView);
        }
    }

    public class DecodeVideoAsync extends AsyncTask<ParticipantVideo, String, Exception>
    {
        ParticipantVideo mVideo;
        ProgressDialog mLoading;

        public DecodeVideoAsync()
        {
            // Loading dialog
            mLoading = new ProgressDialog(ChallengeView.this);

            // Prevent cancel
            mLoading.setCancelable(false);

            // Set default message
            mLoading.setMessage(getString(R.string.loading));

            // Show the progress dialog
            mLoading.show();
        }

        @Override
        protected Exception doInBackground(ParticipantVideo... params)
        {
            // Get first parameter
            mVideo = params[0];

            try
            {
                // Decode the base64-encoded video and save it to external storage
                mVideo.file = VideoDecoder.decodeBase64Video(mVideo.data, ChallengeView.this);
            }
            catch (Exception exc)
            {
                // Return exception to onPostExecute
                return exc;
            }

            // We're good!
            return null;
        }

        @Override
        protected void onPostExecute(Exception exc)
        {
            // Activity dead?
            if (isFinishing())
            {
                return;
            }

            // Hide loading
            if (mLoading.isShowing())
            {
                mLoading.dismiss();
            }

            // Success?
            if ( exc == null )
            {
                // We can now play the video
                playParticipantVideo(mVideo);
            }
            else
            {
                // Log it
                Log.e(Logging.TAG, "Video decode failed", exc);

                // Build the dialog
                AlertDialogBuilder.showGenericDialog(getString(R.string.error), exc.toString(), ChallengeView.this, null);
            }
        }
    }

    private void playParticipantVideo(ParticipantVideo video)
    {
        // Gotta have a locally-saved video for this to work
        if ( StringUtils.stringIsNullOrEmpty(video.file))
        {
            return;
        }

        // Create a new player (recreate it every time)
        MediaPlayer player = new MediaPlayer();

        try
        {
            // Set path to video file
            player.setDataSource(video.file);

            // Prepare the media player (must call before start())
            player.prepare();

            // Finally, start playing the video file (in a new window)
            player.start();
        }
        catch( Exception exc )
        {
            // Log it
            Log.e(Logging.TAG, "Video play failed", exc);

            // Build the dialog
            AlertDialogBuilder.showGenericDialog(getString(R.string.error), exc.toString(), ChallengeView.this, null);
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
