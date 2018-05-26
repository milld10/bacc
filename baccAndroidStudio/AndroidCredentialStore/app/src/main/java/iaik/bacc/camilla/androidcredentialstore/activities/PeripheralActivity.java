package iaik.bacc.camilla.androidcredentialstore.activities;

import android.app.Fragment;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import iaik.bacc.camilla.androidcredentialstore.CredentialApplication;
import iaik.bacc.camilla.androidcredentialstore.R;
import iaik.bacc.camilla.androidcredentialstore.database.DBHelper;
import iaik.bacc.camilla.androidcredentialstore.models.Account;


/**
 * Created by Camilla on 14.03.2018.
 * In this activity all available accounts are shown and at the same time this class
 * handles all BLE gatt services etc.
 * This activity needs to be shown before user interaction pressing the button on the website
 * As this activity is open, it keeps advertising the custom service the website needs to find
 */

public class PeripheralActivity extends ListActivity
{
    private static final String TAG = "PeripheralActivity";

    private static final int REQUEST_ENABLE_BT = 1;

    Toolbar mToolbar;
    TextView bluetooth_notification;
    Button bluetooth_button;
    ArrayList<Account> accountArrayList = new ArrayList<>();
    DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());

    //GATT ****************
    //Descriptors
    private static final UUID CHARACTERISTIC_USER_DESCRIPTION_UUID = UUID
            .fromString("00002901-0000-1000-8000-00805f9b34fb");
    private static final UUID CLIENT_CHARACTERISTIC_CONFIGURATION_UUID = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BluetoothLeService mBleCustomServiceFragment;

    private TextView mAdvStatus;

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    private BluetoothGattCharacteristic mNotifyCharacteristic;


    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private AdvertiseData mAdvData;
    private AdvertiseData mAdvScanResponse;
    private AdvertiseSettings mAdvSettings;
    private BluetoothLeAdvertiser mAdvertiser;
    private BluetoothGattService mBluetoothGattService;


    private String mBluetoothDeviceAddress;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;



    //***********************************************
    //Gatt from Peripheral.java from example
    private final AdvertiseCallback mAdvCallback = new AdvertiseCallback() {
        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.e(TAG, "Not broadcasting: " + errorCode);
            int statusText;
            switch (errorCode) {
                case ADVERTISE_FAILED_ALREADY_STARTED:
                    statusText = R.string.status_advertising;
                    Log.w(TAG, "App was already advertising");
                    break;
                case ADVERTISE_FAILED_DATA_TOO_LARGE:
                    statusText = R.string.status_advDataTooLarge;
                    break;
                case ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                    statusText = R.string.status_advFeatureUnsupported;
                    break;
                case ADVERTISE_FAILED_INTERNAL_ERROR:
                    statusText = R.string.status_advInternalError;
                    break;
                case ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                    statusText = R.string.status_advTooManyAdvertisers;
                    break;
                default:
                    statusText = R.string.status_notAdvertising;
                    Log.wtf(TAG, "Unhandled error: " + errorCode);
            }
            mAdvStatus.setText(statusText);
        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.v(TAG, "Broadcasting");
            mAdvStatus.setText(R.string.status_advertising);
        }
    };






    //Gatt server and server callback function:
    //TODO Handle and correct all function; are all functions needed?
    private BluetoothGattServer mGattServer;
    private final BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, final int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    //not needed because not showing how many devices are connected
