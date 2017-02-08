package com.dunrite.pixbar;

import android.content.Context;
import android.graphics.PixelFormat;
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
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,            //Width
                WindowManager.LayoutParams.WRAP_CONTENT,            //Height
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,     //Overlay above everything
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL     //Don't react to touch events
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, //Allow to go anywhere on screen
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM;

        // navigation bar height
        int navigationBarHeight = 48;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        params.y -= navigationBarHeight; //move into navbar

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(relativeLayout, params);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layoutInflater.inflate(R.layout.buttons, relativeLayout);

        ButterKnife.bind(this, relativeLayout);

        Utils.setSpacing(getContext(), Utils.getSpacing(getContext()), homeButton);
        Utils.setScale(getContext(), Utils.getScale(getContext()), backButton, homeButton, recentsButton);
        Utils.setColor(Utils.getColor(getContext()), backButton, homeButton, recentsButton);
    }

    /**
     * Destroy the view
     */
    public void destroy() {
        windowManager.removeView(relativeLayout);
    }
}
