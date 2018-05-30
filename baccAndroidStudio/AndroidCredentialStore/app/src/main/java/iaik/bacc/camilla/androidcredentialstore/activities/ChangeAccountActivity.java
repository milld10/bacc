package iaik.bacc.camilla.androidcredentialstore.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.support.design.widget.TextInputLayout;
import android.support.design.widget.TextInputEditText;
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


/**
 * Created by Camilla on 02.08.2017.
 */

public class ChangeAccountActivity extends AppCompatActivity
{
    private static final String TAG = "ChangeAccountActivity";

    TextInputLayout accountLayout;
    TextInputEditText edittext_accountname;
    TextInputLayout usernameLayout;
    TextInputEditText edittext_username;
    TextInputLayout passwordLayout;
    TextInputEditText edittext_password;

    Button saveButton;
    Button deleteButton;

    boolean flagEverythingOk = false;

    private DaoSession daoSession;

    //global account id for deleting in alert dialog
    Long id;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_account);


        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "accounts-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        final DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());

        accountLayout = (TextInputLayout) findViewById(R.id.AccountLayout);
        edittext_accountname = (TextInputEditText) findViewById(R.id.account);

        usernameLayout = (TextInputLayout) findViewById(R.id.UsernameLayout);
        edittext_username = (TextInputEditText) findViewById(R.id.username);

        passwordLayout = (TextInputLayout) findViewById(R.id.PasswordLayout);
        edittext_password = (TextInputEditText) findViewById(R.id.password);


        saveButton = (Button) findViewById(R.id.saveBtn);
        deleteButton = (Button) findViewById(R.id.delBtn);

        final Intent intent = getIntent();
        final Account clickedAccount = (Account) intent.getSerializableExtra("clickedAccount");

        id = clickedAccount.getAccount_id();


        Log.w("CHANGE_ACCOUNT", "got the extras: " + clickedAccount.toString());
        Log.w("CHANGE_ACCOUNT", "id of account: " + clickedAccount.getAccount_id());


        try
        {
            final EncryptionHelper encryptionHelper =
                    new EncryptionHelper(CredentialApplication.getInstance());
            Log.d(TAG, "new encryptionHelper object has been generated (within try/catch)");

            //Decryption of data retrieved from DB
            byte[] usernameHlp = encryptionHelper.decrypt(clickedAccount.getUsername());
            byte[] passwordHlp = encryptionHelper.decrypt(clickedAccount.getPassword());

            edittext_accountname.setText(clickedAccount.getAccount_name());

            int lengthUsername = usernameHlp.length;
            edittext_username.setText(Converter.byteToChar(usernameHlp), 0, lengthUsername);

            int lengthPw = passwordHlp.length;
            edittext_password.setText(Converter.byteToChar(passwordHlp), 0, lengthPw);


        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException |
            NoSuchProviderException | InvalidAlgorithmParameterException | BadPaddingException |
                NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
            UnrecoverableEntryException e)
        {
            e.printStackTrace();
        }


        //Similar to AddAccountActivity
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //First: delete the clicked account
                dbHelper.deleteAccount(clickedAccount);

                //New account to store changed account into DB
                final Account account = new Account();

                if(CheckingTools.websiteOk(edittext_accountname.getText().toString()))
                {
                    if(CheckingTools.usernameOk(edittext_username.getText().toString()))
                    {
                        if(CheckingTools.passwordOk(Converter.charToByte(edittext_password)))
                        {
                            byte[] usernameForDb = Converter.charToByte(edittext_username);
                            byte[] passwordForDb = Converter.charToByte(edittext_password);

                            try {
                                EncryptionHelper encryptionHelper =
                                        new EncryptionHelper(CredentialApplication.getInstance());

                                account.setAccount_name(edittext_accountname.getText().toString());
                                account.setUsername(encryptionHelper.encrypt(usernameForDb));
                                account.setPassword(encryptionHelper.encrypt(passwordForDb));

                                Log.d(TAG, "the object is now complete and encrypted!");

                                flagEverythingOk = true;

                            } catch (CertificateException | NoSuchAlgorithmException |
                                    KeyStoreException | IOException | SignatureException |
                                    UnrecoverableEntryException |  BadPaddingException |
                                    InvalidKeyException | InvalidAlgorithmParameterException |
                                    NoSuchPaddingException | NoSuchProviderException |
                                    IllegalBlockSizeException e) {
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
                    Intent intent = new Intent(ChangeAccountActivity.this, ShowAccountsActivity.class);
                    intent.putExtra("account", account);

                    dbHelper.insertNewAccount(account);

                    Log.d(TAG, "account changed successfully and added to DB");

                    //close db:
                    daoSession.getDatabase().close();

                    setResult(RESULT_OK, intent);
                    finish();
                }
                else {
                    Log.d(TAG, "Intent could not be sent!");

                    Toast.makeText(getApplicationContext(), "The account could not be changed!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view){
                AlertDialog dialog = AskToDelete();
                dialog.show();
            }
        });
    }


    private AlertDialog AskToDelete()
    {
        AlertDialog deleteDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)

                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());
                        dbHelper.deleteAccountById(id);

                        dialog.dismiss();
                        finish();
                    }
                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        dialog.dismiss();
                    }
                })
                .create();

        return deleteDialog;
    }
}