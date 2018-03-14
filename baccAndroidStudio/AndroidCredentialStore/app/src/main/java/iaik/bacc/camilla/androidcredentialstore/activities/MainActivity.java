package iaik.bacc.camilla.androidcredentialstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.greenrobot.greendao.database.Database;

import iaik.bacc.camilla.androidcredentialstore.CredentialApplication;
import iaik.bacc.camilla.androidcredentialstore.R;
import iaik.bacc.camilla.androidcredentialstore.database.DBHelper;
import iaik.bacc.camilla.androidcredentialstore.models.DaoMaster;
import iaik.bacc.camilla.androidcredentialstore.models.DaoSession;

public class MainActivity extends Activity
{
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void fingerprintActivity(View view)
    {
        Intent intent = new Intent(this, FingerprintActivity.class);
        startActivity(intent);
    }

    public void showBluetoothActivity(View view)
    {
        Intent intent = new Intent(this, BluetoothActivity.class);
        startActivity(intent);
    }

//    public void showAddLoginActivity(View view)
//    {
//        Intent intent = new Intent(this, AddAccountActivity.class);
//        startActivity(intent);
//    }

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
}