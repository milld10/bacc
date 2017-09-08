package iaik.bacc.camilla.androidcredentialstore.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Camilla on 02.08.2017.
 */

@Entity
public class Account implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private Long account_id;

    @Property(nameInDb = "ACCOUNT_NAME")
    private String account_name;

    @Property(nameInDb = "USERNAME")
    private String username;

    @Property(nameInDb = "PASSWORD")
    private byte[] password;


    @Generated(hash = 985418116)
    public Account(Long account_id, String account_name, String username,
            byte[] password) {
        this.account_id = account_id;
        this.account_name = account_name;
        this.username = username;
        this.password = password;
    }


    @Generated(hash = 882125521)
    public Account() {
    }


    @Override
    public String toString()
    {
        return account_name;
    }


    public Long getAccount_id() {
        return this.account_id;
    }


    public void setAccount_id(Long account_id) {
        this.account_id = account_id;
    }


    public String getAccount_name() {
        return this.account_name;
    }


    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }


    public String getUsername() {
        return this.username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public byte[] getPassword() {
        return this.password;
    }


    public void setPassword(byte[] password) {
        this.password = password;
    }




}
