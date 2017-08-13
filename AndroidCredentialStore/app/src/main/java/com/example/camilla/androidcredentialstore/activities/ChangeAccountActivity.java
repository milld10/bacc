package com.example.camilla.androidcredentialstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.design.widget.TextInputLayout;

import com.example.camilla.androidcredentialstore.CredentialApplication;
import com.example.camilla.androidcredentialstore.R;
import com.example.camilla.androidcredentialstore.database.DBHelper;
import com.example.camilla.androidcredentialstore.models.Account;
import com.example.camilla.androidcredentialstore.tools.Converter;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Created by Camilla on 02.08.2017.
 */

public class ChangeAccountActivity extends AppCompatActivity
{
    private static final int ADD_CRED_RESULT_CODE = 10;

    EditText accountname;
    EditText username;
    EditText password;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_account);

        accountname = (EditText) findViewById(R.id.accountname);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        saveButton = (Button) findViewById(R.id.saveBtn);

        Intent intent = getIntent();
        final Account accountGotten = (Account) intent.getSerializableExtra("clickedAccount");

        Log.w("CHANGE_ACCOUNT", "got the extras: " + accountGotten.toString());

        accountname.setText(accountGotten.getAccount_name());
        username.setText(accountGotten.getUsername());

        //conversion of byte to char array, now in Converter.java
        int length = accountGotten.getPassword().length;
        password.setText(Converter.byteToChar(accountGotten), 0, length);


        //TODO LAST: 13.8. functionality for save button
        //********* for the save button
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());
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



        //this intent for when no changes are made???

        //Intent intentRetour = new Intent(ChangeAccountActivity.this, ShowAccountsActivity.class);
        //send the changed object back to DB and other activity
        //intent.putExtra("changedAccount", changedAccount);

        //setResult(ShowAccountsActivity.RESULT_OK, intentRetour);
    }
}
