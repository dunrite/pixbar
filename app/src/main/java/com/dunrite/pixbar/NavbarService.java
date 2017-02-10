package com.dunrite.pixbar;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;


/**
 * The buttons that floats above the navbar
 */
public class NavbarService extends Service {

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
        super.onDestroy();
        stopForeground(true);
        destroyButtonLayer();
    }

    /**
     * Initialize the buttons
     */
    private void initButtonLayer() {
        if (buttonLayer != null) {
            destroyButtonLayer();
        }
        buttonLayer = new ButtonLayer(this);
    }

    /**
     * Destroy the buttons
     */
    private void destroyButtonLayer() {
        buttonLayer.destroy();
        buttonLayer = null;
    }

    /**
     * For debugging. Log that the service started
     */
    private void logServiceStarted() {
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
    }

    /**
     * For debugging. Log that the service ended
     */
    private void logServiceEnded() {
        Toast.makeText(this, "Service ended", Toast.LENGTH_SHORT).show();
    }

}
