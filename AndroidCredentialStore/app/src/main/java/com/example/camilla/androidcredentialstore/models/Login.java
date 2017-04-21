package com.example.camilla.androidcredentialstore.models;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

/**
 * Created by Camilla on 29.03.2017.
 */

@Entity(
        active =  true,

        nameInDb = "CREDENTIALS"//,

        //generateConstructors = true,
        //generateGettersSetters = true
)
public class Login implements java.io.Serializable
{
    //used for error of serialization
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private long id;

    @Property(nameInDb = "WEBSITE")
    private String website;

    @Property(nameInDb = "USERNAME")
    private String username;

    //@Property(nameInDb = "PASSWORD")
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
