package com.dunrite.pixbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast Reciever that runs on boot
 */

public class OnBootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, NavbarService.class);
        if (Utils.isEnabledOnBoot(context))
            context.startService(startServiceIntent);
    }
}
