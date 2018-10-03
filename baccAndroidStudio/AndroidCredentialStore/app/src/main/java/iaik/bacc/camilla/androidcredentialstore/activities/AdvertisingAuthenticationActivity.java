package iaik.bacc.camilla.androidcredentialstore.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
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
import iaik.bacc.camilla.androidcredentialstore.tools.AdvertisingAuthenticationHandler;
import iaik.bacc.camilla.androidcredentialstore.tools.FingerprintHandler;


public class AdvertisingAuthenticationActivity extends Activity
{
    private static final String TAG = "AdverAuthActivity";

    final String KEYSTORE_PROVIDER = "AndroidKeyStore";

    // Declare a string variable for the key we’re going to use in our fingerprint authentication
    private static final String KEY_NAME = "myKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private AdvertisingAuthenticationHandler handler;
    private KeyguardManager keyguardManager;

    private Intent intent;
    private Context context;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //verifying the secure lock screen with keyguardManager and fingerprintManager
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            TextView textView = findViewById(R.id.mytextview);

            //If device has a fingerprint sensor
            if (!fingerprintManager.isHardwareDetected())
            {
                textView.setText(R.string.fpactivity_hardwaredetected);
            }

            //If user has granted your app the USE_FINGERPRINT permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)
                    != PackageManager.PERMISSION_GRANTED)
            {
                textView.setText(R.string.fpactivity_permission);
            }

            //Check if user has registered at least one fingerprint
            if (!fingerprintManager.hasEnrolledFingerprints())
            {
                textView.setText(R.string.fpactivity_enrolledfingerprints);
            }

            //Check that the lockscreen is secured
            if (!keyguardManager.isKeyguardSecure())
            {
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
                    handler = new AdvertisingAuthenticationHandler(this);
                    handler.startAuth(fingerprintManager, cryptoObject);
                }
            }
        }
    }

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


    private class FingerprintException extends Exception
    {
        public FingerprintException(Exception e) {
            super(e);
        }
    }

}