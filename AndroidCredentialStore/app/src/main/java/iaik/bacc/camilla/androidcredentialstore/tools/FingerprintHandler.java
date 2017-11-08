package iaik.bacc.camilla.androidcredentialstore.tools;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.widget.Toast;

/**
 * Created by Camilla on 03.11.2017.
 */

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManagerCompat.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context context;

    public FingerprintHandler(Context context1)
    {
        context = context1;
    }

    //startAuth responsible for starting the fingerprint authentication
    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject)
    {
        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT)
                != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        //TODO correct authenticate of startAuth methode, api < 23
        //manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }


    //Toasts for user:

    @Override
    //onAuthenticationError is called when a fatal error has occurred. It provides the error code
    // and error message as its parameters
    public void onAuthenticationError(int errMsgId, CharSequence errString)
    {
        Toast.makeText(context, "Authentication error\n" + errString, Toast.LENGTH_LONG).show();
    }

    @Override
    //onAuthenticationFailed is called when the fingerprint doesn’t match with
    // any of the fingerprints registered on the device//
    public void onAuthenticationFailed()
    {
        Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show();
    }

    @Override
    //onAuthenticationHelp is called when a non-fatal error has occurred.
    // This method provides additional information about the error
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString)
    {
        Toast.makeText(context, "Authentication help\n" + helpString, Toast.LENGTH_LONG).show();
    }

    /*
    @Override
    //onAuthenticationSucceeded for successful match of registered prints on device
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result)
    {
        Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show();
    }*/

}
