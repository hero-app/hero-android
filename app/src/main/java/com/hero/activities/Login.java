package com.hero.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hero.R;
import com.hero.config.API;
import com.hero.config.Logging;
import com.hero.logic.login.UserLogin;
import com.hero.model.request.login.LoginRequest;
import com.hero.model.response.login.LoginResponse;
import com.hero.ui.AlertDialogBuilder;
import com.hero.utils.Settings;
import com.hero.utils.caching.Singleton;
import com.hero.utils.networking.HTTP;

import java.util.Arrays;
import java.util.List;

public class Login extends Activity
{
    ImageView mLoginButton;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Initialize app
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        // Initialize login UI
        initializeUI();
    }

    private void initializeUI()
    {
        setContentView(R.layout.activity_login);

        mLoginButton = (ImageView) findViewById(R.id.login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Log in manually
                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("public_profile"));
            }
        });

        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                // Send access token to our API
                new LoginAsync().execute(loginResult);
            }

            @Override
            public void onCancel()
            {
                // App code
                Toast.makeText(Login.this, "Login cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception)
            {
                // App code
                Toast.makeText(Login.this, "Login error: " + exception.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass it on to Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public class LoginAsync extends AsyncTask<LoginResult, String, Exception>
    {
        ProgressDialog mLoading;

        public LoginAsync()
        {
            // Loading dialog
            mLoading = new ProgressDialog(Login.this);

            // Prevent cancel
            mLoading.setCancelable(false);

            // Set default message
            mLoading.setMessage(getString(R.string.loading));

            // Show the progress dialog
            mLoading.show();
        }

        @Override
        protected Exception doInBackground(LoginResult... params)
        {
            // Get access token from AsyncTask params
            String token = params[0].getAccessToken().getToken();

            try
            {
                // Log in to API
                UserLogin.doLogin(token, Login.this);
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
                goToMain();
            }
            else
            {
                // Log it
                Log.e(Logging.TAG, "Login failed", exc);

                // Build an error message
                String errorMessage = getString(R.string.loginFailed) + "\n\n" + exc.toString();

                // Build the dialog
                AlertDialogBuilder.showGenericDialog(getString(R.string.error), errorMessage, Login.this, null);
            }
        }
    }

    private void goToMain()
    {
        // Start the Main activity
        startActivity( new Intent().setClass(this, Main.class));

        // Close this one
        finish();
    }
}
