package com.dunrite.pixbar.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.dunrite.pixbar.R;

/**
 * About activity
 */
public class AboutActivity extends AppCompatActivity {
    //@BindView(R.id.debugtext) TextView debugText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
      //  ButterKnife.bind(this);
        getSupportActionBar().setTitle("About");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String versionName = "";
        PackageInfo packageInfo;
        //Inserts the correct version number of the app
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView tv = (TextView) findViewById(R.id.versionNum);
        tv.setText(versionName);

//        debugText.setText("DEVICE: " + Build.DEVICE +
//                          "\nMANUFACTURER: " + Build.MANUFACTURER +
//                          "\nBOARD: " + Build.BOARD +
//                          "\nBRAND: " + Build.BRAND +
//                          "\nMODEL: " + Build.MODEL +
//                          "\nPRODUCT: " + Build.PRODUCT);
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