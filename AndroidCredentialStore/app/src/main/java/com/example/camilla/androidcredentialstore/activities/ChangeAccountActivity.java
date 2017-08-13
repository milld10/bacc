package com.example.camilla.androidcredentialstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.camilla.androidcredentialstore.CredentialApplication;
import com.example.camilla.androidcredentialstore.R;
import com.example.camilla.androidcredentialstore.database.DBHelper;
import com.example.camilla.androidcredentialstore.models.Account;

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


    //TODO: if clicked on account then show all information here and let user change it! DB NEEDED!


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
        Account accountGotten = (Account) intent.getSerializableExtra("clickedAccount");


        Log.w("CHANGE_ACCOUNT", "got the extras: " + accountGotten.toString());

        accountname.setText(accountGotten.getAccount_name());
        username.setText(accountGotten.getUsername());

        byte[] pw = accountGotten.getPassword();

        int length = pw.length;
        char[] pwArray = new char[pw.length];

        

        ByteBuffer buf = StandardCharsets.UTF_8.encode(CharBuffer.wrap(accountGotten.getPassword()));
        byte[] pw = new byte[buf.limit()];



        //********* for the save button
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Log.w("ADDLOGIN", "before getText() func");
                String _account = account.getText().toString();
                // Log.w("ADDLOGIN", "after getText() func -> website: " + _website);

                String _username = username.getText().toString();

                //uses a toString -> not for passwords!
                //byte[] pwArray = password.getText().toString().getBytes(StandardCharsets.UTF_8);
                int length = password.length();
                char[] pwArray = new char[length];
                password.getText().getChars(0, length, pwArray, 0);

                //****
                //casted to byte array?
                //byte[] _pwArray = Charset.forName("UTF-8").encode(CharBuffer.wrap(pwArray)).array();

                ByteBuffer buf = StandardCharsets.UTF_8.encode(CharBuffer.wrap(pwArray));
                byte[] _pwArray = new byte[buf.limit()];
                buf.get(_pwArray);
                //****


                final Account app = new Account();

                app.setAccount_name(_account);
                app.setUsername(_username);
                app.setPassword(_pwArray);

                //final Account app = new Account(account, _username, _pwArray);

                Intent intent = new Intent(ChangeAccountActivity.this, ShowAccountsActivity.class);
                intent.putExtra("credential", app);

                Log.w("ADD", "before saving it into DB");

                DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());
                Log.w("ADD", "created a new dbHelper");
                dbHelper.insertNewAccount(app);

                Log.w("ADD", "after inserting to DB");

                setResult(ShowAccountsActivity.RESULT_OK, intent);
                finish();
            }
        });


        Intent intentRetour = new Intent(ChangeAccountActivity.this, ShowAccountsActivity.class);
        //send the changed object back to DB and other activity
        intent.putExtra("changedAccount", changedAccount);

        setResult(ShowAccountsActivity.RESULT_OK, intentRetour);



    }

}
