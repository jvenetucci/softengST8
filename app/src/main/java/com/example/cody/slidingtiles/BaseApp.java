package com.example.cody.slidingtiles;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import java.util.UUID;

/**
 * Creates a container so that we can maintain our bluetooth connection across activities.
 * So we can establish our connection, then fire up MathMode2Players
 */

public class BaseApp extends Application{

    private static final String TAG = "BaseApp";
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    BluetoothAdapter mBluetoothAdapter;
    public BluetoothConnectionService myBtConnection;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "in APP: OnCreate");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        myBtConnection = new BluetoothConnectionService(this);
    }
}
