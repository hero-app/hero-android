package com.hero.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.hero.R;
import com.hero.config.Logging;
import com.hero.constants.VideoActivityExtras;
import com.hero.ui.AlertDialogBuilder;

public class Video extends Activity
{
    VideoView mVideoView;
    RelativeLayout mVideoContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Load app UI
        initializeUI();

        // Actually play the video
        playVideo();
    }

    private void initializeUI()
    {
        // Set video layout
        setContentView(R.layout.activity_video);

        // Grab video view from UI
        mVideoView = (VideoView) findViewById(R.id.video);
        mVideoContainer = (RelativeLayout) findViewById(R.id.videoContainer);

        // Handle video view click
        mVideoContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Replay video
                playVideo();
            }
        });
    }

    private void playVideo()
    {
        // Extract local video file path from extras
        String filePath = getIntent().getStringExtra(VideoActivityExtras.VIDEO_PATH);

        try
        {
            // Set video view path
            mVideoView.setVideoPath(filePath);

            // Start playing!
            mVideoView.start();
        }
        catch( Exception exc )
        {
            // Log it
            Log.e(Logging.TAG, "Video play failed", exc);

            // Build the dialog
            AlertDialogBuilder.showGenericDialog(getString(R.string.error), exc.toString(), Video.this, null);
        }
    }
}
