package com.dunrite.pixbar;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dunrite.pixbar.Utility.Utils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Creates layer of buttons to be overlayed onto the navbar
 */
public class ButtonLayer extends View {
    private Context context;
    private View keyboardView;
    private boolean keyboardOpen;
    private Point displaySize;
    private RelativeLayout relativeLayout;
    private WindowManager windowManager;
    @BindView(R.id.homeButtonS) ImageView homeButton;
    @BindView(R.id.backButtonS) ImageView backButton;
    @BindView(R.id.recentsButtonS) ImageView recentsButton;

    /**
     * Constructor
     * @param context application context
     */
    public ButtonLayer(Context context) {
        super(context);
        this.context = context;
        this.keyboardOpen = false;
        this.displaySize = new Point();

        keyboardView = new View(this.context);
        relativeLayout = new RelativeLayout(this.context);
        initWindowManager();
        adjustIfLG();
        setupOrder();
    }

    /**
     * Adjusts padding for non-nexus/G6 LG devices
     */
    private void adjustIfLG() {
        if (Objects.equals(Build.MANUFACTURER, "LGE") && !isLGNexus() && !isLGG6()) {
            int padding = Utils.convertDpToPx(context, 11);
            backButton.setPadding(padding, padding, padding, padding);
            recentsButton.setPadding(padding, padding, padding, padding);
        }
    }

    /**
     * Checks if device is an LG Nexus to ignore special sizing for non-nexus LG devices
     * @return is LG Nexus
     */
    private boolean isLGNexus() {
        return Objects.equals(Build.DEVICE, "mako") ||
                Objects.equals(Build.DEVICE, "hammerhead") ||
                Objects.equals(Build.DEVICE, "bullhead");
    }

    /**
     * Is the device an LG G6?
     * @return isG6
     */
    private boolean isLGG6() {
        return Objects.equals(Build.DEVICE, "lucye");
    }

    /**
     * Sets the style of the home button according to user settings
     */
    private void setupStyle() {
        int padding = 0;
        switch (Utils.getStyle(context)) {
            case 0: //Small ring
                if (isLGG6() || isLGNexus() || !Objects.equals(Build.MANUFACTURER, "LGE"))
                    padding = Utils.convertDpToPx(context, 16);
                else
                    padding = Utils.convertDpToPx(context, 12);
                homeButton.setImageResource(R.drawable.home);
                break;
            case 1: //Big ring
                if (isLGG6() || isLGNexus() || !Objects.equals(Build.MANUFACTURER, "LGE"))
                    padding = Utils.convertDpToPx(context, 11);
                else
                    padding = Utils.convertDpToPx(context, 5);
                homeButton.setImageResource(R.drawable.home_big_ring);
                break;
            case 2: //Fill
                if (isLGG6() || isLGNexus() || !Objects.equals(Build.MANUFACTURER, "LGE"))
                    padding = Utils.convertDpToPx(context, 14);
                else
                    padding = Utils.convertDpToPx(context, 7);
                homeButton.setImageResource(R.drawable.home);
                break;
            default:
                break;
        }
        homeButton.setPadding(padding,padding,padding,padding);
    }

    /**
     * Sets the order of the buttons according to settings
     */
    private void setupOrder(){
        RelativeLayout.LayoutParams backParams = (RelativeLayout.LayoutParams)backButton.getLayoutParams();
        RelativeLayout.LayoutParams recentsParams = (RelativeLayout.LayoutParams)recentsButton.getLayoutParams();
        //Order is flipped
        if (Utils.getOrder(getContext()) == 1) {
            if (Utils.getOrientation(getResources()) == 1) { //not landscape
                recentsParams.removeRule(RelativeLayout.END_OF);
                recentsParams.addRule(RelativeLayout.START_OF, R.id.homeButton);

                backParams.removeRule(RelativeLayout.START_OF);
                backParams.addRule(RelativeLayout.END_OF, R.id.homeButton);
            } else { //is Landscape
                recentsParams.removeRule(RelativeLayout.ABOVE);
                recentsParams.addRule(RelativeLayout.BELOW, R.id.homeButton);

                backParams.removeRule(RelativeLayout.BELOW);
                backParams.addRule(RelativeLayout.ABOVE, R.id.homeButton);
            }
            backButton.setLayoutParams(backParams);
            recentsButton.setLayoutParams(recentsParams);
        }

    }

