package com.hero.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.hero.R;
import com.hero.config.Logging;
import com.hero.constants.BraintreeConstants;
import com.hero.constants.ChallengeActivityExtras;
import com.hero.logic.payments.Braintree;
import com.hero.model.Participant;
import com.hero.model.ParticipantVideo;
import com.hero.ui.AlertDialogBuilder;
import com.hero.ui.images.CircularImageTransformation;
import com.hero.utils.caching.Singleton;
import com.hero.utils.formatting.StringUtils;
import com.hero.utils.media.VideoDecoder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;

public class Challenge extends Activity
{
    com.hero.model.Challenge mChallenge;

    TextView mRules;
    TextView mTitle;
    TextView mDate;
    TextView mPledged;
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
        mPledged = (TextView) findViewById(R.id.pledged);
        mCategory = (TextView) findViewById(R.id.category);
        mLoading = (ProgressBar) findViewById(R.id.loading);
        mDescription = (TextView) findViewById(R.id.description);
        mCreatorName = (TextView) findViewById(R.id.creatorName);
        mCreatorImage = (ImageView) findViewById(R.id.creatorImage);
        mVideoContainer = (LinearLayout) findViewById(R.id.videoContainer);

        // Pledegd text click
        mPledged.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Get token and show Braintree payment activity
                new GetBraintreeTokenAsync().execute();
            }
        });

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
                    new PlayVideoAsync().execute(participant.video);
                }
            });

            // Add to video container as a child
            mVideoContainer.addView(participantView);
        }
    }

    public class PlayVideoAsync extends AsyncTask<ParticipantVideo, String, Exception>
    {
        ParticipantVideo mVideo;
        ProgressDialog mLoading;

        public PlayVideoAsync()
        {
            // Loading dialog
            mLoading = new ProgressDialog(Challenge.this);

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
                // Cache the video (if not already cached)
                mVideo.file = VideoDecoder.getCachedVideoPath(mVideo.url, Challenge.this);
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
                AlertDialogBuilder.showGenericDialog(getString(R.string.error), exc.toString(), Challenge.this, null);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Braintree response?
        if (requestCode == BraintreeConstants.ACTIVITY_REQUEST_CODE)
        {
            // We good?
            if (resultCode == BraintreePaymentActivity.RESULT_OK)
            {
                // Extract nonce
                String paymentMethodNonce = data.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);

                // Send to server and pledge money
                new PledgePaymentAsync().execute(paymentMethodNonce);
            }
        }
    }

    public class PledgePaymentAsync extends AsyncTask<String, String, Exception>
    {
        ProgressDialog mLoading;

        public PledgePaymentAsync()
        {
            // Loading dialog
            mLoading = new ProgressDialog(Challenge.this);

            // Prevent cancel
            mLoading.setCancelable(false);

            // Set default message
            mLoading.setMessage(getString(R.string.loading));

            // Show the progress dialog
            mLoading.show();
        }

        @Override
        protected Exception doInBackground(String... params)
        {
            // Grab nonce from params
            String nonce = params[0];

            // Pledge a dollar
            double amount = 1.0;

            try
            {
                // Actually pledge via Braintree
                Braintree.pledgeChallenge(mChallenge.id, nonce, amount, Challenge.this);
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

            // Failed?
            if ( exc != null )
            {
                // Log it
                Log.e(Logging.TAG, "Nonce send failed", exc);

                // Build the dialog
                AlertDialogBuilder.showGenericDialog(getString(R.string.error), exc.toString(), Challenge.this, null);
            }
            else
            {
                // Build the dialog
                AlertDialogBuilder.showGenericDialog(getString(R.string.success), getString(R.string.pledgeSuccess), Challenge.this, null);
            }
        }
    }

    public class GetBraintreeTokenAsync extends AsyncTask<Integer, String, Exception>
    {
        ProgressDialog mLoading;

        public GetBraintreeTokenAsync()
        {
            // Loading dialog
            mLoading = new ProgressDialog(Challenge.this);

            // Prevent cancel
            mLoading.setCancelable(false);

            // Set default message
            mLoading.setMessage(getString(R.string.loading));

            // Show the progress dialog
            mLoading.show();
        }

        @Override
        protected Exception doInBackground(Integer... params)
        {
            try
            {
                // Grab the actual token
                String token = Braintree.getClientToken(Challenge.this);

                // Start the auth activity
                Intent intent = new Intent(Challenge.this, BraintreePaymentActivity.class);
                intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, token);
                startActivityForResult(intent, BraintreeConstants.ACTIVITY_REQUEST_CODE);
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

            // Failed?
            if ( exc != null )
            {
                // Log it
                Log.e(Logging.TAG, "Client token generation failed", exc);

                // Build the dialog
                AlertDialogBuilder.showGenericDialog(getString(R.string.error), exc.toString(), Challenge.this, null);
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
        Intent videoIntent = new Intent(Intent.ACTION_VIEW);

        // Set video path
        videoIntent.setDataAndType(Uri.fromFile(new File(video.file)), "video/*");

        // Start video activity
        startActivity(videoIntent);
    }

    private void unpackChallenge()
    {
        // Get challenge JSON
        String json = getIntent().getStringExtra(ChallengeActivityExtras.CHALLENGE_EXTRA);

        try
        {
            // Convert JSON to object
            mChallenge = Singleton.getJackson().readValue(json, com.hero.model.Challenge.class);
        }
        catch( Exception exc )
        {
            // Log it
            Log.e(Logging.TAG, "Challenge JSON decoding failed", exc);
        }
    }
}
