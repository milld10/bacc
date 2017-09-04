package com.example.camilla.androidcredentialstore.activities;

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

import com.example.camilla.androidcredentialstore.CredentialApplication;
import com.example.camilla.androidcredentialstore.R;
import com.example.camilla.androidcredentialstore.database.DBHelper;
import com.example.camilla.androidcredentialstore.models.Account;
import com.example.camilla.androidcredentialstore.models.DaoMaster;
import com.example.camilla.androidcredentialstore.models.DaoSession;
import com.example.camilla.androidcredentialstore.tools.CheckingTools;
import com.example.camilla.androidcredentialstore.tools.Converter;

import org.greenrobot.greendao.database.Database;


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
        final Account accountGotten = (Account) intent.getSerializableExtra("clickedAccount");

        id = accountGotten.getAccount_id();

        Log.w("CHANGE_ACCOUNT", "got the extras: " + accountGotten.toString());
        Log.w("CHANGE_ACCOUNT", "id of account: " + accountGotten.getAccount_id());


        accountname.setText(accountGotten.getAccount_name());
        username.setText(accountGotten.getUsername());

        int length = accountGotten.getPassword().length;
        password.setText(Converter.byteToChar(accountGotten), 0, length);


        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //delete the clicked account first
                dbHelper.deleteAccount(accountGotten);


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

                    setResult(ShowAccountsActivity.RESULT_OK, intent);
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