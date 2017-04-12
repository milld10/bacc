package com.example.camilla.androidcredentialstore.models;


/**
 * Created by Camilla on 29.03.2017.
 */

public class Login implements java.io.Serializable
{
    //private long id; //created automatically by greenDao
    private String website;
    private String username;
    private char[] password;

    /*
    //brauche ich den constructor mit parameter??
    public Login(String website, String username, char[] password)
    {
        super();
        this.website = website;
        this.username = username;
        this.password = password;
    }*/


    public String getWebsite() {return website;}

    public void setWebsite(String website) {this.website = website;}


    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}


    public char[] getPassword() {return password;}

    public void setPassword(char[] password) {this.password = password;}


    //used by ArrayAdapter in LoginsActivity
    @Override
    public String toString()
    {
        return website;
    }
}
