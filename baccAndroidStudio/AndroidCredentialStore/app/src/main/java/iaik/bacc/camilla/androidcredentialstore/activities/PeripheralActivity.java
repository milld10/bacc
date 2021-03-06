package iaik.bacc.camilla.androidcredentialstore.activities;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;

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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import iaik.bacc.camilla.androidcredentialstore.CredentialApplication;
import iaik.bacc.camilla.androidcredentialstore.R;
import iaik.bacc.camilla.androidcredentialstore.database.DBHelper;
import iaik.bacc.camilla.androidcredentialstore.models.Account;
import iaik.bacc.camilla.androidcredentialstore.tools.Converter;
import iaik.bacc.camilla.androidcredentialstore.tools.EncryptionHelper;


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
    public static final int AUTHENTICATION_SUCCESS_REQUEST_CODE = 35;
    private static final int REQUEST_ENABLE_BT = 1;

    Toolbar mToolbar;

    ArrayList<Account> accountArrayList = new ArrayList<>();
    DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());

    //Descriptors
    private static final UUID CHARACTERISTIC_USER_DESCRIPTION_UUID = UUID
            .fromString("00002901-0000-1000-8000-00805f9b34fb");
    private static final UUID CLIENT_CHARACTERISTIC_CONFIGURATION_UUID = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BluetoothLeService mBleCustomServiceFragment;

    private TextView mAdvStatus;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private AdvertiseData mAdvData;
    private AdvertiseData mAdvScanResponse;
    private AdvertiseSettings mAdvSettings;
    private BluetoothLeAdvertiser mAdvertiser;
    private BluetoothGattService mBluetoothGattService;
    private Account account;

    //***********************************************
    //Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_available_accounts);

        Log.d(TAG, "onCreate: is called.");

        accountArrayList = (ArrayList<Account>) dbHelper.getAllAccounts();

        mToolbar = (Toolbar) findViewById(R.id.toolbar_show_available_accounts);
        mToolbar.setTitle(R.string.available_accounts);

        mAdvStatus = (TextView) findViewById(R.id.advertise_status);
        mAdvStatus.setText(R.string.status_click_to_advertise);

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        final ArrayAdapter<Account> adapter = new ArrayAdapter<Account>(this,
                android.R.layout.simple_list_item_1,
                accountArrayList);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();

        mBleCustomServiceFragment = new BluetoothLeService();

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        //open gatt server again newly and enable BT if not already (onStart)
        mGattServer = mBluetoothManager.openGattServer(this, mGattServerCallback);
        Log.d(TAG, "...gatt server opened again!");
        if (mGattServer == null){
            ensureBleFeaturesAvailable();
        }
    }


    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id)
    {
        super.onListItemClick(listView, view, position, id);

        if (mGattServer != null) {
            mGattServer.close();
            Log.d(TAG, "...gatt server closed!");
        }
        if (mBluetoothAdapter.isEnabled() && mAdvertiser != null) {
            // If stopAdvertising() gets called before close() a null
            // pointer exception is raised.
            mAdvertiser.stopAdvertising(mAdvCallback);
            Log.d(TAG, "stop advertising...");
        }

        //open gatt server again newly and enable BT if not already (onStart)
        mGattServer = mBluetoothManager.openGattServer(this, mGattServerCallback);
        Log.d(TAG, "...gatt server opened again!");
        if (mGattServer == null){
            ensureBleFeaturesAvailable();
        }

        account = accountArrayList.get(position);

        Intent intent = new Intent(this, AdvertisingAuthenticationActivity.class);
        startActivityForResult(intent, AUTHENTICATION_SUCCESS_REQUEST_CODE);
    }


    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume() was called");

        accountArrayList = (ArrayList<Account>) dbHelper.getAllAccounts();

        ArrayAdapter<Account> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                accountArrayList);

        setListAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() was called");

        if (mGattServer != null) {
            mGattServer.close();
        }
        if (mBluetoothAdapter.isEnabled() && mAdvertiser != null) {
            // If stopAdvertising() gets called before close() a null
            // pointer exception is raised.
            mAdvertiser.stopAdvertising(mAdvCallback);
        }
        mAdvStatus.setText(R.string.status_notAdvertising);
    }


    //***********************************************

    private void startToAdvertiseCredentials()
    {
        /**
         * Method to start advertising only after the user clicked on a list item. otherwise
         * there is no data to be advertised. (was in onStart)
         */

        mGattServer.addService(mBluetoothGattService);

        if (mBluetoothAdapter.isMultipleAdvertisementSupported())
        {
            mAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
            mAdvertiser.startAdvertising(mAdvSettings, mAdvData, mAdvScanResponse, mAdvCallback);
            Log.d(TAG, "start advertising...");
        }
        else
        {
            mAdvStatus.setText(R.string.status_noLeAdv);
        }
    }


    //************************** Getting back from authentication handler:

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    if (!mBluetoothAdapter.isMultipleAdvertisementSupported()) {
                        Toast.makeText(this, R.string.bluetoothAdvertisingNotSupported, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Advertising not supported");
                    }
                    onStart();
                } else {
                    Toast.makeText(this, R.string.bluetoothNotEnabled, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Bluetooth not enabled");
                    finish();
                }
                break;
            case AUTHENTICATION_SUCCESS_REQUEST_CODE:
                if(resultCode == RESULT_OK)
                {
                    //** Encryption of data before advertising it.
                    byte[] decrypted_username;
                    byte[] decrypted_password;

                    try {
                        final EncryptionHelper encryptionHelper =
                                new EncryptionHelper(CredentialApplication.getInstance());
                        Log.d(TAG, "new encryptionHelper object has been generated (within try/catch)");

                        //Decryption of data retrieved from DB
                        decrypted_username = encryptionHelper.decrypt(account.getUsername());
                        decrypted_password = encryptionHelper.decrypt(account.getPassword());

                        //setValue of Characteristics:
                        mBleCustomServiceFragment.putCredentialsAsCharacteristics(decrypted_username, decrypted_password);

                        Log.d(TAG, "Decrypted data (after setting values): username: " +
                                Converter.byteToString(decrypted_username) +
                                " | password: " +
                                Converter.byteToString(decrypted_password));

                    } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException |
                            NoSuchProviderException | InvalidAlgorithmParameterException | BadPaddingException |
                            NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                            UnrecoverableEntryException e) {
                        e.printStackTrace();
                    }


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

                    startToAdvertiseCredentials();
                }
                break;
        }
    }


    //***********************************************
    //GATT
    private final AdvertiseCallback mAdvCallback = new AdvertiseCallback() {
        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.e(TAG, "Not broadcasting: " + errorCode);
            int statusText;
            switch (errorCode) {
                case ADVERTISE_FAILED_ALREADY_STARTED:
                    //error code 3:
                    statusText = R.string.status_advertising;
                    Log.w(TAG, "App was already advertising");
                    break;
                case ADVERTISE_FAILED_DATA_TOO_LARGE:
                    //error code 1:
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
                    Log.d(TAG, "Connected to device: " + device.getAddress());
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
//                    mBluetoothDevices.remove(device);
//                    updateConnectedDevicesStatus();
                    Log.d(TAG, "Disconnected from device");
                }
            } else {
//                mBluetoothDevices.remove(device);
//                updateConnectedDevicesStatus();
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


    //***********************************************
    // Bluetooth

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
        if (mBluetoothAdapter == null){
            Log.e(TAG, "Bluetooth not supported");
            finish();
        }
        else if (!mBluetoothAdapter.isEnabled()) {
            // Make sure bluetooth is enabled.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
}
