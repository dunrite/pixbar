package com.dunrite.pixbar.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.dunrite.pixbar.NavbarService;
import com.dunrite.pixbar.R;
import com.dunrite.pixbar.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The Main Activity of the entire application
 */
public class MainActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {
    //Service toggles
    @BindView(R.id.serviceToggle) Switch serviceToggle;

    //Seekbars
    @BindView(R.id.scaleBar) SeekBar scaleBar;
    @BindView(R.id.spacingBar) SeekBar spacingBar;

    //Preview button images
    @BindView(R.id.homeButton) ImageView homeButton;
    @BindView(R.id.backButton) ImageView backButton;
    @BindView(R.id.recentsButton) ImageView recentsButton;
    @BindView(R.id.card2) CardView navbarPreviewCard;

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

    //color chooser
    @BindView(R.id.colorChooser) Button colorChooser;

    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    public Activity activity;

    public ImageView[] guides;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Utils.isFirstRun(this)) {
            Intent intent = new Intent(this, IntroActivity.class); //call Intro class
            startActivity(intent);
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        guides = new ImageView[]{homeLeftGuide, homeRightGuide, backLeftGuide, backRightGuide,
                recentsLeftGuide, recentsRightGuide};
        activity = this;
        initialize();
    }

    /**
     * Initialize everything for the activity
     */
    private void initialize() {
        updateColor();
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
                Utils.setSpacing(getApplicationContext(), progress, homeButton, 1);
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
                //Utils.setScale(getApplicationContext(), progress, backButton, homeButton, recentsButton, 1);
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
        colorChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorChooser();
            }
        });

        serviceToggle.setChecked(Utils.isEnabled(activity));
        spacingBar.setProgress(Utils.getSpacing(activity));
        scaleBar.setProgress(Utils.getScale(activity));
        showGuideCheck.setChecked(Utils.showGuides(this));
    }

    /**
     * Inflate the overflow menu in the actionbar
     * @param menu the menu
     * @return inflated
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Handle the overflow menu in the actionbar
     * @param item the selected item
     * @return super call
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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
        //homeLeftGuide.setX(homeButton.getX() + ((float) Utils.convertDpToPx(getApplicationContext(), 16)));
        homeLeftGuide.setX((homeButton.getX() + homeButton.getWidth()/2) - (float) Utils.convertDpToPx(getApplicationContext(), 16)/2);
        homeRightGuide.setX((homeButton.getX() + homeButton.getWidth()/2) + (float) Utils.convertDpToPx(getApplicationContext(), 16)/2);

        backLeftGuide.setX((backButton.getX() + backButton.getWidth()/2)- (float) Utils.convertDpToPx(getApplicationContext(), 16)/2);
        backRightGuide.setX((backButton.getX() + backButton.getWidth()/2) + (float) Utils.convertDpToPx(getApplicationContext(), 16)/2);

        recentsLeftGuide.setX((recentsButton.getX() + recentsButton.getWidth()/2) - (float) Utils.convertDpToPx(getApplicationContext(), 16)/2);
        recentsRightGuide.setX((recentsButton.getX() + recentsButton.getWidth()/2) +  (float) Utils.convertDpToPx(getApplicationContext(), 16)/2);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        float offset = size.y - (navbarPreviewCard.getY() + navbarPreviewCard.getHeight());
        Utils.setGuideHeights(this, offset, guides);
    }

    private void showColorChooser() {
        // Pass AppCompatActivity which implements ColorCallback, along with the title of the dialog
        new ColorChooserDialog.Builder(this, R.string.color_palette)
                .doneButton(R.string.md_done_label)  // changes label of the done button
                .cancelButton(R.string.md_cancel_label)  // changes label of the cancel button
                .backButton(R.string.md_back_label)  // changes label of the back button
                .dynamicButtonColor(true)  // defaults to true, false will disable changing action buttons' color to currently selected color
                .show();
    }
    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        Utils.saveValue(this, "color", selectedColor);
        updateColor();
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {

    }

    private void updateColor() {
        int color = Utils.getColor(this);
        Utils.setColor(color, backButton, homeButton, recentsButton);
        updateColorChooserLook(color);
    }

    private void updateColorChooserLook(@ColorInt int color) {
        String hex = String.format("#%06X", (0xFFFFFF & color));
        //System.out.println("HEX IS " + hex);
        if (hex.startsWith("#000")) {
            colorChooser.setTextColor(Color.BLACK);
        } else {
            colorChooser.setTextColor(color);
        }
        colorChooser.setText(hex);
    }
}

