package com.example.camilla.androidcredentialstore.activities;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.camilla.androidcredentialstore.CredentialApplication;
import com.example.camilla.androidcredentialstore.R;
import com.example.camilla.androidcredentialstore.database.DBHelper;
import com.example.camilla.androidcredentialstore.models.Account;
import com.example.camilla.androidcredentialstore.models.DaoMaster;
import com.example.camilla.androidcredentialstore.models.DaoSession;
import com.example.camilla.androidcredentialstore.tools.CheckingTools;
import com.example.camilla.androidcredentialstore.tools.Converter;

import org.greenrobot.greendao.database.Database;


public class AddCredentialActivity extends AppCompatActivity
{
    private static final String TAG = "AddCredentialActivity";

    TextInputLayout accountLayout;
    TextInputEditText accountname;
    TextInputLayout usernameLayout;
    TextInputEditText username;
    TextInputLayout passwordLayout;
    TextInputEditText password;
    Button saveButton;


    boolean flagWebsite = false;
    boolean flagUsername = false;
    boolean flagPassword = false;

    boolean flagEverythingOk = false;

    private DaoSession daoSession;

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

        accountLayout = (TextInputLayout) findViewById(R.id.AccountLayout);
        accountname = (TextInputEditText) findViewById(R.id.account);

        usernameLayout = (TextInputLayout) findViewById(R.id.UsernameLayout);
        username = (TextInputEditText) findViewById(R.id.username);

        passwordLayout = (TextInputLayout) findViewById(R.id.PasswordLayout);
        password = (TextInputEditText) findViewById(R.id.password);

        saveButton = (Button) findViewById(R.id.saveBtn);


        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String _account = accountname.getText().toString();
                String _username = username.getText().toString();
                byte[] _pwArray = Converter.charToByte(password);

                final Account account = new Account();

                if(CheckingTools.websiteOk(_account))
                {
                    if(CheckingTools.usernameOk(_username))
                    {
                        if(CheckingTools.passwordOk(_pwArray))
                        {
                            //object account gets initialized
                            account.setAccount_name(_account);
                            account.setUsername(_username);
                            account.setPassword(_pwArray);

                            flagEverythingOk = true;
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Password is not valid!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Username is not valid!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Website is not valid!",
                            Toast.LENGTH_SHORT).show();
                }



                if(flagEverythingOk)
                {
                    Intent intent = new Intent(AddCredentialActivity.this, ShowAccountsActivity.class);
                    intent.putExtra("account", account);

                    DBHelper dbHelper = new DBHelper(CredentialApplication.getInstance());
                    dbHelper.insertNewAccount(account);

                    Log.w(TAG, "new account added to DB");

                    setResult(ShowAccountsActivity.RESULT_OK, intent);
                    finish();
                }
                else
                {
                    Log.d(TAG, "Intent could not be sent!");

                    Toast.makeText(getApplicationContext(), "The account cannot be added!",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}

