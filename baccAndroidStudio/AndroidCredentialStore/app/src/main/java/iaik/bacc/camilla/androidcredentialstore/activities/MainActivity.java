package iaik.bacc.camilla.androidcredentialstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import iaik.bacc.camilla.androidcredentialstore.R;
import iaik.bacc.camilla.androidcredentialstore.tools.FullScreenDialog;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        no need for toolbar, because of own settings and about menu
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
    }

    /*public void showAddLoginActivity(View view)
    {
        Intent intent = new Intent(this, AddAccountActivity.class);
        startActivity(intent);
    }*/

    public void showBluetoothActivity(View view)
    {
        Intent intent = new Intent(this, BluetoothActivity.class);
        startActivity(intent);
    }

//    public void showAccountsActivity(View view)
//    {
//        Intent intent = new Intent(this, ShowAccountsActivity.class);
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

    //TODO: add functionality

    public void showSettingsActivity(View view) {
    }

    public void showAboutActivity(View view) {
        Intent intent = new Intent(this, AboutDialog.class);
        startActivity(intent);

//        now the about is a normal activity, but can also be a dialog:
//        FullScreenDialog dialog = new FullScreenDialog();
//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//        dialog.show(fragmentTransaction, FullScreenDialog.TAG);
    }

    public void fingerprintActivity(View view) {
        Intent intent = new Intent(this, FingerprintActivity.class);
        startActivity(intent);
    }




}