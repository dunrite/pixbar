package com.dunrite.pixbar;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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
        relativeLayout = new RelativeLayout(this.context);

        initWindowManager();
    }

    /**
     * Initialize the Window Manager
     */
    private void initWindowManager() {
        int width;
        int height;
        if (Utils.getOrientation(getResources()) == 1) {
            width = WindowManager.LayoutParams.MATCH_PARENT;
            height = WindowManager.LayoutParams.WRAP_CONTENT;
        } else {
            height = WindowManager.LayoutParams.MATCH_PARENT;
            width = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                width,            //Width
                height,            //Height
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

            DisplayMetrics displaymetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displaymetrics);
            int h = displaymetrics.heightPixels;

            params.height = h + (Utils.getStatusBarHeight(getResources())/2);
        }
        windowManager.addView(relativeLayout, params);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layoutInflater.inflate(R.layout.buttons, relativeLayout);

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
    }

    /**
     * Destroy the view
     */
    public void destroy() {
        windowManager.removeView(relativeLayout);
    }

}
