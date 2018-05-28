package iaik.bacc.camilla.androidcredentialstore.activities;

import android.app.Fragment;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.ParcelUuid;
import java.util.UUID;

/**
 * This class represents the implementation of a Service for the Gatt Service of BLE.
 * Here the UUID is defined and the Gatt service and characteristics are set
 */

public class BluetoothLeService extends Fragment
{
    private static final String TAG = "BluetoothLeService";

//    public abstract BluetoothGattService getBluetoothGattService();
//    public abstract ParcelUuid getServiceUUID();

    //for new BLGattService
    private static final UUID CUSTOM_SERVICE_UUID = UUID
            .fromString("0000a000-0000-1000-8000-00805f9b34fb");

    //for new Characteristic
    private static final UUID USERNAME_UUID = UUID
            .fromString("0000a001-0000-1000-8000-00805f9b34fb");
    private static final UUID PASSWORD_UUID = UUID
            .fromString("0000a002-0000-1000-8000-00805f9b34fb");


    // GATT ***************************************
    private BluetoothGattService mCustomService;
    private BluetoothGattCharacteristic mUsernameCharacteristic;
    private BluetoothGattCharacteristic mPasswordCharacteristic;

    //Constructor of BLEService
    //TODO hand over parameters of username and password to create the BLEService?
    public BluetoothLeService()
    {
        mUsernameCharacteristic = new BluetoothGattCharacteristic(USERNAME_UUID,
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);

        mPasswordCharacteristic = new BluetoothGattCharacteristic(PASSWORD_UUID,
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);

        mUsernameCharacteristic.addDescriptor(PeripheralActivity.
                getClientCharacteristicConfigurationDescriptor());
        mPasswordCharacteristic.addDescriptor(PeripheralActivity.
                getClientCharacteristicConfigurationDescriptor());

        //not needed, former description of battery level
//        mUsernameCharacteristic.addDescriptor(PeripheralActivity.
//              getCharacteristicUserDescriptionDescriptor(DESCRIPTION));


        mCustomService = new BluetoothGattService(CUSTOM_SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        //adding 2 characteristics: username and password
        mCustomService.addCharacteristic(mPasswordCharacteristic);
        mCustomService.addCharacteristic(mUsernameCharacteristic);

    }

    public BluetoothGattService getBluetoothGattService() {
        return mCustomService;
    }

    public ParcelUuid getServiceUUID() {
        return new ParcelUuid(CUSTOM_SERVICE_UUID);
    }

    public void setCharacteristics(byte[] username, byte[] password)
    {
        mUsernameCharacteristic.setValue(username);
        mPasswordCharacteristic.setValue(password);

    }


    //***************************************************
    //methods of serviceFragement.java of example:
    public int writeCharacteristic(BluetoothGattCharacteristic characteristic,
                                   int offset, byte[] value)
    {
        throw new UnsupportedOperationException("Method writeCharacteristic not overridden");
    }

    public void notificationsDisabled(BluetoothGattCharacteristic characteristic)
    {
        throw new UnsupportedOperationException("Method notificationsDisabled not overridden");
    }

    public void notificationsEnabled(BluetoothGattCharacteristic characteristic, boolean indicate)
    {
        throw new UnsupportedOperationException("Method notificationsEnabled not overridden");
    }

}

