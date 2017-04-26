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
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.camilla.androidcredentialstore.R;
import com.example.camilla.androidcredentialstore.models.AppOfCredential;
import com.example.camilla.androidcredentialstore.models.Credential;

import java.util.ArrayList;
import java.util.List;

public class ShowAppsActivity extends ListActivity
{
    private static final int ADD_APP_RESULT_CODE = 15;

    TextView account;
    TextView username;
    TextView password;

    final ArrayList<AppOfCredential> credentialArrayList = new ArrayList<>();
    //TODO: new function that gets all credentials out of DB
    // to get the logins into the arraylist, credentialArrayList.add(..) to show with the adapter

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_apps);

        AppOfCredential appOfCredential = new AppOfCredential();
        AppOfCredential appOfCredential1 = new AppOfCredential();
        AppOfCredential appOfCredential2 = new AppOfCredential();

        appOfCredential.setAccount_name("tugraz");
        appOfCredential1.setAccount_name("google");
        appOfCredential2.setAccount_name("facebook");
        //get a List:
        //List<Credential> list = appOfCredential.getCredentialList();


        credentialArrayList.add(appOfCredential);
        credentialArrayList.add(appOfCredential1);
        credentialArrayList.add(appOfCredential2);

        //show elements in a ListView
        ArrayAdapter<AppOfCredential> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                credentialArrayList);
        setListAdapter(adapter);

        //TODO: greenDAO DB
        //prepare DAO object for Credential class
        //DaoSession daoSession = ((App) getApplication()).getDaoSession();
        //loginDao = daoSession.getLoginDao();

        //for what are these?
        account = (TextView) findViewById(R.id.account);
        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);


        //TODO: TODO: 2 different buttons or the same in show apps/credentials activity
        FloatingActionButton fab_app = (FloatingActionButton) findViewById(R.id.fab_addApp);
        fab_app.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                //TODO checks; then show new view????ntialActivity(view);
                showAddCredentialActivity(view);
            }
        });
    }


    /*
    //shows credential list of clicked account
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id)
    {
        super.onListItemClick(listView, view, position, id);

        //todo: logic here -> show the listactivity of the credentials in show ShowCredentialAc.
        Intent intent = new Intent(this, ShowCredentialsActivity.class);
        AppOfCredential app = new AppOfCredential();
        String account = app.toString();
        intent.putExtra("account", account);
        //startActivityForResult(intent, CODE); idk if needed?!
        startActivity(intent);

    }*/


    //calling constructor without parameters
    public void showAddCredentialActivity(View view)
    {
        Intent intent = new Intent(this, AddCredentialActivity.class);
        startActivityForResult(intent, ADD_APP_RESULT_CODE);
       // startActivity(intent);
    }


    //NEEDED FOR SHOWCREDENTIALSACTIVITY:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(requestCode == ADD_APP_RESULT_CODE)
        {
            if(resultCode == ShowAppsActivity.RESULT_OK)
            {
                //Credential credential_extras = (Credential) intent.getSerializableExtra("login");

                AppOfCredential extras = (AppOfCredential) intent.getSerializableExtra("credential");
                //only add to arrayList if not null
                if(extras != null)
                {
                    //Log.w("LOGIN", "website: " + extras);

                    //TODO put object into DB with greenDAO here:

                    //add user login to the array list
                    //credentialArrayList.add(credential_extras);
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