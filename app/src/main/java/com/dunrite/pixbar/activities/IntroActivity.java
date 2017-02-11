package com.dunrite.pixbar.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.dunrite.pixbar.R;
import com.dunrite.pixbar.Utils;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Activity that is used the first time a user opens the app
 */
public class IntroActivity extends AppIntro2 {
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("Welcome!",
                "With this app, you can make your navigation buttons look like the ones on the Pixel!", R.drawable.intro,
                ContextCompat.getColor(this, R.color.colorAccent)));

       if(!Utils.hasDrawPermission(this)){
                addSlide(AppIntroFragment.newInstance("Please Allow Permissions",
                        "In order to function, you need to allow Pixbar to draw over apps",
                        R.drawable.check,
                        ContextCompat.getColor(this, R.color.colorAccent)));
        }

       // barColor(Color.parseColor("#3F51B5"));
        setIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimary),
                Color.parseColor("#3F51B5"));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        if (!Utils.hasDrawPermission(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        } else {
            Utils.setFirst(this, false);
            Intent intent = new Intent(this, MainActivity.class); //call Intro class
            startActivity(intent);
        }
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}