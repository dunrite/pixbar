package com.dunrite.pixelfy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;


/**
 * The buttons that floats above the navbar
 */
public class NavbarService extends Service {

    private final static int FOREGROUND_ID = 1000;
    private ButtonLayer buttonLayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //logServiceStarted();
        initButtonLayer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //logServiceEnded();
        stopForeground(true);
        destroyButtonLayer();
    }

    private void initButtonLayer() {
        buttonLayer = new ButtonLayer(this);
    }

    private void destroyButtonLayer() {
        buttonLayer.destroy();
        buttonLayer = null;
    }

    private void logServiceStarted() {
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
    }

    private void logServiceEnded() {
        Toast.makeText(this, "Service ended", Toast.LENGTH_SHORT).show();
    }

}
