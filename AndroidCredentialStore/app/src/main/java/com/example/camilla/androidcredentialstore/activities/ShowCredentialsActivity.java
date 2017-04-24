package com.example.camilla.androidcredentialstore.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.camilla.androidcredentialstore.R;
import com.example.camilla.androidcredentialstore.models.Credential;

import java.util.ArrayList;

public class ShowCredentialsActivity extends ListActivity
{
    private static final int ADD_CRED_RESULT_CODE = 10;

    private ListView credential_list;

    TextView username;
    TextView password;

    final ArrayList<Credential> credentialArrayList = new ArrayList<>();
    //TODO: new function that gets all credentials out of DB


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_credentials);

        Credential credential = new Credential();

        credential.setUsername("testuser");

        credentialArrayList.add(credential);

        ArrayAdapter<Credential> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                credentialArrayList);
        setListAdapter(adapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_addLogin);
        fab.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                showAddCredentialActivity(view);

            }
        });

    }

}
