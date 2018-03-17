package iaik.bacc.camilla.androidcredentialstore.activities;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import iaik.bacc.camilla.androidcredentialstore.CredentialApplication;
import iaik.bacc.camilla.androidcredentialstore.R;
import iaik.bacc.camilla.androidcredentialstore.database.DBHelper;
import iaik.bacc.camilla.androidcredentialstore.models.Account;

/**
 * Created by Camilla on 14.03.2018.
 * In this activity all available accounts are shown and at the same time this class
 * handles all BLE connections
 * This activity is called upon the user interaction pressing the button on the website
 */

public class ShowAvailableAccountsActivity extends ListActivity
{
    private static final String TAG = "ScannerLE";

    private BluetoothAdapter mBluetoothAdapter;

    Toolbar mToolbar;

    TextView bluetooth_notification;
    Button bluetooth_button;

    ArrayList<Account> accountArrayList = new ArrayList<>();

    DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());

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

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        final ArrayAdapter<Account> adapter = new ArrayAdapter<Account>(this,
                android.R.layout.simple_list_item_1,
                accountArrayList);

        setListAdapter(adapter);
        adapter.notifyDataSetChanged();


        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();


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
    protected void onStart()
    {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver1, intentFilter);

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
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if(action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED))
            {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        mBluetoothAdapter.ERROR);

                switch(state)
                {
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


    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id)
    {
        super.onListItemClick(listView, view, position, id);

        mBluetoothAdapter.cancelDiscovery();


        //TODO is a list of accounts that are shown, and user has to select one to send

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
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
    }
}