//                    mBluetoothDevices.add(device);
//                    updateConnectedDevicesStatus();
                    Log.v(TAG, "Connected to device: " + device.getAddress());
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
//                    mBluetoothDevices.remove(device);
//                    updateConnectedDevicesStatus();
                    Log.v(TAG, "Disconnected from device");
                }
            } else {
//                mBluetoothDevices.remove(device);
//                updateConnectedDevicesStatus();
                // There are too many gatt errors (some of them not even in the documentation) so we just
                // show the error to the user.
                final String errorMessage = getString(R.string.status_errorWhenConnecting) + ": " + status;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PeripheralActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "Error when connecting: " + status);
            }
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
                                                BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            Log.d(TAG, "Device tried to read characteristic: " + characteristic.getUuid());
            Log.d(TAG, "Value: " + Arrays.toString(characteristic.getValue()));
            if (offset != 0) {
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_INVALID_OFFSET, offset,
            /* value (optional) */ null);
                return;
            }
            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS,
                    offset, characteristic.getValue());
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
            Log.v(TAG, "Notification sent. Status: " + status);
        }

        //TODO check if onCharacteristicWriteRequest is needed? neccessary to write to charac.?
        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
                                                 BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded,
                                                 int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite,
                    responseNeeded, offset, value);
            Log.v(TAG, "Characteristic Write request: " + Arrays.toString(value));
            int status = mBleCustomServiceFragment.writeCharacteristic(characteristic, offset, value);
            if (responseNeeded) {
                mGattServer.sendResponse(device, requestId, status,
            /* No need to respond with an offset */ 0,
            /* No need to respond with a value */ null);
            }
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId,
                                            int offset, BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
            Log.d(TAG, "Device tried to read descriptor: " + descriptor.getUuid());
            Log.d(TAG, "Value: " + Arrays.toString(descriptor.getValue()));
            if (offset != 0) {
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_INVALID_OFFSET, offset,
            /* value (optional) */ null);
                return;
            }
            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                    descriptor.getValue());
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId,
                                             BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded,
                                             int offset,
                                             byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded,
                    offset, value);
            Log.v(TAG, "Descriptor Write Request " + descriptor.getUuid() + " " + Arrays.toString(value));
            int status = BluetoothGatt.GATT_SUCCESS;
            if (descriptor.getUuid() == CLIENT_CHARACTERISTIC_CONFIGURATION_UUID) {
                BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
                boolean supportsNotifications = (characteristic.getProperties() &
                        BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0;
                boolean supportsIndications = (characteristic.getProperties() &
                        BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0;

                if (!(supportsNotifications || supportsIndications)) {
                    status = BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED;
                } else if (value.length != 2) {
                    status = BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH;
                } else if (Arrays.equals(value, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)) {
                    status = BluetoothGatt.GATT_SUCCESS;
                    mBleCustomServiceFragment.notificationsDisabled(characteristic);
                    descriptor.setValue(value);
                } else if (supportsNotifications &&
                        Arrays.equals(value, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)) {
                    status = BluetoothGatt.GATT_SUCCESS;
                    mBleCustomServiceFragment.notificationsEnabled(characteristic, false /* indicate */);
                    descriptor.setValue(value);
                } else if (supportsIndications &&
                        Arrays.equals(value, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)) {
                    status = BluetoothGatt.GATT_SUCCESS;
                    mBleCustomServiceFragment.notificationsEnabled(characteristic, true /* indicate */);
                    descriptor.setValue(value);
                } else {
                    status = BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED;
                }
            } else {
                status = BluetoothGatt.GATT_SUCCESS;
                descriptor.setValue(value);
            }
            if (responseNeeded) {
                mGattServer.sendResponse(device, requestId, status,
            /* No need to respond with offset */ 0,
            /* No need to respond with a value */ null);
            }
        }
    };










    // Code to manage Service lifecycle.
//    private final ServiceConnection mServiceConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder service) {
////            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
////            if (!mBluetoothLeService.initialize()) {
////                Log.e(TAG, "Unable to initialize Bluetooth");
////                finish();
////            }
////            // Automatically connects to the device upon successful start-up initialization.
////            mBluetoothLeService.connect(mDeviceAddress);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
////            mBluetoothLeService = null;
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_available_accounts);

        Log.d(TAG, "onCreate: is called.");

        accountArrayList = (ArrayList<Account>) dbHelper.getAvailableAccounts();

        mToolbar = (Toolbar) findViewById(R.id.toolbar_show_available_accounts);
        mToolbar.setTitle(R.string.available_accounts);

        bluetooth_notification = (TextView) findViewById(R.id.notification_bluetooth);
        bluetooth_button = (Button) findViewById(R.id.bluetooth_button_onoff);

        mAdvStatus = (TextView) findViewById(R.id.advertise_status);
        mAdvStatus.setText("Not yet advertising!");

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        final ArrayAdapter<Account> adapter = new ArrayAdapter<Account>(this,
                android.R.layout.simple_list_item_1,
                accountArrayList);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();


        //information textview is BT is on or off
        if(mBluetoothAdapter.isEnabled())
            bluetooth_notification.setText(R.string.hint_bluetoothON);
        else
            bluetooth_notification.setText(R.string.hint_bluetoothOFF);

        bluetooth_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: enabling/disabling bt");