    /**
     * Initialize the Window Manager
     */
    private void initWindowManager() {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        displaySize.set(displaymetrics.widthPixels, displaymetrics.heightPixels);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.buttons, relativeLayout);
        ButterKnife.bind(this, relativeLayout);

        if(Utils.getOrientation(getResources()) == 1)
            windowManager.addView(relativeLayout, layoutParamsPortrait());
        else
            windowManager.addView(relativeLayout, layoutParamsLandscape());

        keyboardView = layoutInflater.inflate(R.layout.keyboardview, null, false);
        windowManager.addView(keyboardView, keyboardLayoutParams());

        this.keyboardView.getViewTreeObserver().addOnGlobalLayoutListener(new keyboardListener());


        setupStyle();

        Utils.setSpacing(getContext(), Utils.getSpacing(getContext()), homeButton, Utils.getOrientation(getResources()));
        //Scale should be automatically handled now
        //Utils.setScale(getContext(), Utils.getScale(getContext()), backButton, homeButton, recentsButton, 1);
        Utils.setColor(Utils.getColor(getContext()), backButton, homeButton, recentsButton);

        //Show and hide with the navigation bar
        relativeLayout.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if((visibility & SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
                    //System.out.println("System Bars Visible");
                    relativeLayout.setVisibility(VISIBLE);
                } else {
                    //System.out.println("System Bars NOT Visible");
                    relativeLayout.setVisibility(INVISIBLE);
                }
            }
        });

        if((getSystemUiVisibility() & SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0) {
            relativeLayout.setVisibility(INVISIBLE);
        }

        windowManager.updateViewLayout(keyboardView, keyboardLayoutParams());
    }


    /**
     * Destroy the view
     */
    public void destroy() {
        windowManager.removeView(relativeLayout);
        windowManager.removeView(keyboardView);
    }

    /**
     * Rotates the back button. Called by the layout listener attached to the keyboardView
     */
    private void rotateBackButton() {
        if (keyboardOpen)
            backButton.setRotation(-90);
        else
            backButton.setRotation(0);
    }

    /**
     * Layout Parameters for the invisible keyboard view that is used to detect the keyboard
     * @return layout params
     */
    private WindowManager.LayoutParams keyboardLayoutParams() {
        return new WindowManager.LayoutParams(1, -1, WindowManager.LayoutParams.TYPE_PHONE, 131096,
                PixelFormat.TRANSLUCENT);
    }

    /**
     * Layout Paramters when in landscape orientation
     * @return
     */
    private WindowManager.LayoutParams layoutParamsLandscape() {
       final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, //Width
                WindowManager.LayoutParams.MATCH_PARENT, //Height
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,//Overlay above everything
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL     //Don't react to touch events
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS //Allow to go anywhere on screen
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.RIGHT;
        params.x = -Utils.getNavigationBarHeight(getContext(), true);
        params.y -= 0.5 * Utils.getStatusBarHeight(getResources());
        int h = displaySize.y;
        params.height = h + (Utils.getStatusBarHeight(getResources())/2);
        params.width = Utils.getNavigationBarHeight(getContext(), true);

        return params;
    }

    /**
     * Layout Parameters when in portrait orientation
     * @return
     */
    private WindowManager.LayoutParams layoutParamsPortrait() {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, //Width
                Utils.getNavigationBarHeight(getContext(), false), //Height
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,//Overlay above everything
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL     //Don't react to touch events
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS //Allow to go anywhere on screen
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM | Gravity.LEFT;
        params.y = -params.height; //move into navbar

        return params;
    }

    /**
     * Listens for layout changes and is able to see if the keyboard is open
     */
    class keyboardListener implements ViewTreeObserver.OnGlobalLayoutListener {
        public keyboardListener() {}

        @Override
        public void onGlobalLayout() {
            int navBarH = Utils.getNavigationBarHeight(getContext(), false);
            int statusBarH = Utils.getStatusBarHeight(getResources());

            ButtonLayer.this.keyboardOpen = ButtonLayer.this.keyboardView.getBottom() <
                    (displaySize.y - navBarH - statusBarH);
            rotateBackButton();
        }
    }

}
