package com.example.camilla.androidcredentialstore.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class AddCredentialActivity extends AppCompatActivity
{
    private static final String TAG = "AddCredentialActivity";

    TextInputLayout accountLayout;
    TextInputEditText accountname;
    TextInputLayout usernameLayout;
    TextInputEditText username;
    TextInputLayout passwordLayout;
    TextInputEditText password;
    Button saveButton;


    boolean flagWebsite = false;
    boolean flagUsername = false;
    boolean flagPassword = false;

    private DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credential);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "accounts-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        //TODO Research: what does inflate do??
        /*View login_data = View.inflate(this, R.layout.activity_add_credential, null);

        website = (EditText) login_data.findViewById(R.id.website);
        username = (EditText) login_data.findViewById(R.id.username);
        password = (EditText) login_data.findViewById(R.id.password);*/

        accountLayout = (TextInputLayout) findViewById(R.id.AccountLayout);
        accountname = (TextInputEditText) findViewById(R.id.account);

        usernameLayout = (TextInputLayout) findViewById(R.id.UsernameLayout);
        username = (TextInputEditText) findViewById(R.id.username);

        passwordLayout = (TextInputLayout) findViewById(R.id.PasswordLayout);
        password = (TextInputEditText) findViewById(R.id.password);


        //the save button in AddCredentialActivity
        saveButton = (Button) findViewById(R.id.saveBtn);


        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String _account = accountname.getText().toString();
                String _username = username.getText().toString();

                //own method to convert in Converter.java
                byte[] _pwArray = Converter.charToByte(password);

                final Account account = new Account();

                if(!CheckingTools.websiteOk(_account))
                {
                    Toast.makeText(getApplicationContext(), "The entered website is not valid!",
                            Toast.LENGTH_SHORT).show();
                }
                else{ flagWebsite = true; }

                if(!CheckingTools.usernameOk(_username))
                {
                    Toast.makeText(getApplicationContext(), "The entered username is not valid!",
                            Toast.LENGTH_SHORT).show();
                }
                else{ flagUsername = true; }

                if(!CheckingTools.usernameOk(_username))
                {
                    Toast.makeText(getApplicationContext(),
                            "The entered password must be at least 4 characters long!",
                            Toast.LENGTH_SHORT).show();
                }
                else{ flagPassword = true; }


                if(flagWebsite && flagUsername && flagPassword)
                {
                    account.setAccount_name(_account);
                    account.setUsername(_username);
                    account.setPassword(_pwArray);


                    Intent intent = new Intent(AddCredentialActivity.this, ShowAccountsActivity.class);
                    intent.putExtra("account", account);

                    Log.w(TAG, "before saving it into DB");

                    DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());
                    Log.w(TAG, "created a new dbHelper");
                    dbHelper.insertNewAccount(account);

                    Log.w(TAG, "after inserting to DB");

                    setResult(ShowAccountsActivity.RESULT_OK, intent);
                    finish();
                }
                else
                {

                    Toast.makeText(getApplicationContext(), "The account cannot be added!",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}

