package iaik.bacc.camilla.androidcredentialstore.tools;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
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

    private final static int AUTH_TAG_LEN = 128;

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private CredentialApplication application;

    private byte[] cipherText;
//    private byte[] cipherMessage; //don't make global?

    private byte[] encryption;

    //iv size 12 byte, not 16!
    private byte[] iv = new byte[12];

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
            //not needed to get a key right now, key is used when encrypting a text
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

    //TODO: maybe create a encrypt method for strings instead of byte[]; for account_name?
    /**
     * Method is used in AddAccountActivity to encrypt the handed over byte array of credentials
     * no alias is needed as a parameter.
     */
    public byte[] encrypt(final byte[] plainText) throws
            UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            InvalidAlgorithmParameterException, SignatureException, BadPaddingException,
            IllegalBlockSizeException, CertificateException
    {

        Log.d(TAG, "encrypt method has been called!");

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//        GCMParameterSpec parameterSpec = new GCMParameterSpec(AUTH_TAG_LEN, iv);

        cipher.init(Cipher.ENCRYPT_MODE, getKey(ALIAS));

        iv = cipher.getIV();
        Log.d(TAG, "iv after cipher.getIV(): " + iv);

        cipherText = cipher.doFinal(plainText);


        //doFinal return the byte array which is the encrypted text, previously the type of textToEncrypt was "String"
        //return(encryption = cipher.doFinal(textToEncrypt.getBytes("UTF-8")));


        //Concat all information into a single message
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + cipherText.length);

        byteBuffer.putInt(iv.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);
        byte[] cipherMessage = byteBuffer.array();

        return cipherMessage;
    }


    //getter for encryption and IV
    //are key needed? idk for what?
//    byte[] getEncryption()
//    {
//        return encryption;
//    }
//
//    byte[] getIV()
//    {
//        return iv;
//    }

    //----------------------------------------------------------------------------------------------
    //Decryption

    //no alias is handed over as parameter,
    //TODO change that only param is ecryptedData and the IV needs to be extracted
//    public String decryptData(final byte[] encryptedData, final byte[] encryptionIv)
//            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
//            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
//            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException
//    {
//        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//        final GCMParameterSpec spec = new GCMParameterSpec(AUTH_TAG_LEN, encryptionIv);
//        cipher.init(Cipher.DECRYPT_MODE, getKeyForDecrypt(ALIAS), spec);
//
//        return new String(cipher.doFinal(encryptedData), "UTF-8");
//    }

    public byte[] decrypt(final byte[] cipherMessage)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException
    {
        Log.d(TAG, "decrypt method has been called!");


        //deconstruction of the cipherText and IV:
        ByteBuffer byteBuffer = ByteBuffer.wrap(cipherMessage);
        int ivLength = byteBuffer.getInt();
//        byte[] iv = new byte[ivLength];
        iv = new byte[ivLength];
        byteBuffer.get(iv);
//        byte[] cipherText = new byte[byteBuffer.remaining()];
        cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);



        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        Log.d(TAG, "cipher getInstance worked!");

//        byte[] iv = cipher.getIV();
//        Log.d(TAG, "iv from cipher.getIV(): " + iv);

        final GCMParameterSpec parameterSpec = new GCMParameterSpec(AUTH_TAG_LEN, iv);
        Log.d(TAG, "iv: " + iv);
        Log.d(TAG, "new gcmparameter spec worked!");

        cipher.init(Cipher.DECRYPT_MODE, getKey(ALIAS), parameterSpec);
        Log.d(TAG, "cipher.init worked!");

//        return new String(cipher.doFinal(encryptedData), "UTF-8");
        Log.d(TAG, "before cipher.doFinal!");
        return cipher.doFinal(cipherText);
    }


    //TODO maybe same as for encrypt --> check
//    private SecretKey getKeyForDecrypt(final String alias) throws NoSuchAlgorithmException,
//            UnrecoverableEntryException, KeyStoreException
//    {
//        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
//    }


    //TODO: check if key is the same for decrypt func, then use only one function!
//    private SecretKey getKeyForEncrypt(String alias) throws UnrecoverableEntryException,
//            NoSuchAlgorithmException, KeyStoreException
//    {
//        Log.d(TAG, "getKeyforEncrypt has been called!");
//        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
//    }

    private SecretKey getKey(String alias) throws UnrecoverableEntryException, KeyStoreException,
            NoSuchAlgorithmException
    {
        Log.d(TAG, "getKey has been called!");
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
    }
}