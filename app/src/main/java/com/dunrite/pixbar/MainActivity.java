package com.dunrite.pixbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The Main Activity of the entire application
 */
public class MainActivity extends AppCompatActivity {
    //Service toggles
    @BindView(R.id.serviceToggle) Switch serviceToggle;

    //Seekbars
    @BindView(R.id.scaleBar) SeekBar scaleBar;
    @BindView(R.id.spacingBar) SeekBar spacingBar;

    //Preview button images
    @BindView(R.id.homeButton) ImageView homeButton;
    @BindView(R.id.backButton) ImageView backButton;
    @BindView(R.id.recentsButton) ImageView recentsButton;

    //Guides
    @BindView(R.id.showGuideCheck) CheckBox showGuideCheck;
    @BindView(R.id.homeLeftGuide) ImageView  homeLeftGuide;
    @BindView(R.id.homeRightGuide) ImageView  homeRightGuide;
    @BindView(R.id.backLeftGuide) ImageView  backLeftGuide;
    @BindView(R.id.backRightGuide) ImageView  backRightGuide;
    @BindView(R.id.recentsLeftGuide) ImageView  recentsLeftGuide;
    @BindView(R.id.recentsRightGuide) ImageView  recentsRightGuide;

    //Apply button
    @BindView(R.id.applyButton) Button applyButton;

    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    public Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Utils.isFirstRun(this)) {
            Intent intent = new Intent(this, IntroActivity.class); //call Intro class
            startActivity(intent);
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        activity = this;
        initialize();
    }

    /**
     * Initialize everything for the activity
     */
    private void initialize() {
        applyButton.setEnabled(Utils.isEnabled(this));

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(activity)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                    }
                }
                restartService();
            }
        });
        spacingBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //System.out.println("Spacing set to " + progress);
                Utils.setSpacing(getApplicationContext(), progress, homeButton);
                resetGuides();
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
                Utils.setScale(getApplicationContext(), progress, backButton, homeButton, recentsButton);
                resetGuides();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Utils.saveValue(activity, "scale", seekBar.getProgress());
            }
        });
        serviceToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.setEnabled(activity, isChecked);
                if(isChecked)
                    startService();
                else
                    stopService();
                applyButton.setEnabled(isChecked);
            }
        });
        showGuideCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    showGuides();
                else
                    hideGuides();
            }
        });

        serviceToggle.setChecked(Utils.isEnabled(activity));
        spacingBar.setProgress(Utils.getSpacing(activity));
        scaleBar.setProgress(Utils.getScale(activity));
        showGuideCheck.setChecked(Utils.showGuides(this));
    }

    /**
     * Restart the service that shows the buttons on the navbar
     */
    private void restartService() {
        stopService();
        startService();
    }

    /**
     * Start the service that shows the buttons on the navbar
     */
    private void startService() {
        if (Utils.hasDrawPermission(this)) {
            startService(new Intent(this, NavbarService.class));
        } else {
            serviceToggle.setChecked(false);
            Snackbar.make(findViewById(android.R.id.content), "You Need to Enable The Draw Permission", Snackbar.LENGTH_LONG)
                    .setAction("Enable", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                        }
                    }).setActionTextColor(Color.RED)
                    .show();
        }

    }

    /**
     * Stop the service that shows the buttons on the navbar
     */
    private void stopService() {
        stopService(new Intent(this, NavbarService.class));
    }

    /**
     * Show guide lines that help with placement
     */
    private void showGuides() {
        homeLeftGuide.setVisibility(View.VISIBLE);
        homeRightGuide.setVisibility(View.VISIBLE);

        backLeftGuide.setVisibility(View.VISIBLE);
        backRightGuide.setVisibility(View.VISIBLE);

        recentsLeftGuide.setVisibility(View.VISIBLE);
        recentsRightGuide.setVisibility(View.VISIBLE);
        Utils.setShowGuides(this, true);
        resetGuides();
    }

    /**
     * Hide guide lines that help with placement
     */
    private void hideGuides() {
        homeLeftGuide.setVisibility(View.GONE);
        homeRightGuide.setVisibility(View.GONE);

        backLeftGuide.setVisibility(View.GONE);
        backRightGuide.setVisibility(View.GONE);

        recentsLeftGuide.setVisibility(View.GONE);
        recentsRightGuide.setVisibility(View.GONE);
        Utils.setShowGuides(this, true);
    }

    /**
     * Reset the position of the guide lines
     */
    private void resetGuides() {
        homeLeftGuide.setX((homeButton.getX() + homeButton.getWidth()/2) - homeButton.getHeight()/2);
        homeRightGuide.setX((homeButton.getX() + homeButton.getWidth()/2) + homeButton.getHeight()/2);

        backLeftGuide.setX((backButton.getX() + backButton.getWidth()/2) - backButton.getHeight()/2);
        backRightGuide.setX((backButton.getX() + backButton.getWidth()/2) + backButton.getHeight()/2);

        recentsLeftGuide.setX((recentsButton.getX() + recentsButton.getWidth()/2) - recentsButton.getHeight()/2);
        recentsRightGuide.setX((recentsButton.getX() + recentsButton.getWidth()/2) + recentsButton.getHeight()/2);
    }
}

