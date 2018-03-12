package iaik.bacc.camilla.androidcredentialstore.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import iaik.bacc.camilla.androidcredentialstore.CredentialApplication;
import iaik.bacc.camilla.androidcredentialstore.R;
import iaik.bacc.camilla.androidcredentialstore.models.MasterPassword;
import iaik.bacc.camilla.androidcredentialstore.tools.CheckingTools;
import iaik.bacc.camilla.androidcredentialstore.tools.Converter;
import iaik.bacc.camilla.androidcredentialstore.tools.EncryptionHelper;

/**
 * Created by Camilla on 11.03.2018.
 */

public class SetMasterPassword extends Activity
{

    private static final String TAG = "SetMasterPWActivity";

    TextInputLayout masterPasswordLayout;
    TextInputEditText edittext_masterPassword;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_master_password);

        masterPasswordLayout = (TextInputLayout) findViewById(R.id.masterPasswordLayout);
        edittext_masterPassword = (TextInputEditText) findViewById(R.id.masterPassword);

        saveButton = (Button) findViewById(R.id.button_save);


        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //TODO: check if there is a password stored, if not then save in MP table
                //also later on: encrypt and put into table
                Log.d(TAG, "save button has been clicked");

                final MasterPassword masterPassword = new MasterPassword();

                if(CheckingTools.passwordOk(Converter.charToByte(edittext_masterPassword)))
                {

                    byte[] mpassword = Converter.charToByte(edittext_masterPassword);

                    try {
                        EncryptionHelper encryptionHelper =
                                new EncryptionHelper(CredentialApplication.getInstance());

                        //This is the encryption of the password!
                        //TODO encrypt once the method works!!
//                        masterPassword.setMasterPassword(encryptionHelper.encrypt(mpassword));

                    }
                    catch (CertificateException | NoSuchAlgorithmException |
                            KeyStoreException | IOException | UnrecoverableEntryException |
                            InvalidAlgorithmParameterException | NoSuchProviderException e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                    //needed exceptions for encryption:
//                    catch (CertificateException | NoSuchAlgorithmException |
//                            KeyStoreException | IOException | SignatureException |
//                            UnrecoverableEntryException |  BadPaddingException |
//                            InvalidKeyException | InvalidAlgorithmParameterException |
//                            NoSuchPaddingException | NoSuchProviderException |
//                            IllegalBlockSizeException e) {
//                        Log.e(TAG, e.getMessage());
//                        e.printStackTrace();
//                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Password is not valid!",
                            Toast.LENGTH_SHORT).show();


                }

            }
        });

    }


}
