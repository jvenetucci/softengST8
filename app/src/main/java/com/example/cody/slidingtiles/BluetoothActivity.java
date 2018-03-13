package com.example.cody.slidingtiles;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;


public class BluetoothActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private static final String TAG = "BluetoothActivity";

    Button btnEnableDisable_Discoverable;
    //Button btnStartConnection;
    //Button btnSend;
    //BluetoothConnectionService mBluetoothConnection;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBTDevice;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
//    public ArrayList<BluetoothDevice> mBTDevicesPaired = new ArrayList<>();
//    public ArrayList<BluetoothDevice> mBTDevicesUnpaired = new ArrayList<>();


    public DeviceListAdapter mDeviceListAdapter;

    ListView lvNewDevices;
    ListView bondedDevices;

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    // Create a BroadcastReceiver for ACTION_STATE_CHANGED
    // For ON/OFF button
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };

    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                if(!mBTDevices.contains(device)) {
                    mBTDevices.add(device);
                }

                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress() + " :: " +device.getBondState());
                if(device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "onReceive: bonded dev");
                    mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                    bondedDevices.setAdapter(mDeviceListAdapter);
                }
                if(device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "onReceive: new device..");
                    mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                    lvNewDevices.setAdapter(mDeviceListAdapter);
                }

            }
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    //inside BroadcastReceiver4
                    mBTDevice = mDevice;
                    //refreshViews();
                    startConnection();
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        //Button btnONOFF = (Button) findViewById(R.id.btnONOFF);
        btnEnableDisable_Discoverable = (Button) findViewById(R.id.btnDiscoverable_on_off);
        //btnStartConnection = (Button) findViewById(R.id.btnStartConnection);
        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        bondedDevices = (ListView) findViewById(R.id.bondedDevices);
        mBTDevices = new ArrayList<>();
//        mBTDevicesPaired = new ArrayList<>();
//        mBTDevicesUnpaired = new ArrayList<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        lvNewDevices.setOnItemClickListener(BluetoothActivity.this);
        bondedDevices.setOnItemClickListener(BluetoothActivity.this);
/*
        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                enableDisableBT();
            }
        });
*/
/*
        btnStartConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startConnection();
            }
        });
*/
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiverSyncOpen, new IntentFilter("incomingMessage"));
    }
    //***remember the connection will fail and app will crash if you haven't paired first
    public void startConnection(){
        Log.d(TAG, "startConnection: checking for bond.");

        if (mBTDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            Log.d(TAG, "startConnection: BONDED.");
            startBTConnection(mBTDevice, MY_UUID_INSECURE);
        }
    }
    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        //mBluetoothConnection.startClient(device,uuid);
        ((BaseApp)this.getApplicationContext()).myBtConnection.startClient(device, uuid);
    }
    //ON OFF BUTTON
    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }



    }


    //ENABLE_DISCOVERY BUTTON
    public void btnEnableDisable_Discoverable(View view) {
        Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2,intentFilter);

    }


    //DISCOVER BUTTON
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void btnDiscover(View view) {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");
        //refreshViews();
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //first cancel discovery because its very memory intensive.
        mBluetoothAdapter.cancelDiscovery();
        Log.d(TAG, "onItemClick: You Clicked on a device.");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with " + deviceName);
            if(mBTDevices.get(i).getBondState() == BluetoothDevice.BOND_NONE) {
                mBTDevices.get(i).createBond();
            }
            mBTDevice = mBTDevices.get(i);
            //mBluetoothConnection = new BluetoothConnectionService(BluetoothActivity.this);
            //selected a device to connect. so why not?
            if(mBTDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                startConnection();
            }
        }
    }
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        try {
            unregisterReceiver(mBroadcastReceiver1);
        }catch (Exception e){
            Log.d(TAG, "onDestroy: no receiver1.");
        }
        try {
            unregisterReceiver(mBroadcastReceiver2);
        }catch (Exception e){
            Log.d(TAG, "onDestroy: no receiver2.");
        }
        try {
            unregisterReceiver(mBroadcastReceiver3);
        }catch (Exception e){
            Log.d(TAG, "onDestroy: no receiver3.");
        }
        try {
            unregisterReceiver(mBroadcastReceiver4);
        }catch (Exception e){
            Log.d(TAG, "onDestroy: no receiver4.");
        }
        try {
            unregisterReceiver(mReceiverSyncOpen);
        }catch (Exception e){
            Log.d(TAG, "onDestroy: no SyncOpen.");
        }

        try {
            ((BaseApp) this.getApplicationContext()).myBtConnection.closeConnection();
        }catch (Exception e){
            Log.d(TAG, "onDestroy: fail closing connection.");
        }
        //mBluetoothAdapter.cancelDiscovery();
    }


    /*
     * On button press, signal the other device that we are starting a game and send
     * the initializing board data so that the games start the same.
     *
     *
     */
    public void newActivity(View view) {
        BoardGenerator mBoardGenerator = new BoardGenerator();
        int[][] sharedBoard;
        String sharedBoardAsString = mBoardGenerator.boardToString(mBoardGenerator.generateMathModeBoard());
        Log.d(TAG, "new activity: " +sharedBoardAsString);
        sharedBoard = mBoardGenerator.mathModeBoardFromString(sharedBoardAsString);
        boolean connectStatus = ((BaseApp) this.getApplicationContext()).myBtConnection.getState();
        if (connectStatus) {
            Log.d(TAG, "new activity: connected " );
            try {
                String gameStart = "Game Start";
                gameStart += sharedBoardAsString;
                byte [] bytes =  gameStart.getBytes(Charset.defaultCharset());
                ((BaseApp) this.getApplicationContext()).myBtConnection.write(bytes);
            }catch (Exception e){
                Log.d(TAG, "new activity: fail to send game over input stream");
            }
            Intent intent = new Intent(this, MathMode2Player.class);
            intent.putExtra("newGame", sharedBoardAsString);
            startActivity(intent);
        } else {
            Log.d(TAG, "new activity: NOT connected " );
        }
    }

    //get input stream to signal opening the app at the same time
    BroadcastReceiver mReceiverSyncOpen = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String text = intent.getStringExtra("theMessage");
            Log.d(TAG, "reading input stream..  " + text);

            if (text.contains("Game Start")){
                String newBoard = text.substring(10);
                Intent start2Player= new Intent(context, MathMode2Player.class);
                start2Player.putExtra("newGame",newBoard);
                startActivity(start2Player);
            }
        }
    };

    protected void refreshViews() {
        try {
            lvNewDevices.setAdapter(null);
            bondedDevices.setAdapter(null);
        } catch (Exception e) {
            Log.e(TAG, "refreshViews: failed.");
        }
        if(mBTDevices.size() >0) {
            for(BluetoothDevice device: mBTDevices) {
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "refreshViews: bonded");
                    mDeviceListAdapter = new DeviceListAdapter(this, R.layout.device_adapter_view, mBTDevices);
                    bondedDevices.setAdapter(mDeviceListAdapter);
                }
                else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "refreshViews: not bonded");
                    mDeviceListAdapter = new DeviceListAdapter(this, R.layout.device_adapter_view, mBTDevices);
                    lvNewDevices.setAdapter(mDeviceListAdapter);
                }
            }

        }
    }
}
