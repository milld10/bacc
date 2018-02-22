package iaik.bacc.camilla.androidcredentialstore.activities;

/**
 * Activity to display all logins in a list
 */

import android.app.ListActivity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

import iaik.bacc.camilla.androidcredentialstore.CredentialApplication;
import iaik.bacc.camilla.androidcredentialstore.R;
import iaik.bacc.camilla.androidcredentialstore.database.DBHelper;
import iaik.bacc.camilla.androidcredentialstore.models.Account;
import iaik.bacc.camilla.androidcredentialstore.tools.Converter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShowAccountsActivity extends ListActivity
{
    private static final int ADD_ACCOUNT_RESULT_CODE = 15;
    private static final int CHANGE_ACCOUNT_RESULT_CODE = 20;

    private static final String TAG = "ShowAccountsActivity";

    ArrayList<Account> accountArrayList = new ArrayList<>();

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_accounts);

        DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());
        accountArrayList = (ArrayList<Account>) dbHelper.getAllAccounts();

        mToolbar = (Toolbar) findViewById(R.id.toolbar_show_accounts);
        mToolbar.setTitle(R.string.show_accounts);


        //show elements in the ListView <activity_show_accounts>
        final ArrayAdapter<Account> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                accountArrayList);


        setListAdapter(adapter);
        //what does notifyDataSetChanged do??
        adapter.notifyDataSetChanged();


        //FAButton switches to AddAccountActivity
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


    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id)
    {
        super.onListItemClick(listView, view, position, id);

        Account account = accountArrayList.get(position);

        Intent intent = new Intent(ShowAccountsActivity.this, ChangeAccountActivity.class);
        intent.putExtra("clickedAccount",account);

        Log.d(TAG, "before sending intent to change account activity");
        Log.d(TAG, "account-name: " + account.getAccount_name() + " with id: " + account.getAccount_id());

        startActivityForResult(intent, CHANGE_ACCOUNT_RESULT_CODE);
    }


    public void showAddCredentialActivity(View view)
    {
        Intent intent = new Intent(this, AddAccountActivity.class);
        startActivityForResult(intent, ADD_ACCOUNT_RESULT_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        switch (requestCode)
        {
            case ADD_ACCOUNT_RESULT_CODE:
                if (resultCode == ShowAccountsActivity.RESULT_OK)
                {
                    Account extras = (Account) intent.getSerializableExtra("credential");
                    //only add to arrayList if not null
                    if (extras != null) {
                        Log.w(TAG, "website: " + extras + " id: " + extras.getAccount_id());

                        //update the listView with new account
                        accountArrayList.add(extras);
                        getListAdapter().notify();

                        Log.d(TAG, "NEW account added to the arraylist");
                    }
                }
                break;
            case CHANGE_ACCOUNT_RESULT_CODE:
                if (resultCode == ShowAccountsActivity.RESULT_OK)
                {
                    Account extras = (Account) intent.getSerializableExtra("credential");
                    //only add to arrayList if not null
                    if (extras != null) {
                        Log.d(TAG, "website: " + extras + ", id: " + extras.getAccount_id());

                        //update the listView with changed account
                        accountArrayList.add(extras);

                        Log.d(TAG, "CHANGED account added to the arraylist");
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