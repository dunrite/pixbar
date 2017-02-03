package com.dunrite.pixelfy;

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


    public ButtonLayer(Context context) {
        super(context);
        this.context = context;
        relativeLayout = new RelativeLayout(this.context);

        initWindowManager();
    }

    private void initWindowManager() {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM;
        params.y -= 145;

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(relativeLayout, params);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Here is the place where you can inject whatever layout you want.
        layoutInflater.inflate(R.layout.buttons, relativeLayout);

        ButterKnife.bind(this, relativeLayout);

        Utils.setSpacing(getContext(), Utils.getSpacing(getContext()), homeButton);
        Utils.setScale(getContext(), Utils.getScale(getContext()), backButton, homeButton, recentsButton);
    }

    public void destroy() {
        windowManager.removeView(relativeLayout);
    }
}
