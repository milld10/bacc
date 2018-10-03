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

        if(keyStore.containsAlias(alias)) {
            Log.d(TAG, "secret key has already been generated: " + keyStore.getEntry(alias, null));
        }
        else {
            Log.d(TAG, "keystore did not contain key with alias, a new one will be created!");
            generateSecretKey();
        }
    }

    @NonNull
    private SecretKey generateSecretKey() throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException, CertificateException,
            KeyStoreException, IOException
    {
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
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(false);
        }

        keyGenerator.init(SpecBuilder.build());

        //method generateKey generate a secret key, and stores it automatically into the keystore.
        return keyGenerator.generateKey();
    }


    //----------------------------------------------------------------------------------------------
    //Encryption
    /** Method is used to encrypt the handed over byte array of plainText */
    public byte[] encrypt(final byte[] plainText) throws
            UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            InvalidAlgorithmParameterException, SignatureException, BadPaddingException,
            IllegalBlockSizeException, CertificateException, IllegalStateException
    {
        Log.d(TAG, "encrypt method has been called!");

        byte[] iv = new byte[12];

        //creating my own secureRandom for IV
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        Log.d(TAG, "iv after secureRandom.nextBytes(): " + Converter.bytesToHex(iv));

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        GCMParameterSpec spec = new GCMParameterSpec(AUTH_TAG_LEN, iv);
        cipher.init(Cipher.ENCRYPT_MODE, getKey(ALIAS), spec);

        Log.d(TAG, "iv after cipher.getIV(): " + Converter.bytesToHex(iv));

        byte[] cipherText = cipher.doFinal(plainText);

        //Concat all information into a single message
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + cipherText.length);
        byteBuffer.putInt(iv.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);
        Log.d(TAG, "cipherText in hex: " + Converter.bytesToHex(cipherText));
        byte[] cipherMessage = byteBuffer.array();
        Log.d(TAG, "cipherMessage in hex: " + Converter.bytesToHex(cipherMessage));

        return cipherMessage;
    }


    //----------------------------------------------------------------------------------------------
    //Decryption
    /** Method is used to decrypt the handed over cipherMessage,
     * containing the IV and the cipherText */
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
        byte[] iv_decrypt = new byte[ivLength];
        byteBuffer.get(iv_decrypt);
        Log.d(TAG, "iv after byteBuffer.get(iv): " + Converter.bytesToHex(iv_decrypt));
        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);
        Log.d(TAG, "cipherText: "+ Converter.bytesToHex(cipherText));
        Log.d(TAG, "--------------- end deconstruction");


        //actual decryption:
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(AUTH_TAG_LEN, iv_decrypt);

        cipher.init(Cipher.DECRYPT_MODE, getKey(ALIAS), gcmParameterSpec);

        byte[] plaintext = cipher.doFinal(cipherText);
        Log.d(TAG, "plaintext (in hex) is: " + Converter.bytesToHex(plaintext));

        return plaintext;
    }

    private SecretKey getKey(String alias) throws UnrecoverableEntryException,
            NoSuchAlgorithmException, KeyStoreException
    {
        Log.d(TAG, "getKey has been called!");
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
    }

}