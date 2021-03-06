package com.dunrite.pixbar.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.lang.reflect.InvocationTargetException;

/**
 * Utility class for various common methods
 */
public class Utils {
    private static final int DEFAULT_SPACING = 125;

    /**
     * private constructor. Should never call this
     */
    private Utils() {}

    /**
     * Gets style of the buttons
     * @param c context
     * @return scale in px
     */
    public static int getStyle(Context c) {
        SharedPreferences sharedPref = c.getSharedPreferences("style", Context.MODE_PRIVATE);
        return sharedPref.getInt("style", 0);
    }

    /**
     * Gets order of the buttons
     * @param c context
     * @return order of buttons
     */
    public static int getOrder(Context c) {
        SharedPreferences sharedPref = c.getSharedPreferences("order", Context.MODE_PRIVATE);
        return sharedPref.getInt("order", 0);
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
        SharedPreferences sharedPref = c.getSharedPreferences("tint", Context.MODE_PRIVATE);
        return sharedPref.getInt("tint", 0xFFFFFF);
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
     * checks if service is enabled on boot
     * @param c context
     * @return enabled
     */
    public static boolean isEnabledOnBoot(Context c) {
        SharedPreferences sharedPref = c.getSharedPreferences("boot", Context.MODE_PRIVATE);
        return sharedPref.getBoolean("boot", false);
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
     * Saves that the service is enabled on bootin shared prefs
     * @param a activity
     * @param enabled isServiceEnabled
     */
    public static void setOnBoot(Activity a, boolean enabled) {
        SharedPreferences sharedPref = a.getSharedPreferences("boot", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("boot", enabled);
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
        double realScale = convertDpToPx(c, getNavigationBarHeight(c, false) * (scale/100));
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
    public static int convertDpToPx(Context c, double dp) {
        //DisplayMetrics displayMetrics = c.getResources().getDisplayMetrics();
        //return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        final float scale = c.getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (dp * scale + 0.5f);
        return padding_in_px;
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

    /**
     * Gets current screen orientation
     * @param resources
     * @return
     */
    public static int getOrientation(Resources resources) {
        return resources.getConfiguration().orientation;
    }

    /**
     * returns the height of the navbar for placement of the buttons
     * @param c current contect
     * @param isLandscape is the screen in landscape mode
     * @return height
     */
    public static int getNavigationBarHeight(Context c, boolean isLandscape) {
        return isLandscape ? getNavigationBarSize(c).x : getNavigationBarSize(c).y;
    }

    /**
     * Returns the total size of the navigation bar using a method that works on all devices
     * @param context current context
     * @return navbar size in height and width
     */
    private static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    /**
     * Get the usable screen area to help determine the navbar size
     * @param context current context
     * @return
     */
    private static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    /**
     * Gets the actual size of the device's screen
     * @param context current context
     * @return
     */
    private static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException e) {} catch (InvocationTargetException e) {} catch (NoSuchMethodException e) {}
        }

        return size;
    }

    /**
     * returns the height of the statusbar
     * @param resources
     * @return height in px
     */
    public static int getStatusBarHeight(Resources resources) {
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

}
