package com.example.camilla.androidcredentialstore.activities;

/**
 * Activity to display all logins in a list
 */

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

public class LoginsActivity extends ListActivity
{
    private static final int ADD_LOGIN_RESULT_CODE = 15;

    private ListView list;

    //private LoginDao loginDao;

    TextView website;
    TextView username;
    TextView password;

    final ArrayList<Credential> credentialArrayList = new ArrayList<Credential>();
    //to get the logins into the arraylist, credentialArrayList.add(..) to show with the adapter

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logins);

        Credential credential = new Credential();
        credential.setWebsite("www.google.com");
        credential.setUsername("testuser");

        credentialArrayList.add(credential);

        //show elements in a ListView
        ArrayAdapter<Credential> adapter = new ArrayAdapter<>(this,
                                        android.R.layout.simple_list_item_1,
                credentialArrayList);
        setListAdapter(adapter);

        //TODO: greenDAO DB
        //prepare DAO object for Credential class
        //DaoSession daoSession = ((App) getApplication()).getDaoSession();
        //loginDao = daoSession.getLoginDao();

        //for what are these?
        website = (TextView) findViewById(R.id.website);
        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_addLogin);
        fab.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                //TODO checks; then show new view????
                showAddLoginActivity(view);
            }
        });
    }

    //calling constructor without parameters
    public void showAddLoginActivity(View view)
    {
        Intent intent = new Intent(this, AddLoginActivity.class);
        startActivityForResult(intent, ADD_LOGIN_RESULT_CODE);
       // startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {

        if(requestCode == ADD_LOGIN_RESULT_CODE)
        {
            if(resultCode == LoginsActivity.RESULT_OK)
            {
                Credential credential_extras = (Credential) intent.getSerializableExtra("login");

                //only add to arrayList if not null
                if(credential_extras != null)
                {
                    Log.w("LOGIN", "website: " + credential_extras);

                    //TODO put object into DB with greenDAO? here:

                    //add user login to the array list
                    credentialArrayList.add(credential_extras);
                }
            }
        }
    }
}

        /*if(extras != null)
        {
            int Value = extras.getInt("id");

            if(Value > 0)
            {
                String website = extras.getString("website");
                String username = extras.getString("username");
                char[] password = extras.getCharArray("password");

                Cursor cursor = mydb.getData(Value);
                id_To_Update = Value;
                cursor.moveToFirst();

                String website = cursor.getString(rs.getColumnIndex(DBHelper.COLUMN_WEBSITE));
                String username = cursor.getString(rs.getColumnIndex(DBHelper.COLUMN_USERNAME));
                String password = cursor.getString(rs.getColumnIndex(DBHelper.COLUMN_PASSWORD));

                if(!cursor.isClosed())
                {
                    cursor.close();
                }
            }
        }*/