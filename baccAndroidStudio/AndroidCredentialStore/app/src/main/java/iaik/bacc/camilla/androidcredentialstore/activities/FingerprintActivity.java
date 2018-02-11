package iaik.bacc.camilla.androidcredentialstore.activities;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import iaik.bacc.camilla.androidcredentialstore.R;
import iaik.bacc.camilla.androidcredentialstore.tools.FingerprintHandler;


public class FingerprintActivity extends AppCompatActivity
{
    private static final String TAG = "FingerprintActivity";

    final String KEYSTORE_PROVIDER = "AndroidKeyStore";

    // Declare a string variable for the key we’re going to use in our fingerprint authentication
    private static final String KEY_NAME = "yourKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;


    TextInputLayout masterPasswordLayout;
    TextInputEditText masterPassword;
    Button accessButton;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

        masterPasswordLayout = (TextInputLayout) findViewById(R.id.masterPasswordLayout);
        masterPassword = (TextInputEditText) findViewById(R.id.masterPassword);

        accessButton = (Button) findViewById(R.id.button_access);


        accessButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //TODO: grant access to show accounts activity
                //for now, the accounts are shown until the password works
                Intent intent = new Intent(FingerprintActivity.this, ShowAccountsActivity.class);
                startActivity(intent);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Log.d(TAG, "now in big first if");
            //verifying the secure lock screen with keyguardManager and fingerprintManager
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);


            TextView textView = findViewById(R.id.mytextview);


            //If device has a fingerprint sensor
            if (!fingerprintManager.isHardwareDetected()) {
                Log.d(TAG, "1. little if");
                textView.setText(R.string.fpactivity_hardwaredetected);
            }

            //If user has granted your app the USE_FINGERPRINT permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "2. little if");
                textView.setText(R.string.fpactivity_permission);
            }

            //Check if user has registered at least one fingerprint
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                Log.d(TAG, "3. little if");
                textView.setText(R.string.fpactivity_enrolledfingerprints);
            }

            //Check that the lockscreen is secured
            if (!keyguardManager.isKeyguardSecure())
            {
                Log.d(TAG, "4. little if");
                textView.setText(R.string.fpactivity_keyguardsecure);
            }
            //if its secured, then generate key, init cipher and startAuth
            else
            {
                try {
                    generateKeyForFingerprint();
                } catch (FingerprintException e) {
                    Log.d(TAG, "ERROR, is in catch of generateKeyForFingerprints");
                    e.printStackTrace();
                }

                if (initCipher()) {
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler helper = new FingerprintHandler(this);
                    helper.startAuth(fingerprintManager, cryptoObject);
                }
            }
        }
    }

    //OnCreate end



//method to gain access to Android keystore and generate the encryption key
    private void generateKeyForFingerprint() throws FingerprintException {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);

            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }


    public boolean initCipher() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //true: cipher has been initialized successfully
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {

            //false: cipher initialization failed
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }



    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }
}

