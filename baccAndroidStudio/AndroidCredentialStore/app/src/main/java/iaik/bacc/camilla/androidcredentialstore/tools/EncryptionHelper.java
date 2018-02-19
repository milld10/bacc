package iaik.bacc.camilla.androidcredentialstore.tools;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import iaik.bacc.camilla.androidcredentialstore.CredentialApplication;

/**
 * Created by Camilla on 14.12.2017.
 * Class for en- and decryption of credentials
 */

public class EncryptionHelper
{
    private final static String TAG = "EncryptionHelper";
    private final static String ALIAS = "aliasCredentialManager";

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private CredentialApplication application;

    private byte[] encryption;
    private byte[] iv;

    KeyStore keyStore;


    public EncryptionHelper(CredentialApplication application)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
            IOException, NoSuchProviderException, InvalidAlgorithmParameterException,
            UnrecoverableEntryException
    {
        this.application = application;

//        loadKeyStore(); //is done in generateKeyAfterCheck.
//        generateSecretKey();
        generateKeyAfterCheck(ALIAS);
    }

    //needed to generate and retrieve the key
    private void loadKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
            IOException
    {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
    }

    /**
     * This method is called first in the Constructor,
     * checks if there is a key with the given alias in the keystore and calls getKeyForEncrypt,
     * if not it calls generate key to generate a new key with that alias.
     */
    private void generateKeyAfterCheck(String alias) throws KeyStoreException, IOException,
            InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException,
            CertificateException, UnrecoverableEntryException {

        loadKeyStore();
        if(keyStore.containsAlias(alias))
        {
            Log.d(TAG, "secret key has already been generated: " + keyStore.getEntry(alias, null));
            //not needed to get a key, key is "getted" when encryption a text
//            getKeyForEncrypt(alias);
        }
        else
        {
            Log.d(TAG, "keystore did not contain key with alias, a new one will be created!");
            generateSecretKey();
        }
    }



    @NonNull
    private SecretKey generateSecretKey() throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException, CertificateException,
            KeyStoreException, IOException
    {
        //keystore should already be loaded
//        loadKeyStore();

        //getting instance of KeyGenerator and save the key in AndroidKeyStore
        final KeyGenerator keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

        //KeyGenParameterSpec: properties for the keys we are going to generate;
        //specify every property the key should have
        //instead of handing alias with function call, the predefined string is used
        KeyGenParameterSpec.Builder SpecBuilder = new KeyGenParameterSpec.Builder(
                ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);

        //Properties of the Key
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            SpecBuilder.setKeySize(128)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE);
        }

        keyGenerator.init(SpecBuilder.build());

        //method generateKey generate a secret key, and stores it automatically into the keystore.
        return keyGenerator.generateKey();
    }


    //----------------------------------------------------------------------------------------------
    //Encryption


    /**
     * Method is used in AddAccountActivity to encrypt the handed over byte array of credentials
     * no alias is needed as a parameter.
     */
    public byte[] encryptText(final byte[] textToEncrypt) throws
            UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            InvalidAlgorithmParameterException, SignatureException, BadPaddingException,
            IllegalBlockSizeException, CertificateException
    {
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getKeyForEncrypt(ALIAS));

        iv = cipher.getIV();

        //doFinal return the byte array which is the encrypted text
        //here the type of textToEncrypt was "String"
        //return(encryption = cipher.doFinal(textToEncrypt.getBytes("UTF-8")));
        return(encryption = cipher.doFinal(textToEncrypt));
    }


    //TODO: check if key is the same for decrypt func, then use only one function!
    private SecretKey getKeyForEncrypt(String alias) throws UnrecoverableEntryException,
            NoSuchAlgorithmException, KeyStoreException
    {
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
    }

    //getter for encryption and IV
    //are key needed? idk for what?
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
    public String decryptData(final byte[] encryptedData, final byte[] encryptionIv)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException
    {
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
        cipher.init(Cipher.DECRYPT_MODE, getKeyForDecrypt(ALIAS), spec);

        return new String(cipher.doFinal(encryptedData), "UTF-8");
    }

    public String decryptDataWithoutIv(final byte[] encryptedData)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException
    {
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        final GCMParameterSpec spec = new GCMParameterSpec(128, cipher.getIV());
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