//                enableDisableBT();
                ensureBleFeaturesAvailable();
            }
        });


        mBleCustomServiceFragment = new BluetoothLeService();

        mBluetoothGattService = mBleCustomServiceFragment.getBluetoothGattService();

        mAdvSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setConnectable(true)
                .build();
        mAdvData = new AdvertiseData.Builder()
                .setIncludeTxPowerLevel(true)
                .addServiceUuid(mBleCustomServiceFragment.getServiceUUID())
                .build();
        mAdvScanResponse = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .build();

    }


    @Override
    protected void onStart()
    {
        super.onStart();
//        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//        registerReceiver(mBroadcastReceiver1, intentFilter);

        // If the user disabled Bluetooth when the app was in the background,
        // openGattServer() will return null.
        mGattServer = mBluetoothManager.openGattServer(this, mGattServerCallback);
        if (mGattServer == null) {
            ensureBleFeaturesAvailable();
            //TODO not know if needed?? does enableDisableBT do the same than ensureBleFeatureAvailable()?
//            enableDisableBT();
            return;
        }

        // Add a service for a total of three services (Generic Attribute and Generic Access
        // are present by default).
        mGattServer.addService(mBluetoothGattService);

        if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {
            mAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
            mAdvertiser.startAdvertising(mAdvSettings, mAdvData, mAdvScanResponse, mAdvCallback);
        } else {
            mAdvStatus.setText(R.string.status_noLeAdv);
        }

    }


    //method called by on/off button listener
    public void enableDisableBT(){
        //3 cases
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

            mBluetoothAdapter.startDiscovery();

//            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//            registerReceiver(mBroadcastReceiver1, BTIntent);
        }

        //if BT is already enabled, then disable
        if (mBluetoothAdapter.isEnabled())
        {
            Log.d(TAG, "enableDisableBT: disabling BT");
            mBluetoothAdapter.disable();

//            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

    // Create a BroadcastReceiver for ACTION_STATE_CHANGED when BT gets turned on/off
    //TODO is a broadcastreceiver neccessary??
//    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver()
//    {
//        public void onReceive(Context context, Intent intent)
//        {
//            String action = intent.getAction();
//            if(action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED))
//            {
//                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
//                        mBluetoothAdapter.ERROR);
//
//                switch(state)
//                {
//                    case BluetoothAdapter.STATE_OFF:
//                        Log.d(TAG, "onReceive: STATE OFF");
//                        bluetooth_notification.setText(R.string.hint_bluetoothOFF);
//                        break;
//                    case BluetoothAdapter.STATE_TURNING_OFF:
//                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
//                        break;
//                    case BluetoothAdapter.STATE_ON:
//                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
//                        bluetooth_notification.setText(R.string.hint_bluetoothON);
//                        break;
//                    case BluetoothAdapter.STATE_TURNING_ON:
//                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
//                        break;
//                }
//            }
//        }
//    };


    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id)
    {
        super.onListItemClick(listView, view, position, id);
        mBluetoothAdapter.cancelDiscovery();

        //TODO is a list of accounts that are shown, and user has to select one to send
        //put the clicked item into the advertising data!!
    }

    @Override
    public void onResume()
    {
        super.onResume();
        accountArrayList = (ArrayList<Account>) dbHelper.getAvailableAccounts();

        ArrayAdapter<Account> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                accountArrayList);

        setListAdapter(adapter);


        if(mBluetoothAdapter.isEnabled())
            bluetooth_notification.setText(R.string.hint_bluetoothON);
        else
            bluetooth_notification.setText(R.string.hint_bluetoothOFF);


//        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
//        {
//            finish();
//        }
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
//        unregisterReceiver(mBroadcastReceiver1);
    }



    //*******************************
    // Functions for Custom Service (used by BluetoothLeService.java: not any more)

    ///////////////////////
    ////// Bluetooth //////
    ///////////////////////
    public static BluetoothGattDescriptor getClientCharacteristicConfigurationDescriptor() {
        BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(
                CLIENT_CHARACTERISTIC_CONFIGURATION_UUID,
                (BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE));
        descriptor.setValue(new byte[]{0, 0});
        return descriptor;
    }

    public static BluetoothGattDescriptor getCharacteristicUserDescriptionDescriptor(String defaultValue) {
        BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(
                CHARACTERISTIC_USER_DESCRIPTION_UUID,
                (BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE));
        try {
            descriptor.setValue(defaultValue.getBytes("UTF-8"));
        } finally {
            return descriptor;
        }
    }

    private void ensureBleFeaturesAvailable() {
        if (mBluetoothAdapter == null)
        {
            Log.e(TAG, "Bluetooth not supported");
            finish();
        } else if (!mBluetoothAdapter.isEnabled())
        {
            // Make sure bluetooth is enabled.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                if (!mBluetoothAdapter.isMultipleAdvertisementSupported()) {
                    Toast.makeText(this, R.string.bluetoothAdvertisingNotSupported, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Advertising not supported");
                }
                onStart();
            } else {
                //TODO(g-ortuno): UX for asking the user to activate bt
                Toast.makeText(this, R.string.bluetoothNotEnabled, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Bluetooth not enabled");
                finish();
            }
        }
    }

    private void disconnectFromDevices() {
        Log.d(TAG, "Disconnecting devices...");
        for (BluetoothDevice device : mBluetoothManager.getConnectedDevices(
                BluetoothGattServer.GATT)) {
            Log.d(TAG, "Devices: " + device.getAddress() + " " + device.getName());
            mGattServer.cancelConnection(device);
        }
    }

}
