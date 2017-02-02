package com.dunrite.pixelfy;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.serviceToggle) Switch serviceToggle;
    @BindView(R.id.scaleBar) SeekBar scaleBar;
    @BindView(R.id.spacingBar) SeekBar spacingBar;
    @BindView(R.id.homeButton) ImageView homeButton;
    @BindView(R.id.backButton) ImageView backButton;
    @BindView(R.id.recentsButton) ImageView recentsButton;
    private final int NAVBAR_HEIGHT_IN_DP = 48;
    private final int DEFAULT_SPACING = 125;
    public Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        activity = this;
        initialize();
    }

    private void initialize() {
        spacingBar.setProgress(Utils.getSpacing(activity));
        scaleBar.setProgress(Utils.getScale(activity));
        spacingBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //System.out.println("Spacing set to " + progress);
                setSpacing(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Utils.saveValue(activity, "spacing", seekBar.getProgress());
            }
        });
        scaleBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setScale(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Utils.saveValue(activity, "progress", seekBar.getProgress());
            }
        });
    }

    private void setSpacing(double spacing) {
        double realSpacing = convertDpToPx(DEFAULT_SPACING * (spacing/100));
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) homeButton.getLayoutParams();
        marginParams.setMargins((int) realSpacing, 0, (int) realSpacing, 0);
        homeButton.requestLayout();
    }

    private void setScale(double scale) {
        double realScale = convertDpToPx(NAVBAR_HEIGHT_IN_DP * (scale/100));
        homeButton.getLayoutParams().height = (int) realScale;
        backButton.getLayoutParams().height = (int) realScale;
        recentsButton.getLayoutParams().height = (int) realScale;

        homeButton.requestLayout();
        backButton.requestLayout();
        recentsButton.requestLayout();
    }

    private double convertDpToPx(double dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}

