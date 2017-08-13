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

import com.example.camilla.androidcredentialstore.CredentialApplication;
import com.example.camilla.androidcredentialstore.R;
import com.example.camilla.androidcredentialstore.database.DBHelper;
import com.example.camilla.androidcredentialstore.models.Account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShowAccountsActivity extends ListActivity
{
    private static final int ADD_ACCOUNT_RESULT_CODE = 15;
    private static final int CHANGE_ACCOUNT_RESULT_CODE = 20;

    TextView account;
    TextView username;
    TextView password;

    ArrayList<Account> accountArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_accounts);

        DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());
        accountArrayList = (ArrayList<Account>) dbHelper.getAllAccounts();

        //show elements in the ListView <activity_show_accounts>
        ArrayAdapter<Account> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                accountArrayList);

        /*adapter.sort(new Comparator<Account>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });*/

        setListAdapter(adapter);
        //what does notifyDataSetChanged do??
        adapter.notifyDataSetChanged();



        //for what are these?
        this.account = (TextView) findViewById(R.id.account);
        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);


        //FAButton switches to AddCredentialActivity
        FloatingActionButton fab_app = (FloatingActionButton) findViewById(R.id.fab_addApp);
        fab_app.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                showAddCredentialActivity(view);
            }
        });
    }

    //onCreate end


    //TODO LAST: edit to show clicked account in changeAccountActivity
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id)
    {
        super.onListItemClick(listView, view, position, id);

        //do something using the position in the array

        Account account = accountArrayList.get(position);

        Intent intent = new Intent(ShowAccountsActivity.this, ChangeAccountActivity.class);
        intent.putExtra("clickedAccount",account);

        Log.w("SHOWACCOUNTS", "before sending intent to change account activity");
        Log.w("SHOWACCOUNTS", "account-name: " + account.getAccount_name() + " with id: " + account.getAccount_id());

        startActivityForResult(intent, CHANGE_ACCOUNT_RESULT_CODE);
    }


    //calling constructor without parameters
    public void showAddCredentialActivity(View view)
    {
        Intent intent = new Intent(this, AddCredentialActivity.class);
        startActivityForResult(intent, ADD_ACCOUNT_RESULT_CODE);
       // startActivity(intent);
    }

    //for method showAddCredentialActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        switch (requestCode)
        {
            case ADD_ACCOUNT_RESULT_CODE:
                if (resultCode == ShowAccountsActivity.RESULT_OK)
                {
                    //Credential credential_extras = (Credential) intent.getSerializableExtra("login");

                    Account extras = (Account) intent.getSerializableExtra("credential");
                    //only add to arrayList if not null
                    if (extras != null) {
                        Log.w("SHOWACCOUNTS", "website: " + extras);
                        Log.w("SHOWACCOUNTS", "id: " + extras.getAccount_id());

                        //update the listView with new account
                        accountArrayList.add(extras);
                        getListAdapter().notify();

                        Log.w("SHOWACCOUNTS", "NEW account added to the arraylist");
                    }
                }
                break;
            case CHANGE_ACCOUNT_RESULT_CODE:
                if (resultCode == ShowAccountsActivity.RESULT_OK)
                {
                    Account extras = (Account) intent.getSerializableExtra("credential");
                    //only add to arrayList if not null
                    if (extras != null) {
                        Log.w("SHOWACCOUNTS", "website: " + extras);
                        Log.w("SHOWACCOUNTS", "id: " + extras.getAccount_id());

                        //update the listView with changed account
                        accountArrayList.add(extras);

                        Log.w("SHOWACCOUNTS", "CHANGED account added to the arraylist");
                    }
                }
                break;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());
        accountArrayList = (ArrayList<Account>) dbHelper.getAllAccounts();

        ArrayAdapter<Account> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                accountArrayList);

        setListAdapter(adapter);
    }
}