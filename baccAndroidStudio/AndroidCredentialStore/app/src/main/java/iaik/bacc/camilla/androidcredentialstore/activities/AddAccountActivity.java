package iaik.bacc.camilla.androidcredentialstore.activities;

import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import iaik.bacc.camilla.androidcredentialstore.CredentialApplication;
import iaik.bacc.camilla.androidcredentialstore.R;
import iaik.bacc.camilla.androidcredentialstore.database.DBHelper;
import iaik.bacc.camilla.androidcredentialstore.models.Account;
import iaik.bacc.camilla.androidcredentialstore.models.DaoMaster;
import iaik.bacc.camilla.androidcredentialstore.models.DaoSession;
import iaik.bacc.camilla.androidcredentialstore.tools.CheckingTools;
import iaik.bacc.camilla.androidcredentialstore.tools.Converter;
import iaik.bacc.camilla.androidcredentialstore.tools.EncryptionHelper;

import org.greenrobot.greendao.database.Database;

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


public class AddAccountActivity extends AppCompatActivity
{
    private static final String TAG = "AddAccountActivity";

    TextInputLayout accountLayout;
    TextInputEditText edittext_accountname;
    TextInputLayout usernameLayout;
    TextInputEditText edittext_username;
    TextInputLayout passwordLayout;
    TextInputEditText edittext_password;
    Button saveButton;

    boolean flagEverythingOk = false;

    private DaoSession daoSession;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "accounts-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        //TODO Research: what does inflate do??
        /*View login_data = View.inflate(this, R.layout.activity_add_account, null);

        website = (EditText) login_data.findViewById(R.id.website);
        edittext_username = (EditText) login_data.findViewById(R.id.edittext_username);
        edittext_password = (EditText) login_data.findViewById(R.id.edittext_password);*/

        accountLayout = (TextInputLayout) findViewById(R.id.AccountLayout);
        edittext_accountname = (TextInputEditText) findViewById(R.id.account);

        usernameLayout = (TextInputLayout) findViewById(R.id.UsernameLayout);
        edittext_username = (TextInputEditText) findViewById(R.id.username);

        passwordLayout = (TextInputLayout) findViewById(R.id.PasswordLayout);
        edittext_password = (TextInputEditText) findViewById(R.id.password);

        saveButton = (Button) findViewById(R.id.saveBtn);


        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                String accountForDb = edittext_accountname.getText().toString();
//                String usernameForDb = edittext_username.getText().toString();
//                byte[] passwordForDb = Converter.charToByte(edittext_password);

                //this object will be stored into the DB
                final Account account = new Account();

                if(CheckingTools.websiteOk(edittext_accountname.getText().toString()))
                {
                    if(CheckingTools.usernameOk(edittext_username.getText().toString()))
                    {
                        if(CheckingTools.passwordOk(Converter.charToByte(edittext_password)))
                        {
//                            String accountForDb = edittext_accountname;
                            byte[] usernameForDb = Converter.charToByte(edittext_username);
                            byte[] passwordForDb = Converter.charToByte(edittext_password);

                            try
                            {
                                //TODO encrypt the data after it is checked but before it is stored into the DB
                                EncryptionHelper encryptionHelper = new EncryptionHelper(CredentialApplication.getInstance());

                                //------------Account
                                //TODO maybe don't even encrypt edittext_accountname, for easier display in arraylist adapter??

                                Log.d(TAG, "plaintext account_name: " + edittext_accountname.getText().toString());
//                                Log.d(TAG, "account_name as a byte[]: " + accountForDb);
//                                Log.d(TAG, "account_name as a char[]: " + Converter.byteToChar(accountForDb));
//
//                                byte[] encrypted = encryptionHelper.encryptText(accountForDb);
//                                Log.d(TAG, "account_name encrypted as a byte[]: " + encrypted);
//
//                                byte[] decrypted = encryptionHelper.decryptDataWithoutIv(accountForDb);
//                                Log.d(TAG, "account_name decrypted again a byte[]: " + decrypted);



                                account.setAccount_name(edittext_accountname.getText().toString());
                                Log.d(TAG, "account is now set (not encrypted in plaintext) in the object");


                                //------------Username
                                account.setUsername(encryptionHelper.encrypt(usernameForDb));
                                Log.d(TAG, "edittext_username is now set in the object");


                                //------------Password
                                account.setPassword(encryptionHelper.encrypt(passwordForDb));
                                Log.d(TAG, "pw is now set in the object");

                                Log.d(TAG, "the object is now complete!");

                                flagEverythingOk = true;

                            } catch (CertificateException | NoSuchAlgorithmException |
                                    KeyStoreException | IOException | SignatureException |
                                    UnrecoverableEntryException |  BadPaddingException |
                                    InvalidKeyException | InvalidAlgorithmParameterException |
                                    NoSuchPaddingException | NoSuchProviderException |
                                    IllegalBlockSizeException e)
                            {
                                Log.e(TAG, e.getMessage());
                                e.printStackTrace();
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Password is not valid!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Username is not valid!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Website is not valid!",
                            Toast.LENGTH_SHORT).show();
                }


                if(flagEverythingOk)
                {
                    Intent intent = new Intent(AddAccountActivity.this, ShowAccountsActivity.class);
                    intent.putExtra("account", account);

                    DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());
                    dbHelper.insertNewAccount(account);

                    Log.d(TAG, "new (encrypted) account added to DB");

                    setResult(RESULT_OK, intent);
                    finish();
                }
                else
                {
                    Log.d(TAG, "Intent could not be sent!");

                    Toast.makeText(getApplicationContext(), "The account cannot be added!",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

}

