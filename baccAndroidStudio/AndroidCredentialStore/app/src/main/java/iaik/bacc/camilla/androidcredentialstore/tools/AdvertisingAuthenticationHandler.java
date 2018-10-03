package iaik.bacc.camilla.androidcredentialstore.tools;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import iaik.bacc.camilla.androidcredentialstore.activities.PeripheralActivity;
import iaik.bacc.camilla.androidcredentialstore.activities.ShowAccountsActivity;

/**
 * Created by Camilla on 03.11.2017.
 */


@TargetApi(Build.VERSION_CODES.M)
public class AdvertisingAuthenticationHandler extends FingerprintManager.AuthenticationCallback
{
    private static final String TAG = "AdvertisingAuthenticationHandler";

    private CancellationSignal cancellationSignal;
    private Context appContext;

    public AdvertisingAuthenticationHandler(Context context)
    {
        appContext = context;
    }

    //method responsible for starting the fingerprint authentication
    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject)
    {
        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.USE_FINGERPRINT)
                != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }



    @Override
    //is called when a fatal error has occurred, provides error code and error message
    public void onAuthenticationError(int errMsgId, CharSequence errString)
    {
        Toast.makeText(appContext, "Authentication error\n" + errString, Toast.LENGTH_SHORT).show();
    }

    @Override
    //is called when the fingerprint doesn’t match with any of the fingerprints registered on the device
    public void onAuthenticationFailed()
    {
        Toast.makeText(appContext, "Authentication failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    //is called when a non-fatal error has occurred, provides additional information about the error
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString)
    {
        Toast.makeText(appContext, "Authentication help\n" + helpString, Toast.LENGTH_SHORT).show();
    }

    @Override
    //for successful match of registered prints on device
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result)
    {
        Intent success = new Intent();
        ((Activity) appContext).setResult(Activity.RESULT_OK, success);
        ((Activity) appContext).finish();
    }
}
