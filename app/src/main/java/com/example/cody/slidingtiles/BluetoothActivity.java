package com.example.cody.slidingtiles;

import android.Manifest;
import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.ToggleButton;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;


public class BluetoothActivity extends AppCompatActivity{
    private static final String TAG = "BluetoothActivity";

    ToggleButton toggleButton;
    Spinner spinner;
    Button btnEnableDisable_Discoverable;
    //Button btnStartConnection;
    //Button btnSend;
    //BluetoothConnectionService mBluetoothConnection;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBTDevice;
    public ArrayList<BluetoothDevice> mNewBTDevices = new ArrayList<>();
    public ArrayList<BluetoothDevice> mBondedBTDevices = new ArrayList<>();
    public DeviceListAdapter mNewDeviceListAdapter;
    public DeviceListAdapter mBondedListAdapter;
    private ArrayAdapter lvAdapter;
    private ArrayAdapter BondedAdapter;
    ListView lvNewDevices;
    ListView bondedDevices;
    private int numberOfRounds;
    private String gameMode;
    private String oppTempName;
    private static final String BASIC_MODE = "BSC";
    private static final String CUTTHROAT_MODE = "CUT";
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
/*                if(!mBTDevices.contains(device)) {
                    mBTDevices.add(device);
                }
 */
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress() + " :: " +device.getBondState());

                if(device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "onReceive: new device..");
                    if(!mNewBTDevices.contains(device)) {
                        mNewBTDevices.add(device);
                    }
                    mNewDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mNewBTDevices);
                    lvNewDevices.setAdapter(mNewDeviceListAdapter);
                }
                if(device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "onReceive: bonded dev");
                    if(!mBondedBTDevices.contains(device)) {
                        mBondedBTDevices.add(device);
                    }
                    mBondedListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBondedBTDevices);
                    bondedDevices.setAdapter(mBondedListAdapter);
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
        mBondedBTDevices = new ArrayList<>();
        mNewBTDevices = new ArrayList<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //set our name here
        Log.d (TAG, "our device name is..: " + mBluetoothAdapter.getName());
        if(((BaseApp)this.getApplicationContext()).playerName.compareTo("Player 1") != 0) {
            mBluetoothAdapter.setName(((BaseApp) this.getApplicationContext()).playerName);
        }
        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        //Spinner for the number of rounds to play
        spinner = (Spinner) findViewById(R.id.spinner);
        final ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.roundsToPlay, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String roundSelection = (String) spinnerAdapter.getItem(position);
                Log.d(TAG, "onDropClick: You Clicked on = " + roundSelection);
                numberOfRounds = Integer.valueOf(roundSelection);

                Log.d(TAG, "onDropClick: value" + numberOfRounds);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // default game mode
        gameMode = BASIC_MODE;
        //Toggle button for the different game modes: CutThroat or Basic
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled: Basic Mode
                    Log.d(TAG, "Toggle : value" + gameMode);
                    gameMode = BASIC_MODE;
                } else {
                    // The toggle is disabled: CutThroat Mode
                    Log.d(TAG, "Toggle : value" + gameMode);
                    gameMode = CUTTHROAT_MODE;
                }
            }
        });

        // listview adapters for bonded and new devices
        lvAdapter = new ArrayAdapter<BluetoothDevice>(this, R.layout.device_adapter_view,mNewBTDevices);
        lvNewDevices.setAdapter(lvAdapter);
        lvNewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //first cancel discovery because its very memory intensive.
                mBluetoothAdapter.cancelDiscovery();

                Log.d(TAG, "onItemClick: You Clicked on a device.");
                String deviceName = mNewBTDevices.get(position).getName();
                String deviceAddress = mNewBTDevices.get(position).getAddress();

                Log.d(TAG, "onItemClick: deviceName = " + deviceName);
                Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

                //create the bond.
                //NOTE: Requires API 17+? I think this is JellyBean
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                    Log.d(TAG, "Trying to pair with " + deviceName);
                    if(mNewBTDevices.get(position).getBondState() == BluetoothDevice.BOND_NONE) {
                        mNewBTDevices.get(position).createBond();
                    }
                    mBTDevice = mNewBTDevices.get(position);

                    //remove from unpaired. add to paired
                    mNewBTDevices.remove(mBTDevice);
                    mBondedBTDevices.add(mBTDevice);
                    mBondedListAdapter = new DeviceListAdapter(BluetoothActivity.this, R.layout.device_adapter_view, mBondedBTDevices);
                    bondedDevices.setAdapter(mBondedListAdapter);
                    //mBluetoothConnection = new BluetoothConnectionService(BluetoothActivity.this);
                    //selected a device to connect. so why not?
                    if(mBTDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                        startConnection();
                    }
                }
            }
        });

        BondedAdapter = new ArrayAdapter<BluetoothDevice>(this, R.layout.device_adapter_view,mBondedBTDevices);
        bondedDevices.setAdapter(BondedAdapter);
        bondedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //first cancel discovery because its very memory intensive.
                mBluetoothAdapter.cancelDiscovery();

                Log.d(TAG, "onItemClick: You Clicked on a device.");
                String deviceName = mBondedBTDevices.get(position).getName();
                String deviceAddress = mBondedBTDevices.get(position).getAddress();

                Log.d(TAG, "onItemClick: deviceName = " + deviceName);
                Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

                //create the bond.
                //NOTE: Requires API 17+? I think this is JellyBean
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                    Log.d(TAG, "Trying to pair with " + deviceName);
                    if(mBondedBTDevices.get(position).getBondState() == BluetoothDevice.BOND_NONE) {
                        mBondedBTDevices.get(position).createBond();
                    }
                    mBTDevice = mBondedBTDevices.get(position);
                    //mBluetoothConnection = new BluetoothConnectionService(BluetoothActivity.this);
                    //selected a device to connect. so why not?
                    if(mBTDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                        startConnection();
                    }
                }
            }
        });
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
       // refreshViews();
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

