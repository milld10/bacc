package iaik.bacc.camilla.androidcredentialstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import iaik.bacc.camilla.androidcredentialstore.R;

public class MainActivity extends Activity
{
    private static final String TAG = "MainActivity";

    private static final int FINGERPRINT_SUCCESS_RESULT_CODE = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void fingerprintActivity(View view)
    {
        Intent intent = new Intent(this, FingerprintActivity.class);
        intent.putExtra("from", "MainActivity");
        startActivity(intent);
//        startActivityForResult(intent, FINGERPRINT_SUCCESS_RESULT_CODE);
    }

    public void showBluetoothActivity(View view)
    {
        Intent intent = new Intent(this, PeripheralActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void showSettingsActivity(View view)
    {
        //TODO: add functionality
        //here the master password can be changed
    }

    public void showAboutActivity(View view)
    {
        Intent intent = new Intent(this, AboutDialog.class);
        startActivity(intent);

//        now the about is a normal activity, but can also be a dialog:
//        FullScreenDialog dialog = new FullScreenDialog();
//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//        dialog.show(fragmentTransaction, FullScreenDialog.TAG);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        switch(resultCode)
        {
            case FINGERPRINT_SUCCESS_RESULT_CODE:
                if(resultCode == FingerprintActivity.RESULT_OK)
                {
                    Intent showAccountsIntent = new Intent(this, ShowAccountsActivity.class);
                    startActivity(showAccountsIntent);
                }

        }
    }
}