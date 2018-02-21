package iaik.bacc.camilla.androidcredentialstore.tools;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.crypto.AEADBadTagException;
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
            CertificateException, UnrecoverableEntryException
    {
        loadKeyStore();

        if(keyStore.containsAlias(alias))
        {
            Log.d(TAG, "secret key has already been generated: " + keyStore.getEntry(alias, null));
            //no need to get a key right now, key is used when en-/decrypting a text
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
        //keystore is already loaded in generateKeyAfterCheck
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
     * Method is used to encrypt the handed over byte array of plainText
     */
    public byte[] encrypt(final byte[] plainText) throws
            UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            InvalidAlgorithmParameterException, SignatureException, BadPaddingException,
            IllegalBlockSizeException, CertificateException, IllegalStateException
    {
        Log.d(TAG, "encrypt method has been called!");
        Log.d(TAG, "this is the given plaintext to encrypt: "+ new String(plainText, "UTF-8"));


        byte[] iv = new byte[12];

        Log.d(TAG, "iv after creation: " + Converter.bytesToHex(iv));

//        SecureRandom secureRandom = new SecureRandom();
//        secureRandom.nextBytes(iv);
//        Log.d(TAG, "iv after secureRandom.nextBytes(): " + Converter.bytesToHex(iv));

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//        GCMParameterSpec parameterSpec = new GCMParameterSpec(AUTH_TAG_LEN, iv);

        iv = cipher.getIV();
        Log.d(TAG, "iv after cipher.getIV(): " + Converter.bytesToHex(iv));

        cipher.init(Cipher.ENCRYPT_MODE, getKey(ALIAS));

//        //TODO NEW
//        cipher.updateAAD("MyAAD".getBytes("UTF-8"));



        byte[] cipherText = cipher.doFinal(plainText);

        Log.d(TAG, "encrypt doFinal worked!");
        Log.d(TAG, "cipherText in hex: " + Converter.bytesToHex(cipherText));

        //doFinal return the byte array which is the encrypted text, previously the type of textToEncrypt was "String"
        //return(encryption = cipher.doFinal(textToEncrypt.getBytes("UTF-8")));


        //Concat all information into a single message

        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + cipherText.length);

        Log.d(TAG, "byteBuffer empty: " + byteBuffer);
        byteBuffer.putInt(iv.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);
        byte[] cipherMessage = byteBuffer.array();
        Log.d(TAG, "cipherMessage: " + Converter.bytesToHex(cipherMessage));

        return cipherMessage;
    }


    //----------------------------------------------------------------------------------------------
    //Decryption
    /**
     * Method is used to decrypt the handed over cipherMessage, containing the IV and the cipherText
     */
    public byte[] decrypt(final byte[] cipherMessage) throws NoSuchPaddingException,
            NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException, IOException, NoSuchProviderException, AEADBadTagException
    {
        Log.d(TAG, "decrypt method has been called!");


        //deconstruction of the cipherText and IV:
        Log.d(TAG, "--------------- begin deconstruction");
        Log.d(TAG, "handed over cipherMessage: "+ Converter.bytesToHex(cipherMessage));
        ByteBuffer byteBuffer = ByteBuffer.wrap(cipherMessage);
        int ivLength = byteBuffer.getInt();
        Log.d(TAG, "ivLength: " + ivLength);

        byte[] iv_decrypt = new byte[ivLength];
        Log.d(TAG, "iv declared with ivLength: " + Converter.bytesToHex(iv_decrypt));
        byteBuffer.get(iv_decrypt);
        Log.d(TAG, "iv after byteBuffer.get(iv): " + Converter.bytesToHex(iv_decrypt));
        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);
        Log.d(TAG, "cipherText: "+ Converter.bytesToHex(cipherText));
        Log.d(TAG, "--------------- end deconstruction");


        //actual decryption work:
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        Log.d(TAG, "cipher getInstance worked!");

//        byte[] iv = cipher.getIV();
//        Log.d(TAG, "iv from cipher.getIV(): " + Converter.bytesToHex(iv));

//        iv_decrypt = cipher.getIV();
//        Log.d(TAG, "iv_decrypt from cipher.getIV(): " + Converter.bytesToHex(iv_decrypt));


        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(AUTH_TAG_LEN, iv_decrypt);

        Log.d(TAG, "iv after gcmParameterSpec: " + Converter.bytesToHex(iv_decrypt));
        Log.d(TAG, "GCMParameterSpec worked; parameterSpec is: " + gcmParameterSpec);


        Log.d(TAG, "right before cipher.init in decrypt function!");
        cipher.init(Cipher.DECRYPT_MODE, getKey(ALIAS), gcmParameterSpec);

//        cipher.updateAAD("MyAAD".getBytes("UTF-8"));


        Log.d(TAG, "before cipher.doFinal!");

        byte[] plaintext = cipher.doFinal(cipherText);


        Log.d(TAG, "plaintext is: " + plaintext);
        Log.d(TAG, "plaintext in String is: " + Converter.bytesToHex(plaintext));

        return plaintext;
    }


    private SecretKey getKey(String alias) throws UnrecoverableEntryException,
            NoSuchAlgorithmException, KeyStoreException
    {
        Log.d(TAG, "getKey has been called!");
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
    }


    //TODO return new String(cipher.doFinal(encryptedData), "UTF-8"); function for en/decryption of Strings?
}