package com.example.camilla.androidcredentialstore.tools;

import android.support.design.widget.TextInputEditText;

/**
 * Class CheckingTools holds methods for checking the input of the user
 */

public class CheckingTools {


    public static boolean websiteOk(TextInputEditText website)
    {

        int _website = website.getText().toString().trim().length();
        if(_website > 0)
        {
            
        }


    }

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

}
