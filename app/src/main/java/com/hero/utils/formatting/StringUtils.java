package com.hero.utils.formatting;

public class StringUtils
{
    public static boolean stringIsNullOrEmpty(String inputString)
    {
        // String is null? return true
        if (inputString == null)
        {
            return true;
        }

        // String is empty? true
        if (inputString.trim().equals(""))
        {
            return true;
        }

        // String is not empty
        return false;
    }
}
