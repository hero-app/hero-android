package com.hero.utils;

import android.content.Context;

import com.hero.R;
import com.hero.utils.caching.Singleton;
import com.hero.utils.formatting.StringUtils;

public class Settings
{
    public static boolean isLoggedIn(Context context)
    {
        // Got a key?
        return !StringUtils.stringIsNullOrEmpty(getUserKey(context));
    }

    public static String getUserKey(Context context)
    {
        // Get saved preference
        return Singleton.getSharedPreferences(context).getString(context.getString(R.string.user_key), null);

    }
}
