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
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;


/**
 * Created by Camilla on 02.08.2017.
 */

public class ChangeAccountActivity extends AppCompatActivity
{
    private static final String TAG = "ChangeAccountActivity";

    TextInputLayout accountLayout;
    TextInputEditText accountname;
    TextInputLayout usernameLayout;
    TextInputEditText username;
    TextInputLayout passwordLayout;
    TextInputEditText password;
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
        accountname = (TextInputEditText) findViewById(R.id.account);

        usernameLayout = (TextInputLayout) findViewById(R.id.UsernameLayout);
        username = (TextInputEditText) findViewById(R.id.username);

        passwordLayout = (TextInputLayout) findViewById(R.id.PasswordLayout);
        password = (TextInputEditText) findViewById(R.id.password);

        saveButton = (Button) findViewById(R.id.saveBtn);
        deleteButton = (Button) findViewById(R.id.delBtn);

        final Intent intent = getIntent();
        final Account clickedAccount = (Account) intent.getSerializableExtra("clickedAccount");

        id = clickedAccount.getAccount_id();

        Log.w("CHANGE_ACCOUNT", "got the extras: " + clickedAccount.toString());
        Log.w("CHANGE_ACCOUNT", "id of account: " + clickedAccount.getAccount_id());


        /** TODO decrypt text before displaying it back onto the screen */
        accountname.setText(clickedAccount.getAccount_name());
        username.setText(clickedAccount.getUsername());

        int length = clickedAccount.getPassword().length;
        password.setText(Converter.byteToChar(clickedAccount), 0, length);

//        try {
//            EncryptionHelper encryptionHelper = new EncryptionHelper(CredentialApplication.getInstance());
//            encryptionHelper.decryptDataWithoutIv(clickedAccount.getUsername());
//
//        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException |
//                UnrecoverableEntryException | NoSuchProviderException |
//                InvalidAlgorithmParameterException | IOException e) {
//            e.printStackTrace();
//        }


//        char[] _accountName = Converter.byteToChar();


        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //delete the clicked account first
                dbHelper.deleteAccount(clickedAccount);


                String _account = accountname.getText().toString();
                String _username = username.getText().toString();
                byte[] _pwArray = Converter.charToByte(password);

                final Account account = new Account();

                if(CheckingTools.websiteOk(_account))
                {
                    if(CheckingTools.usernameOk(_username))
                    {
                        if(CheckingTools.passwordOk(_pwArray))
                        {
                            //object account gets initialized
                            account.setAccount_name(_account);
                            account.setUsername(_username);
                            account.setPassword(_pwArray);

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
                    Intent intent = new Intent(ChangeAccountActivity.this, ShowAccountsActivity.class);
                    intent.putExtra("account", account);

                    dbHelper.insertNewAccount(account);

                    Log.w(TAG, "account changes successfully and added to DB");

                    setResult(RESULT_OK, intent);
                    finish();
                }
                else
                {
                    Log.d(TAG, "Intent could not be sent!");

                    Toast.makeText(getApplicationContext(), "The account cannot be changed!",
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


        //this intent for when no changes are made???

        //Intent intentRetour = new Intent(ChangeAccountActivity.this, ShowAccountsActivity.class);
        //send the changed object back to DB and other activity
        //intent.putExtra("changedAccount", changedAccount);

        //setResult(ShowAccountsActivity.RESULT_OK, intentRetour);
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