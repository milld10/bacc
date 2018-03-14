package iaik.bacc.camilla.androidcredentialstore.activities;

import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;

import iaik.bacc.camilla.androidcredentialstore.tools.CheckingTools;

/**
 * Created by Camilla on 14.03.2018.
 */

public class ScannerBLE {

    private MainActivity mMainActivity;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mIsScanning;
    private Handler mHandler;

    private long mScanPeriod;
    private int mSignalStrength;

    public ScannerBLE(MainActivity mainActivity, long scanPeriod, int signalStrength)
    {
        mMainActivity = mainActivity;

        mHandler = new Handler();

        this.mScanPeriod = scanPeriod;
        this.mSignalStrength = signalStrength;

        final BluetoothManager bluetoothManager =
                (BluetoothManager) mMainActivity.getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();

    }


    public boolean isScanning()
    {
        return mIsScanning;
    }


    public void start() {
        if(!CheckingTools.checkBluetooth(mBluetoothAdapter))
        {
            CheckingTools.requestUserBluetooth(mMainActivity);
//            mMainActivity.stopScan();

        }
    }

}
