package com.hero.logic.payments;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.hero.R;
import com.hero.config.API;
import com.hero.config.Logging;
import com.hero.model.request.common.AuthenticatedRequest;
import com.hero.model.request.payment.PledgeMoneyRequest;
import com.hero.model.response.common.BasicResponse;
import com.hero.model.response.payment.ClientTokenResponse;
import com.hero.utils.Settings;
import com.hero.utils.caching.Singleton;
import com.hero.utils.networking.HTTP;

public class Braintree
{
    public static void pledgeChallenge(String challengeId, String nonce, double amount, Context context) throws Exception
    {
        // Log to logcat
        Log.d(Logging.TAG, "Pledging money");

        // Grab user auth key
        String userKey = Settings.getUserKey(context);

        // Create login request
        PledgeMoneyRequest request = new PledgeMoneyRequest(userKey);

        // Set up some basic data
        request.amount = amount;
        request.nonce = nonce;

        // Send the request to our API
        String json = HTTP.post(API.ENDPOINT + "/challenges/" + Uri.encode(challengeId) + "/pledge", request);

        // Convert JSON to object
        BasicResponse response = Singleton.getJackson().readValue(json, BasicResponse.class);

        // Failed?
        if ( ! response.success )
        {
            throw new Exception(context.getString(R.string.pledgeFailed));
        }
    }

    public static String getClientToken(Context context) throws Exception
    {
        // Log to logcat
        Log.d(Logging.TAG, "Fetching client token");

        // Grab user auth key
        String userKey = Settings.getUserKey(context);

        // Create login request
        AuthenticatedRequest request = new AuthenticatedRequest(userKey);

        // Send the request to our API
        String json = HTTP.post(API.ENDPOINT + "/payment/client_token", request);

        // Convert JSON to object
        ClientTokenResponse response = Singleton.getJackson().readValue(json, ClientTokenResponse.class);

        // Return actual token
        return response.token;
    }
}

