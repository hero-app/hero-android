package com.hero.logic.login;

import android.content.Context;
import android.util.Log;

import com.hero.R;
import com.hero.config.API;
import com.hero.config.Logging;
import com.hero.model.request.login.LoginRequest;
import com.hero.model.response.login.LoginResponse;
import com.hero.utils.Settings;
import com.hero.utils.caching.Singleton;
import com.hero.utils.networking.HTTP;

public class UserLogin
{
    public static void doLogin(String accessToken, Context context) throws Exception
    {
        // Log to logcat
        Log.d(Logging.TAG, "Logging in with access token: " + accessToken);

        // Create login request
        LoginRequest register = new LoginRequest(accessToken);

        // Send the request to our API
        String json = HTTP.post(API.ENDPOINT + "/login", register);

        // Convert JSON to object
        LoginResponse response = Singleton.getJackson().readValue(json, LoginResponse.class);

        // Save user data
        Settings.setStringPreference(R.string.user_name, response.key, context);
        Settings.setStringPreference(R.string.user_fbid, response.key, context);
        Settings.setStringPreference(R.string.user_image, response.key, context);
        Settings.setStringPreference(R.string.user_key, response.key, context);
        Settings.setStringPreference(R.string.user_id, response.key, context);
    }
}
