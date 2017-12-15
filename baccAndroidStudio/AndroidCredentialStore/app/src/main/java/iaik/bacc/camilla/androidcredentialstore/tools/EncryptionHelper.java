package iaik.bacc.camilla.androidcredentialstore.tools;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Created by Camilla on 14.12.2017.
 * Class with encrypt and decrypt methods
 */

public class EncryptionHelper
{
    private final static String TAG = "EncryptionHelper";
    private final static String ALIAS = "aliasCredentialManager";

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private byte[] encryption;
    private byte[] iv;

    KeyStore keyStore;

    EncryptionHelper() throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
            IOException
    {
        loadKeyStore();
    }


    void loadKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
            IOException
    {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
    }

    @NonNull
    private SecretKey generateSecretKey() throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException, CertificateException,
            KeyStoreException, IOException
    {
        loadKeyStore();

        //getting instance of KeyGenerator and save the key in AndroidKeyStore
        final KeyGenerator keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

        //KeyGenParameterSpec: properties for the keys we are going to generate;
        //specify every property the key should have
        //instead of handing alias with function call, the predefined string is used
        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(
                ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);

        //Properties of the Key
        builder.setKeySize(128)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE);


        keyGenerator.init(builder.build());

        //method generateKey generate a secret key
        return keyGenerator.generateKey();
    }


    //----------------------------------------------------------------------------------------------
    //Encryption

    //no alias is handed over with function call
    byte[] encryptText(final String textToEncrypt) throws
            UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            InvalidAlgorithmParameterException, SignatureException, BadPaddingException,
            IllegalBlockSizeException
    {
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getKeyForEncrypt(ALIAS));

        iv = cipher.getIV();

        //doFinal return the byte array which is the encrypted text
        return(encryption = cipher.doFinal(textToEncrypt.getBytes("UTF-8")));
    }

    //TODO: check if key is the same for decrypt func, then use only one function!
    private SecretKey getKeyForEncrypt(String alias) throws UnrecoverableEntryException,
            NoSuchAlgorithmException, KeyStoreException
    {
        /*Key key;
        try {
            key = KeyStore.getInstance(ANDROID_KEY_STORE).getKey(alias, null);
        }
        catch (KeyStoreException e)
        {
            Log.e(TAG, e.getMessage());
        }
        catch (UnrecoverableKeyException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return  key.getKey(alias, null);*/
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
    }

    //getter for encryption and IV
    byte[] getEncryption()
    {
        return encryption;
    }

    byte[] getIV()
    {
        return iv;
    }

    //----------------------------------------------------------------------------------------------
    //Decryption

    //no alias is handed over as parameter,
    //TODO change that only param is ecryptedData and the IV needs to be extracted
    String decryptData(final byte[] encryptedData, final byte[] encryptionIv)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException
    {
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
        cipher.init(Cipher.DECRYPT_MODE, getKeyForDecrypt(ALIAS), spec);

        return new String(cipher.doFinal(encryptedData), "UTF-8");
    }


    //TODO maybe same as for encrypt --> check
    private SecretKey getKeyForDecrypt(final String alias) throws NoSuchAlgorithmException,
            UnrecoverableEntryException, KeyStoreException
    {
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
    }

}
