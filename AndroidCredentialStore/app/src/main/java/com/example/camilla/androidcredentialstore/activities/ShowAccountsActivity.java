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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.camilla.androidcredentialstore.R;
import com.example.camilla.androidcredentialstore.models.Account;

import java.util.ArrayList;

public class ShowAccountsActivity extends ListActivity
{
    private static final int ADD_ACCOUNT_RESULT_CODE = 15;
    private static final int CHANGE_ACCOUNT_RESULT_CODE = 20;

    TextView account;
    TextView username;
    TextView password;

    final ArrayList<Account> accountArrayList = new ArrayList<>();
    //TODO: new function that gets all credentials out of DB
    // to get the logins into the arraylist, credentialArrayList.add(..) to show with the adapter

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_apps);

        Account account = new Account();
        Account account1 = new Account();
        Account account2 = new Account();

        account.setAccount_name("tugraz");
        account1.setAccount_name("google");
        account2.setAccount_name("facebook");

        accountArrayList.add(account);
        accountArrayList.add(account1);
        accountArrayList.add(account2);

        //show elements in a ListView
        ArrayAdapter<Account> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                accountArrayList);
        setListAdapter(adapter);

        //TODO: greenDAO DB
        //prepare DAO object for Credential class
        //DaoSession daoSession = ((App) getApplication()).getDaoSession();
        //loginDao = daoSession.getLoginDao();

        //for what are these?
        this.account = (TextView) findViewById(R.id.account);
        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);

        /*
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(ShowAccountsActivity.this, ChangeAccountActivity.class);
                startActivityForResult(intent, CHANGE_ACCOUNT_RESULT_CODE);
            }
        });*/


        //TODO: switch to AddCredentialActivity
        FloatingActionButton fab_app = (FloatingActionButton) findViewById(R.id.fab_addApp);
        fab_app.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                showAddCredentialActivity(view);
            }
        });


    }


    //TODO LAST: edit to show clicked account in changeAccountActivity
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id)
    {
        super.onListItemClick(listView, view, position, id);

        //do something using the position in the array

        Intent intent = new Intent(this, ShowAccountsActivity.class);
        Account account = new Account();
        String account_name = account.toString();
        intent.putExtra("account", account);
        startActivityForResult(intent, CHANGE_ACCOUNT_RESULT_CODE);
    }

    //calling constructor without parameters
    public void showAddCredentialActivity(View view)
    {
        Intent intent = new Intent(this, AddCredentialActivity.class);
        startActivityForResult(intent, ADD_ACCOUNT_RESULT_CODE);
       // startActivity(intent);
    }

    //NEEDED FOR SHOWCREDENTIALSACTIVITY:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(requestCode == ADD_ACCOUNT_RESULT_CODE)
        {
            if(resultCode == ShowAccountsActivity.RESULT_OK)
            {
                //Credential credential_extras = (Credential) intent.getSerializableExtra("login");

                Account extras = (Account) intent.getSerializableExtra("credential");
                //only add to arrayList if not null
                if(extras != null)
                {
                    Log.w("SHOWACCOUNTS", "website: " + extras);

                    //TODO: update the listView with new account
                    accountArrayList.add(extras);

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