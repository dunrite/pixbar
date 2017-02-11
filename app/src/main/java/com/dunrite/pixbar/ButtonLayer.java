package com.dunrite.pixbar;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
    }

    /**
     * Initialize the Window Manager
     * TODO: THIS METHOD IS A MESS
     */
    private void initWindowManager() {
        int wmWidth;
        int wmHeight;
        if (Utils.getOrientation(getResources()) == 1) {
            wmWidth = WindowManager.LayoutParams.MATCH_PARENT;
            wmHeight = WindowManager.LayoutParams.WRAP_CONTENT;
        } else {
            wmHeight = WindowManager.LayoutParams.MATCH_PARENT;
            wmWidth = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        displaySize.set(displaymetrics.widthPixels, displaymetrics.heightPixels);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                wmWidth,            //Width
                wmHeight,            //Height
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,//Overlay above everything
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL     //Don't react to touch events
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS //Allow to go anywhere on screen
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        //System.out.println(Utils.getOrientation((getResources())));
        if (Utils.getOrientation(getResources()) == 1) {
            params.gravity = Gravity.BOTTOM | Gravity.LEFT;
            params.y = -Utils.getNavigationBarHeight(getResources()); //move into navbar
        } else {
            params.gravity = Gravity.RIGHT;
            params.x = -Utils.getNavigationBarHeight(getResources());
            params.y -= 0.5 * Utils.getStatusBarHeight(getResources());
            int h = displaymetrics.heightPixels;
            params.height = h + (Utils.getStatusBarHeight(getResources())/2);
        }
        windowManager.addView(relativeLayout, params);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layoutInflater.inflate(R.layout.buttons, relativeLayout);

        keyboardView = layoutInflater.inflate(R.layout.keyboardview, null, false);
        windowManager.addView(keyboardView, keyboardLayoutParams());


        this.keyboardView.getViewTreeObserver().addOnGlobalLayoutListener(new keyboardListener());

        ButterKnife.bind(this, relativeLayout);

        Utils.setSpacing(getContext(), Utils.getSpacing(getContext()), homeButton, Utils.getOrientation(getResources()));
        //Scale should be automatically handled now
        //Utils.setScale(getContext(), Utils.getScale(getContext()), backButton, homeButton, recentsButton, 1);
        Utils.setColor(Utils.getColor(getContext()), backButton, homeButton, recentsButton);

        //Show and hide with the navigation bar
        relativeLayout.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if((visibility & SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    //System.out.println("System Bars Visible");
                    relativeLayout.setVisibility(VISIBLE);
                } else {
                    //System.out.println("System Bars NOT Visible");
                    relativeLayout.setVisibility(INVISIBLE);
                }
            }
        });

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
     * Listens for layout changes and is able to see if the keyboard is open
     */
    class keyboardListener implements ViewTreeObserver.OnGlobalLayoutListener {
        public keyboardListener() {}

        @Override
        public void onGlobalLayout() {
            int navBarH = Utils.getNavigationBarHeight(getResources());
            int statusBarH = Utils.getStatusBarHeight(getResources());

            ButtonLayer.this.keyboardOpen = ButtonLayer.this.keyboardView.getBottom() <
                    (displaySize.y - navBarH - statusBarH);
            rotateBackButton();
        }
    }

}
