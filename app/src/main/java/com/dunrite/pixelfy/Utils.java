package com.dunrite.pixelfy;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class for various common methods
 */
public class Utils {

    private Utils() {}

    public static int getScale(Activity a) {
        SharedPreferences sharedPref = a.getSharedPreferences("scale", Context.MODE_PRIVATE);
        return sharedPref.getInt("scale", 50);
    }

    public static int getSpacing(Activity a) {
        SharedPreferences sharedPref = a.getSharedPreferences("spacing", Context.MODE_PRIVATE);
        return sharedPref.getInt("spacing", 50);
    }

    public static boolean isEnabled(Activity a) {
        SharedPreferences sharedPref = a.getSharedPreferences("enabled", Context.MODE_PRIVATE);
        return sharedPref.getBoolean("enabled", false);
    }

    public static void saveValue(Activity a, String type, int value) {
        SharedPreferences sharedPref = a.getSharedPreferences(type, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(type, value);
        editor.apply();
    }
}
