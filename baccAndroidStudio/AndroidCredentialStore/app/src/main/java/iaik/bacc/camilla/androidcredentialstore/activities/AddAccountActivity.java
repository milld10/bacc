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
                                EncryptionHelper encryptionHelper =
                                        new EncryptionHelper(CredentialApplication.getInstance());

                                Log.d(TAG, "plaintext account_name: " +
                                        edittext_accountname.getText().toString());
                                account.setAccount_name(edittext_accountname.getText().toString());

                                account.setUsername(encryptionHelper.encrypt(usernameForDb));

                                account.setPassword(encryptionHelper.encrypt(passwordForDb));
//                                Log.d(TAG, "pw is now set in the object");

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

