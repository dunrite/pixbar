package com.dunrite.pixelfy;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Utility class for various common methods
 */
public class Utils {
    private static final int NAVBAR_HEIGHT_IN_DP = 48;
    private static final int DEFAULT_SPACING = 125;

    private Utils() {}

    public static int getScale(Context c) {
        SharedPreferences sharedPref = c.getSharedPreferences("scale", Context.MODE_PRIVATE);
        return sharedPref.getInt("scale", 50);
    }

    public static int getSpacing(Context c) {
        SharedPreferences sharedPref = c.getSharedPreferences("spacing", Context.MODE_PRIVATE);
        return sharedPref.getInt("spacing", 50);
    }

    public static boolean isEnabled(Context c) {
        SharedPreferences sharedPref = c.getSharedPreferences("enabled", Context.MODE_PRIVATE);
        return sharedPref.getBoolean("enabled", false);
    }

    public static void saveValue(Activity a, String type, int value) {
        SharedPreferences sharedPref = a.getSharedPreferences(type, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(type, value);
        editor.apply();
    }

    public static void setEnabled(Activity a, boolean enabled) {
        SharedPreferences sharedPref = a.getSharedPreferences("enabled", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("enabled", enabled);
        editor.apply();
    }

    public static void setSpacing(Context c, double spacing, ImageView homeButton) {
        double realSpacing = convertDpToPx(c, DEFAULT_SPACING * (spacing/100));
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) homeButton.getLayoutParams();
        marginParams.setMargins((int) realSpacing, 0, (int) realSpacing, 0);
        homeButton.requestLayout();
    }

    public static void setScale(Context c, double scale,
                                ImageView backButton, ImageView homeButton, ImageView recentsButton) {
        double realScale = convertDpToPx(c, NAVBAR_HEIGHT_IN_DP * (scale/100));
        homeButton.getLayoutParams().height = (int) realScale;
        backButton.getLayoutParams().height = (int) realScale;
        recentsButton.getLayoutParams().height = (int) realScale;

        homeButton.requestLayout();
        backButton.requestLayout();
        recentsButton.requestLayout();
    }

    private static double convertDpToPx(Context c, double dp) {
        DisplayMetrics displayMetrics = c.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
