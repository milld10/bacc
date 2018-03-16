package iaik.bacc.camilla.androidcredentialstore.activities;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import iaik.bacc.camilla.androidcredentialstore.R;

/**
 * Created by Camilla on 14.03.2018.
 */

public class ScannerBLE extends ListActivity
{
    private static final String TAG = "ScannerLE";

    private BluetoothAdapter mBluetoothAdapter;

    TextView bluetooth_notification;
    Button bluetooth_button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_available_accounts);

        Log.d(TAG, "onCreate: is called.");

        bluetooth_notification = (TextView) findViewById(R.id.notification_bluetooth);
        bluetooth_button = (Button) findViewById(R.id.bluetooth_button_onoff);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();


        if(mBluetoothAdapter.isEnabled())
            bluetooth_notification.setText(R.string.hint_bluetoothON);
        else
            bluetooth_notification.setText(R.string.hint_bluetoothOFF);


    }


}
