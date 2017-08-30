package com.example.camilla.androidcredentialstore.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.design.widget.TextInputLayout;
import android.support.design.widget.TextInputEditText;

import com.example.camilla.androidcredentialstore.CredentialApplication;
import com.example.camilla.androidcredentialstore.R;
import com.example.camilla.androidcredentialstore.database.DBHelper;
import com.example.camilla.androidcredentialstore.models.Account;
import com.example.camilla.androidcredentialstore.models.DaoMaster;
import com.example.camilla.androidcredentialstore.models.DaoSession;
import com.example.camilla.androidcredentialstore.tools.Converter;

import org.greenrobot.greendao.database.Database;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Created by Camilla on 02.08.2017.
 */

public class ChangeAccountActivity extends AppCompatActivity
{
    TextInputLayout accountLayout;
    TextInputEditText accountname;
    TextInputLayout usernameLayout;
    TextInputEditText username;
    TextInputLayout passwordLayout;
    TextInputEditText password;
    Button saveButton;
    Button deleteButton;

    private DaoSession daoSession;


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


        /*
        //***********************
        Account testAccount = new Account();
        long testId = 15;
        Long obj = new Long(testId);

        testAccount = dbHelper.getAccountById(obj);

        Log.w("CHANGE_ACCOUNT", "TESTACCOUNT ID: " + testAccount.getPassword() + " and its id: " + testAccount.getAccount_id());
        //***********************
        */

        accountname.setText(accountGotten.getAccount_name());
        username.setText(accountGotten.getUsername());

        //conversion of byte to char array, now in Converter.java
        int length = accountGotten.getPassword().length;
        Log.w("CHANGE_ACCOUNT", "pw BEFORE converter: " + accountGotten.getPassword().toString());

        password.setText(Converter.byteToChar(accountGotten), 0, length);

        Log.w("CHANGE_ACCOUNT", "pw AFTER converter: " + password.getText().toString());

        //TODO LAST: 13.8. functionality for save button
        //********* for the save button
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());
                Log.w("CHANGE ACC", "created a new dbHelper");

                //delete the clicked account first
                dbHelper.deleteAccount(accountGotten);


                String _account = accountname.getText().toString();
                String _username = username.getText().toString();
                byte[] _pwArray = Converter.charToByte(password);

                final Account account = new Account();

                account.setAccount_name(_account);
                account.setUsername(_username);
                account.setPassword(_pwArray);


                Intent intent = new Intent(ChangeAccountActivity.this, ShowAccountsActivity.class);
                intent.putExtra("account", account);

                Log.w("ADD", "before saving it into DB");

                dbHelper.insertNewAccount(account);

                Log.w("ADD", "after inserting to DB");

                setResult(ShowAccountsActivity.RESULT_OK, intent);
                finish();
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
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                //.setIcon(R.drawable.delete)

                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        /*
                        daoSession = new DaoMaster(db).newSession();
                        final DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());

                        Intent intent = getIntent();
                        final Account accountGotten = (Account) intent.getSerializableExtra("clickedAccount");

                        dbHelper.deleteAccount(accountGotten);
                        */
                        DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());
                        dbHelper.deleteAccountById(id);

                        finish();



                        dialog.dismiss();
                    }
                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {

                        dialog.dismiss();
                    }
                })
                .create();

        return myQuittingDialogBox;
    }

}
