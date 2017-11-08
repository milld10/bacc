package iaik.bacc.camilla.androidcredentialstore.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.Manifest;

import iaik.bacc.camilla.androidcredentialstore.R;
import iaik.bacc.camilla.androidcredentialstore.models.DeviceListAdapter;

import java.util.ArrayList;


public class BluetoothActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    private static final String TAG = "BluetoothActivity";

    BluetoothAdapter mBluetoothAdapter;

    TextView bluetooth_notification;
    Button bluetooth_button;
    Button button_discoverableOnOff;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter deviceListAdapter;
    ListView lvNewDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        Log.d(TAG, "onCreate: is called.");

        bluetooth_notification = (TextView) findViewById(R.id.notification_bluetooth);
        bluetooth_button = (Button) findViewById(R.id.bluetooth_button);
        button_discoverableOnOff = (Button) findViewById(R.id.button_discoverableOnOff);

        //a bluetooth adapter object is initialised
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();

        lvNewDevices.setOnItemClickListener(BluetoothActivity.this);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);


        if(mBluetoothAdapter.isEnabled())
            bluetooth_notification.setText(R.string.hint_bluetoothON);
        else
            bluetooth_notification.setText(R.string.hint_bluetoothOFF);


        bluetooth_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: enabling/disabling bt");
                enableDisableBT();
            }
        });

    }


    @Override
    protected void onStart(){

        super.onStart();

        Log.d(TAG, "onStart: is called");

        //registered the receiver again to prevent from crashing
        //IntentFilter for enabling/disabling bluetooth
        IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver1, BTIntent);

        //intentFilter for Discoverability
        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2, intentFilter);

    }


    //method called by on/off button listener
    public void enableDisableBT(){
        //3 szenarios
        // if adapter is null the device is not capable of BT
        if(mBluetoothAdapter == null)
        {
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities");
        }

        //if BT is not enabled, then start intent to enable it
        if (!mBluetoothAdapter.isEnabled())
        {
            Log.d(TAG, "enableDisableBT: enabling BT");

            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }

        //if BT is already enabled, then disable
        if (mBluetoothAdapter.isEnabled())
        {
            Log.d(TAG, "enableDisableBT: disabling BT");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

    // Create a BroadcastReceiver for ACTION_STATE_CHANGED when BT gets turned on/off
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        bluetooth_notification.setText(R.string.hint_bluetoothOFF);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        bluetooth_notification.setText(R.string.hint_bluetoothON);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };





    public void btnEnableDisableDiscoverable(View view) {
        Log.d(TAG, "btnEnableDisable_Discoverable: making device discoverable for 300 seconds");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2, intentFilter);
    }


    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED))
            {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch(mode){
                    //Device is in discoverable mode
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
                        Log.d(TAG, "mBroadcastReceiver2: Connecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }
            }
        }
    };



    public void buttonDiscover(View view) {
        Log.d(TAG, "buttonDiscover: Looking for unpaired devices.");

        //if it is discovering when button is clicked, then stop and start again
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "buttonDiscover: Canceling discovery.");

            //check BT permissions in manifest
            //checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);

        }
        //if its not discovering then start discovery
        if(!mBluetoothAdapter.isDiscovering())
        {
            //check BT permissions in manifest
            //checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }



    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * is executed by buttonDiscover() method.
     */

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            //the ACTION_FOUND is passed with the button in the intent filter
            if(action.equals(BluetoothDevice.ACTION_FOUND))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                deviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(deviceListAdapter);
            }
        }
    };

    /**
     * identifies bond state changes e.g. when BT pairing status changes
     */
    private BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            Log.d(TAG, "onBonding: ACTION FOUND.");

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
            {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //there are 3 different cases for intents:
                //case1: BT device is already bonded
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDED)
                {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");

                }
                //case2: BT device is now creating a bond with other device
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDING)
                {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");

                }
                //case3: BT device is not bonded; bond broken
                if(mDevice.getBondState() == BluetoothDevice.BOND_NONE)
                {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");

                }
            }
        }
    };

    /*
    @Override
    protected void onStop()
    {
        super.onStop();

        Log.d(TAG, "onStop: called.");
        //super.onDestroy();
        //unregister the ACTION_FOUND receiver, broadcastreceiver gets destroyed when the application is closed
        unregisterReceiver(mBroadcastReceiver1);
    }*/

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy: is called.");
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);

    }


    /**
     * This method is required for all devices running API23+
     * Android must programmitically check the permissions for bluetooth. Putting the proper
     * permissions in the manifest is not enough.
     *
     * It will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if(permissionCheck != 0)
            {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
            else
            {
                Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        //canceling discoverability, memory intensive
        mBluetoothAdapter.cancelDiscovery();

        //getting the device from list which you clicked on
        Log.d(TAG, "onItemClick: you clicked on a device.");
        String deviceName = mBTDevices.get(position).getName();
        String deviceAddress = mBTDevices.get(position).getAddress();
        Log.d(TAG, "onItemClick: deviceName: " + deviceName + ", deviceAddress: " + deviceAddress);

        //creating the bond of my device and the clicked device from list
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            Log.d(TAG, "Trying to pair with " + deviceName);
            mBTDevices.get(position).createBond();
        }

    }
}
