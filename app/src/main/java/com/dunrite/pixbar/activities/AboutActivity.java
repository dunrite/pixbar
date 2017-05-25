package com.dunrite.pixbar.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.dunrite.pixbar.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * About activity
 */
public class AboutActivity extends AppCompatActivity {
    //@BindView(R.id.debugtext) TextView debugText;
    @BindView(R.id.emailButton) Button emailButton;
    @BindView(R.id.githubButton) Button githubButton;
    @BindView(R.id.versionNum) TextView versionNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("About");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String versionName = "";
        PackageInfo packageInfo;
        //Inserts the correct version number of the app
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = "v" + packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionNum.setText(versionName);

//        debugText.setText("DEVICE: " + Build.DEVICE +
//                          "\nMANUFACTURER: " + Build.MANUFACTURER +
//                          "\nBOARD: " + Build.BOARD +
//                          "\nBRAND: " + Build.BRAND +
//                          "\nMODEL: " + Build.MODEL +
//                          "\nPRODUCT: " + Build.PRODUCT);
    }

    @OnClick(R.id.emailButton)
    public void startEmail() {
        ShareCompat.IntentBuilder.from(this)
                .setType("message/rfc822")
                .addEmailTo("support@dunriteapps.com")
                .setSubject("Pixbar")
                .setText("Describe your issue here...")
                .setChooserTitle("Send email...")
                .startChooser();
    }

    @OnClick(R.id.githubButton)
    public void openGithub() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://github.com/dunrite/pixbar/issues"));
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}