package iaik.bacc.camilla.androidcredentialstore.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Camilla on 23.02.2018.
 */

@Entity
public class MasterPassword
{
    @Id(autoincrement = true)
    private Long masterPassword_id;

    @Property(nameInDb = "MASTERPASSWORD")
    private byte[] masterPassword;

    @Generated(hash = 308378705)
    public MasterPassword(Long masterPassword_id, byte[] masterPassword) {
        this.masterPassword_id = masterPassword_id;
        this.masterPassword = masterPassword;
    }

    @Generated(hash = 1953507699)
    public MasterPassword() {
    }

    public Long getMasterPassword_id() {
        return this.masterPassword_id;
    }

    public void setMasterPassword_id(Long masterPassword_id) {
        this.masterPassword_id = masterPassword_id;
    }

    public byte[] getMasterPassword() {
        return this.masterPassword;
    }

    public void setMasterPassword(byte[] masterPassword) {
        this.masterPassword = masterPassword;
    }
}
