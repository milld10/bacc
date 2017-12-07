package iaik.bacc.camilla.androidcredentialstore.tools;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by Camilla on 06.12.2017.
 */

class Encryption
{
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private byte[] encryption;
    private byte[] iv;

    Encryption()
    {
    }

    byte[] encryptText(final String alias, final String textToEncrypt) throws
        UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
        NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
        InvalidAlgorithmParameterException, SignatureException, BadPaddingException,
        IllegalBlockSizeException
    {
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias));

        iv = cipher.getIV();

        //doFinal return the byte array which is the encrypted text
        return(encryption = cipher.doFinal(textToEncrypt.getBytes("UTF-8")));
    }

    @NonNull
    private SecretKey getSecretKey(final String alias) throws NoSuchAlgorithmException,
        NoSuchProviderException, InvalidAlgorithmParameterException
    {
        //getting instance of KeyGenerator and save the key in AndroidKeyStore
        final KeyGenerator keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

        //KeyGenParameterSpec: properties for the keys we are going to generate;
        //specify every property the key should have
        keyGenerator.init(new KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build());

        //method generateKey generate a secret key
        return keyGenerator.generateKey();
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
}
