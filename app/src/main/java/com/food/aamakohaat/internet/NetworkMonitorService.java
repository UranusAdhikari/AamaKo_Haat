package com.food.aamakohaat.internet;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.food.aamakohaat.internet.NetworkChangeReceiver;

public class NetworkMonitorService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Register the BroadcastReceiver here
        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, networkChangeReceiver.getIntentFilter());

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
