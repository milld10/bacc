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

import com.example.camilla.androidcredentialstore.R;
import com.example.camilla.androidcredentialstore.models.Account;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class AddCredentialActivity extends AppCompatActivity
{
    EditText username;
    EditText password;
    EditText account;
    Context context;

    com.example.camilla.androidcredentialstore.database.DBHelper DBHelper;
    SQLiteDatabase database;

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
                final Account app = new Account();

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

                //actually false
                boolean flag_website = true;
                boolean flag_username = true;
                boolean flag_pw = true;


                //credential.setUsername("surprise");
                app.setAccount_name(_account);
                app.setUsername(_username);
                app.setPassword(_pwArray);


                //TODO: checks for the fields, make new and less checks!
                /*
                if(!_website.isEmpty())
                {
                    if(Patterns.WEB_URL.matcher(_website).matches())
                    {
                        //credential.setWebsite(_website);
                        flag_website = true;
                    }
                    else
                    {
                        //website is not valid
                        Toast.makeText(getApplicationContext(), "The entered website is not valid!",
                                Toast.LENGTH_SHORT).show();
                        //return;
                    }
                }
                else
                {
                    //toast that website is not valid
                    Toast.makeText(getApplicationContext(), "You did not enter a website!",
                            Toast.LENGTH_SHORT).show();
                    //return;
                }


                if(!_username.matches(""))
                {
                    //other checks to username?
                    credential.setUsername(_username);
                    flag_username = true;
                }
                else
                {
                    //toast that website is not valid
                    Toast.makeText(getApplicationContext(), "You did not enter a username!",
                            Toast.LENGTH_SHORT).show();
                    //return;
                }


                if(pwArray.length != 0)
                {
                    credential.setPassword(pwArray);
                    flag_pw = true;

                    //fill array with zeros
                    for(int i = 0; i < pwArray.length; i++)
                    {
                        pwArray[i] = 0;
                    }
                }
                else
                {
                    //toast that website is not valid
                    Toast.makeText(getApplicationContext(), "You did not enter a password!",
                            Toast.LENGTH_SHORT).show();
                    //return;
                }

                */


                if(flag_website && flag_username && flag_pw)
                {
                    //****** Code just executed when all fields als filled in and no exceptions are thrown

                    Log.w("ADDLOGIN", "before finishing the new intent");

                    Intent intent = new Intent(AddCredentialActivity.this, ShowAccountsActivity.class);
                    intent.putExtra("credential", app);

                    //for Credential list
                    //setResult(ShowCredentialsActivity.RESULT_OK, intent);

                    //for App List
                    setResult(ShowAccountsActivity.RESULT_OK, intent);
                    finish();
                }
                else
                {
                    Log.w("ADDLOGIN", "Intent could not be sent");
                    /*Toast.makeText(getApplicationContext(), "Intent could not be sent",
                            Toast.LENGTH_SHORT).show();*/
                }
            }
        });
    }
}

