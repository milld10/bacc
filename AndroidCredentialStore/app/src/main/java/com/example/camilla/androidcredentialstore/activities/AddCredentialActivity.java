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

import org.greenrobot.greendao.database.Database;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class AddCredentialActivity extends AppCompatActivity
{
    EditText username;
    EditText password;
    EditText account;
    Context context;

    DBHelper dbHelper;
    SQLiteDatabase database;

    private DaoSession daoSession;

    CredentialApplication credentialApplication;

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

        account = (EditText) findViewById(R.id.account);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        //the save button in AddCredentialActivity
        saveButton = (Button) findViewById(R.id.saveBtn);

        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Log.w("ADDLOGIN", "before getText() func");
                String _account = account.getText().toString();
                // Log.w("ADDLOGIN", "after getText() func -> website: " + _website);

                String _username = username.getText().toString();

                //uses a toString -> not for passwords!
                //byte[] pwArray = password.getText().toString().getBytes(StandardCharsets.UTF_8);
                int length = password.length();
                char[] pwArray = new char[length];
                password.getText().getChars(0, length, pwArray, 0);

                //****
                //casted to byte array?
                //byte[] _pwArray = Charset.forName("UTF-8").encode(CharBuffer.wrap(pwArray)).array();

                ByteBuffer buf = StandardCharsets.UTF_8.encode(CharBuffer.wrap(pwArray));
                byte[] _pwArray = new byte[buf.limit()];
                buf.get(_pwArray);
                //****


                final Account app = new Account();

                app.setAccount_name(_account);
                app.setUsername(_username);
                app.setPassword(_pwArray);

                //final Account app = new Account(account, _username, _pwArray);

                Intent intent = new Intent(AddCredentialActivity.this, ShowAccountsActivity.class);
                intent.putExtra("credential", app);

                Log.w("ADD", "before saving it into DB");

                DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());
                Log.w("ADD", "created a new dbHelper");
                dbHelper.insertNewAccount(app);

                Log.w("ADD", "after inserting to DB");

                setResult(ShowAccountsActivity.RESULT_OK, intent);
                finish();
            }
        });

    }

    /*

    public DaoSession getDaoSession()
    {
        return daoSession;
    }


    byte[] pw = {1,2,3};
    Account account1 = new Account(1, "google", "user1", pw);

    long account_id = getDaoSession().getAccountDao().insert(account1);*/



}

