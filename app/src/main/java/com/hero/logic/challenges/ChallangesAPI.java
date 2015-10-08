package com.hero.logic.challenges;

import android.content.Context;
import android.util.Log;

import com.hero.R;
import com.hero.config.API;
import com.hero.config.Logging;
import com.hero.model.Challenge;
import com.hero.model.request.challenges.FeedResponse;
import com.hero.model.request.common.AuthenticatedRequest;
import com.hero.model.request.login.LoginRequest;
import com.hero.model.response.login.LoginResponse;
import com.hero.utils.Settings;
import com.hero.utils.caching.Singleton;
import com.hero.utils.networking.HTTP;

import java.util.List;

public class ChallangesAPI
{
    public static List<Challenge> getFeed(Context context) throws Exception
    {
        // Log to logcat
        Log.d(Logging.TAG, "Fetching feed");

        // Grab user auth key
        String userKey = Settings.getUserKey(context);

        // Create login request
        AuthenticatedRequest request = new AuthenticatedRequest(userKey);

        // Send the request to our API
        String json = HTTP.post(API.ENDPOINT + "/feed", request);

        // Convert JSON to object
        FeedResponse response = Singleton.getJackson().readValue(json, FeedResponse.class);

        // Return only challenges
        return response.challenges;
    }
}
