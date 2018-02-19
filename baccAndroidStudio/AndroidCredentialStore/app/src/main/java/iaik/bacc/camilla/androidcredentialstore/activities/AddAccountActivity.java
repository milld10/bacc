package iaik.bacc.camilla.androidcredentialstore.activities;

import android.content.Intent;
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
    TextInputEditText accountname;
    TextInputLayout usernameLayout;
    TextInputEditText username;
    TextInputLayout passwordLayout;
    TextInputEditText password;
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

        //for encryption of data before storing it into the DB
        final EncryptionHelper encryptionHelper;

        //TODO Research: what does inflate do??
        /*View login_data = View.inflate(this, R.layout.activity_add_account, null);

        website = (EditText) login_data.findViewById(R.id.website);
        username = (EditText) login_data.findViewById(R.id.username);
        password = (EditText) login_data.findViewById(R.id.password);*/

        accountLayout = (TextInputLayout) findViewById(R.id.AccountLayout);
        accountname = (TextInputEditText) findViewById(R.id.account);

        usernameLayout = (TextInputLayout) findViewById(R.id.UsernameLayout);
        username = (TextInputEditText) findViewById(R.id.username);

        passwordLayout = (TextInputLayout) findViewById(R.id.PasswordLayout);
        password = (TextInputEditText) findViewById(R.id.password);

        saveButton = (Button) findViewById(R.id.saveBtn);


        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String _account = accountname.getText().toString();
                String _username = username.getText().toString();
                byte[] _pwArray = Converter.charToByte(password);

                //this object will be stored into the DB
                final Account account = new Account();

                if(CheckingTools.websiteOk(_account))
                {
                    if(CheckingTools.usernameOk(_username))
                    {
                        if(CheckingTools.passwordOk(_pwArray))
                        {
                            //object account gets initialized to store in DB
                            //TODO encrypt the data after it is checked but before it is stored into the DB
                            account.setAccount_name(_account);
//                            account.setUsername(_username);
//                            account.setPassword(_pwArray);
                            try
                            {
                                EncryptionHelper encryptionHelper = new EncryptionHelper(CredentialApplication.getInstance());
//                                Log.d(TAG, "just about to convert the account to an byte array: " + accountname);
//                                byte[] hlpAccount = Converter.charToByte(accountname);
//                                Log.d(TAG, "converted account to byte array, success! " + hlpAccount);
//                                account.setAccount_name_encrypted(encryptionHelper.encryptText(hlpAccount));
//                                Log.d(TAG, "account is now set in the object");

                                //Dont encrypt account name, for list in show accounts
//                                account.setAccount_name(_account);

                                Log.d(TAG, "about to convert username: " + username);
                                byte[] hlpUsername = Converter.charToByte(username);
                                Log.d(TAG, "converted username to byte array, success! " + hlpUsername);
                                account.setUsername_encrypted(encryptionHelper.encryptText(hlpUsername));
                                Log.d(TAG, "username is now set in the object");

                                //_pwArray is already a byte[]; no need for convertion
                                account.setPassword(encryptionHelper.encryptText(_pwArray));
                                Log.d(TAG, "pw is now set in the object, the object is now complete!");

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

                            flagEverythingOk = true;
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Password is not valid!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Username is not valid!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
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