/*
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
    */
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
        int[][] sharedBoard = mBoardGenerator.generateMathModeBoard();
        mBoardGenerator.shuffleBoard(sharedBoard);
        String sharedBoardAsString = mBoardGenerator.boardToString(sharedBoard);
//        String sharedBoardAsString = mBoardGenerator.boardToString(mBoardGenerator.generateMathModeBoard());
        ((BaseApp) this.getApplicationContext()).opponentName = mBTDevice.getName();
        oppTempName = ((BaseApp) this.getApplicationContext()).opponentName;
        Log.d(TAG, "new activity: " +sharedBoardAsString);
        //sharedBoard = mBoardGenerator.mathModeBoardFromString(sharedBoardAsString);
        boolean connectStatus = ((BaseApp) this.getApplicationContext()).myBtConnection.getState();
        if (connectStatus) {
            Log.d(TAG, "new activity: connected " );
            try {
                String gameStart = "Game Start";
                gameStart += numberOfRounds;
                gameStart += gameMode;
                gameStart += sharedBoardAsString;
                gameStart += mBluetoothAdapter.getName();
                Log.d(TAG, "new Activity: write out all: " +gameStart);
                byte [] bytes =  gameStart.getBytes(Charset.defaultCharset());
                ((BaseApp) this.getApplicationContext()).myBtConnection.write(bytes);
            }catch (Exception e){
                Log.d(TAG, "new activity: fail to send game over input stream");
            }
            Intent intent = new Intent(this, MathMode2Player.class);
            intent.putExtra("newGame", sharedBoardAsString);
            intent.putExtra("gameType",gameMode);
            intent.putExtra("rounds", numberOfRounds);
            intent.putExtra("oppName", oppTempName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
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
                if(text.contains("BSC")) {
                    gameMode ="BSC";
                }else{
                    gameMode ="CUT";
                }
                numberOfRounds = Integer.valueOf(text.substring(10,11));
                String newBoard = text.substring(14,72);
                oppTempName = text.substring(73);
                Intent start2Player= new Intent(context, MathMode2Player.class);
                start2Player.putExtra("newGame",newBoard);
                start2Player.putExtra("gameType",gameMode);
                start2Player.putExtra("rounds", numberOfRounds);
                start2Player.putExtra("oppName", oppTempName);
             //   Log.d(TAG, "ReceiverSyncOpen: Intent values: " +numberOfRounds +"|" + gameMode + "|" + newBoard);
                start2Player.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                start2Player.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                start2Player.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivity(start2Player);
            }
        }
    };
/*
    protected void refreshViews() {
        try {
            lvNewDevices.setAdapter(null);
            bondedDevices.setAdapter(null);
        } catch (Exception e) {
            Log.e(TAG, "refreshViews: failed.");
        }
        if(mBondedBTDevices.size() >0){
            for(BluetoothDevice device: mBondedBTDevices) {
                Log.d(TAG, "refreshViews: bonded");
                mBondedListAdapter = new DeviceListAdapter(this, R.layout.device_adapter_view, mBondedBTDevices);
                bondedDevices.setAdapter(mBondedListAdapter);
            }
        }
        if(mBTDevices.size() >0) {
            for(BluetoothDevice device: mBTDevices) {
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "refreshViews: bonded");
                    mDeviceListAdapter = new DeviceListAdapter(this, R.layout.device_adapter_view, mBTDevices);
                    bondedDevices.setAdapter(mDeviceListAdapter);
                }
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "refreshViews: not bonded");
                    mDeviceListAdapter = new DeviceListAdapter(this, R.layout.device_adapter_view, mBTDevices);
                    lvNewDevices.setAdapter(mDeviceListAdapter);
                }
            }

        }
    }
  */
}
