package com.dunrite.pixbar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Utility class for various common methods
 */
public class Utils {
    private static final int NAVBAR_HEIGHT_IN_DP = 48;
    private static final int DEFAULT_SPACING = 125;

    /**
     * private constructor. Should never call this
     */
    private Utils() {}

    /**
     * Gets scale for the buttons
     * @param c context
     * @return scale in px
     */
    public static int getScale(Context c) {
        SharedPreferences sharedPref = c.getSharedPreferences("scale", Context.MODE_PRIVATE);
        return sharedPref.getInt("scale", 50);
    }

    /**
     * Gets spacing for the buttons
     * @param c context
     * @return spacing in px
     */
    public static int getSpacing(Context c) {
        SharedPreferences sharedPref = c.getSharedPreferences("spacing", Context.MODE_PRIVATE);
        return sharedPref.getInt("spacing", 50);
    }

    /**
     * Gets color for the buttons
     * @param c context
     * @return color of buttons
     */
    public static int getColor(Context c) {
        SharedPreferences sharedPref = c.getSharedPreferences("color", Context.MODE_PRIVATE);
        return sharedPref.getInt("color", 0x00000);
    }

    /**
     * checks if service is enabled
     * @param c context
     * @return enabled
     */
    public static boolean isEnabled(Context c) {
        SharedPreferences sharedPref = c.getSharedPreferences("enabled", Context.MODE_PRIVATE);
        return sharedPref.getBoolean("enabled", false);
    }

    /**
     * checks if this is the first time running the app
     * @param c context
     * @return isFirst
     */
    public static boolean isFirstRun(Context c) {
        SharedPreferences sharedPref = c.getSharedPreferences("first", Context.MODE_PRIVATE);
        return sharedPref.getBoolean("first", true);
    }

    /**
     * checks if the guides should be showing or not
     * @param c context
     * @return showGuides
     */
    public static boolean showGuides(Context c) {
        SharedPreferences sharedPref = c.getSharedPreferences("guides", Context.MODE_PRIVATE);
        return sharedPref.getBoolean("guides", false);
    }

    /**
     * Saves a value in shared preferences for use later
     * @param a activity
     * @param type the type ("spacing", "scale")
     * @param value the value in px
     */
    public static void saveValue(Activity a, String type, int value) {
        SharedPreferences sharedPref = a.getSharedPreferences(type, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(type, value);
        editor.apply();
    }

    /**
     * Saves that the service is enabled in shared prefs
     * @param a activity
     * @param enabled isServiceEnabled
     */
    public static void setEnabled(Activity a, boolean enabled) {
        SharedPreferences sharedPref = a.getSharedPreferences("enabled", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("enabled", enabled);
        editor.apply();
    }

    /**
     * Saves whether or not to show the guides in the main activity
     * @param a activity
     * @param enabled areGuidesEnabled
     */
    public static void setShowGuides(Activity a, boolean enabled) {
        SharedPreferences sharedPref = a.getSharedPreferences("guides", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("guides", enabled);
        editor.apply();
    }

    /**
     * Sets whether or not this is the first time in the app
     * @param a activity
     * @param enabled isFirst
     */
    public static void setFirst(Activity a, boolean enabled) {
        SharedPreferences sharedPref = a.getSharedPreferences("first", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("first", enabled);
        editor.apply();
    }

    /**
     * Saves the value of the spacing between buttons in service and button preview
     * @param c context
     * @param spacing spacing in px
     * @param homeButton the home button, which stays in the center
     */
    public static void setSpacing(Context c, double spacing, ImageView homeButton, int orientation) {
        double realSpacing;
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) homeButton.getLayoutParams();
        if (orientation == 1) {
            realSpacing = convertDpToPx(c, DEFAULT_SPACING * (spacing/100));
            marginParams.setMargins((int) realSpacing, 0, (int) realSpacing, 0);
        } else {
            realSpacing = convertDpToPx(c, DEFAULT_SPACING * (spacing/100));
            //System.out.println(realSpacing);
            marginParams.setMargins(0, (int) realSpacing, 0, (int) realSpacing);
        }
        homeButton.requestLayout();
    }

    /**
     * Saves the value of the scale for all of the buttons in service and button preview
     * @param c context
     * @param scale scale in px
     * @param backButton reference to the back button object
     * @param homeButton reference to the home button object
     * @param recentsButton reference to the recents button object
     */
    public static void setScale(Context c, double scale,
                                ImageView backButton, ImageView homeButton, ImageView recentsButton, int orientation) {
        setScale(c, scale, backButton, orientation);
        setScale(c, scale, homeButton, orientation);
        setScale(c, scale, recentsButton, orientation);
    }

    public static void setScale(Context c, double scale, ImageView image, int orientation) {
        double realScale = convertDpToPx(c, NAVBAR_HEIGHT_IN_DP * (scale/100));
        if (orientation == 1)
            image.getLayoutParams().height = (int) realScale;
        else
            image.getLayoutParams().width = (int) realScale;
        image.requestLayout();
    }

    public static void setGuideHeights(Context c, double height, ImageView[] guides) {
        for (ImageView image : guides) {
            image.getLayoutParams().height = (int)height;
            image.requestLayout();
        }
    }

    /**
     * Saves the value of the color for all of the buttons in service anc button preview
     * @param color color
     * @param backButton reference to back button
     * @param homeButton reference to home button
     * @param recentsButton reference to recents button
     */
    public static void setColor(@ColorInt int color,
                                ImageView backButton, ImageView homeButton, ImageView recentsButton) {
        backButton.setColorFilter(color);
        homeButton.setColorFilter(color);
        recentsButton.setColorFilter(color);
    }

    /**
     * Converts values in dp to pixels
     * @param c context
     * @param dp value in dp
     * @return value in pixels
     */
    public static double convertDpToPx(Context c, double dp) {
        DisplayMetrics displayMetrics = c.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * Checks if app can draw over other apps
     * @param c
     * @return can draw over other apps
     */
    public static boolean hasDrawPermission(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(c);
        }
        return true;
    }

    public static int getOrientation(Resources resources) {
        return resources.getConfiguration().orientation;
    }

    public static int getNavigationBarHeight(Resources resources) {
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId <= 0) {
            return 0;
        }
        return resources.getDimensionPixelSize(resourceId);
    }

    public static int getStatusBarHeight(Resources resources) {
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

}
