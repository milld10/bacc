package com.example.camilla.androidcredentialstore.activities;

import android.accounts.Account;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.example.camilla.androidcredentialstore.R;

/**
 * Created by Camilla on 02.08.2017.
 */

public class ChangeAccountActivity extends AppCompatActivity
{
    private static final int ADD_CRED_RESULT_CODE = 10;

    TextView accountname;
    TextView username;
    TextView password;
    Button saveButton;


    //TODO: if clicked on account then show all information here and let user change it! DB NEEDED!


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_account);

        accountname = (TextView) findViewById(R.id.accountname);
        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);

        saveButton = (Button) findViewById(R.id.saveBtn);

    }

}
