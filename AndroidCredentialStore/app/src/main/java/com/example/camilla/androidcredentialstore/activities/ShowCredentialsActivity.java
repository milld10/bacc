package com.example.camilla.androidcredentialstore.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.camilla.androidcredentialstore.R;

import java.util.ArrayList;
/*

commented out due to know needed in version2.
ShowCredentialsActivity was for handling the object Credentials and showing them in a list when clicked on
the account where the credentials belonged to.

public class ShowCredentialsActivity extends ListActivity
{
    private static final int ADD_CRED_RESULT_CODE = 10;

    TextView username;
    TextView password;

    final ArrayList<Credential> credentialArrayList = new ArrayList<>();
    //TODO: new function that gets all credentials out of DB


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_credentials);

        Account app = new Account();
        Credential credential = new Credential();

        Intent intent = getIntent();
        app.setAccount_name(intent.getStringExtra("account"));
        //String account = intent.getStringExtra("account");



        credential.setUsername("testuser_static");

        credentialArrayList.add(credential);

        //TODO: now still static, but get list from DB
        ArrayAdapter<Credential> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                credentialArrayList);
        setListAdapter(adapter);


        FloatingActionButton fab_cred = (FloatingActionButton) findViewById(R.id.fab_addCred);
        fab_cred.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                showAddCredentialActivity(view);

            }
        });

    }
    public void showAddCredentialActivity(View view)
    {
        Intent intent = new Intent(this, AddCredentialActivity.class);
        Account app = new Account();
        String account = app.getAccount_name();
        intent.putExtra("account", account);
        //startActivityForResult(intent, ADD_CRED_RESULT_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(requestCode == ADD_CRED_RESULT_CODE)
        {
            if(resultCode == ShowAccountsActivity.RESULT_OK)
            {
                Credential credential_extras = (Credential) intent.getSerializableExtra("credential");

                if(credential_extras != null)
                {
                    Log.w("ShowCredActi", "worked");
                    credentialArrayList.add(credential_extras);

                    //create a new appofcredential then a credential?
                }
            }
        }
    }
}
*/