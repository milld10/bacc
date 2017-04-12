package com.example.camilla.androidcredentialstore.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.MenuItemHoverListener;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.camilla.androidcredentialstore.database.DBHelper;
import com.example.camilla.androidcredentialstore.models.Login;
import com.example.camilla.androidcredentialstore.R;



public class AddLoginActivity extends AppCompatActivity
{
    EditText username;
    EditText password;
    EditText website;
    Context context;

    com.example.camilla.androidcredentialstore.database.DBHelper DBHelper;
    SQLiteDatabase database;

    Button btn;


    //put this cons where data gets saved
    /*public AddLoginActivity(Context context)
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
        setContentView(R.layout.activity_add_login);

        //TODO Research: what does inflate do??
        /*View login_data = View.inflate(this, R.layout.activity_add_login, null);

        website = (EditText) login_data.findViewById(R.id.website);
        username = (EditText) login_data.findViewById(R.id.username);
        password = (EditText) login_data.findViewById(R.id.password);*/

        website = (EditText) findViewById(R.id.website);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        //the save button in AddLoginActivity
        btn = (Button) findViewById(R.id.saveBtn);

        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final Login login = new Login();

               // Log.w("ADDLOGIN", "before getText() func");
                String _website = website.getText().toString();
               // Log.w("ADDLOGIN", "after getText() func -> website: " + _website);

                String _username = username.getText().toString();

                //normal über 2 Zeile, jetzt ohne zwischenspeichern in String
                //String _password = password.getText().toString();
                //char[] pwArray = _password.toCharArray();

                char[] pwArray = password.getText().toString().toCharArray();


                boolean flag_website = false;
                boolean flag_username = false;
                boolean flag_pw = false;


                if(!_website.matches(""))
                {
                    if(Patterns.WEB_URL.matcher(_website).matches())
                    {
                        login.setWebsite(_website);
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
                    login.setUsername(_username);
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
                    login.setPassword(pwArray);
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



                if(flag_website && flag_username && flag_pw)
                {
                    //****** Code just executed when all fields als filled in and no exceptions are thrown

                    Log.w("ADDLOGIN", "obj website: " + login.getWebsite() + " || txt website: " + _website);

                    Intent intent = new Intent(AddLoginActivity.this, LoginsActivity.class);
                    intent.putExtra("login", login);

                    setResult(LoginsActivity.RESULT_OK, intent);
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

