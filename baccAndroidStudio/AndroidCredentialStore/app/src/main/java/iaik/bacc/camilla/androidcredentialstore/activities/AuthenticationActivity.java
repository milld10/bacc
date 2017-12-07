package iaik.bacc.camilla.androidcredentialstore.activities;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

import iaik.bacc.camilla.androidcredentialstore.R;

/**
 * Created by Camilla on 06.12.2017.
 */

public class AuthenticationActivity extends AppCompatActivity
{
    private static final String TAG = "AuthenticationActivity";


    TextInputLayout masterPasswordLayout;
    TextInputEditText masterPassword;
    Button accessButton;

    final String KEYSTORE_PROVIDER = "AndroidKeyStore";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authen_screen);

        masterPasswordLayout = (TextInputLayout) findViewById(R.id.masterPasswordLayout);
        masterPassword = (TextInputEditText) findViewById(R.id.masterPassword);

        accessButton = (Button) findViewById(R.id.button_access);

        accessButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //TODO: grant access to show accounts activity
            }
        });

    }

    void loadKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException
    {

    }


}
