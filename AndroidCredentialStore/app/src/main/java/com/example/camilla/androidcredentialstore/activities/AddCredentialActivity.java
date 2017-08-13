package com.example.camilla.androidcredentialstore.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.database.sqlite.SQLiteDatabase;

import com.example.camilla.androidcredentialstore.CredentialApplication;
import com.example.camilla.androidcredentialstore.R;
import com.example.camilla.androidcredentialstore.database.DBHelper;
import com.example.camilla.androidcredentialstore.models.Account;
import com.example.camilla.androidcredentialstore.models.DaoMaster;
import com.example.camilla.androidcredentialstore.models.DaoSession;
import com.example.camilla.androidcredentialstore.tools.Converter;

import org.greenrobot.greendao.database.Database;


public class AddCredentialActivity extends AppCompatActivity
{
    EditText username;
    EditText password;
    EditText accountname;
    Context context;

    private DaoSession daoSession;

    Button saveButton;

    //put this cons where data gets saved
    /*public AddCredentialActivity(Context context)
    {
    //TODO rausfinden: wie kann man den konstruktor mit parametern aufrufen kann?
    //wird vil nicht mehr gebraucht wegen greenDAO!!
        //DBHelper = new DBHelper(context);
        //database = DBHelper.getWritableDatabase();
    }*/

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

        accountname = (EditText) findViewById(R.id.account);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

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

                account.setAccount_name(_account);
                account.setUsername(_username);
                account.setPassword(_pwArray);


                Intent intent = new Intent(AddCredentialActivity.this, ShowAccountsActivity.class);
                intent.putExtra("account", account);

                Log.w("ADD ACC", "before saving it into DB");

                DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());
                Log.w("ADD ACC", "created a new dbHelper");
                dbHelper.insertNewAccount(account);

                Log.w("ADD ACC", "after inserting to DB");

                setResult(ShowAccountsActivity.RESULT_OK, intent);
                finish();
            }
        });
    }
}

