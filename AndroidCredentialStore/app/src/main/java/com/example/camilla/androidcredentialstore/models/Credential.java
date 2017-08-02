package com.example.camilla.androidcredentialstore.models;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by Camilla on 13.03.2017.
 */

@Entity(
        active =  true,
        nameInDb = "CREDENTIALS"
)
public class Credential implements java.io.Serializable
{
    //used for error of serialization
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private long cred_id;
    private String username;
    private byte[] password;
    private long accountId;

/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;

/** Used for active entity operations. */
@Generated(hash = 1832334603)
private transient CredentialDao myDao;

@Generated(hash = 843430051)
public Credential(long cred_id, String username, byte[] password, long accountId)
{
    this.cred_id = cred_id;
    this.username = username;
    this.password = password;
    this.accountId = accountId;
}

@Generated(hash = 943805485)
public Credential() {
}
public long getCred_id() {
    return this.cred_id;
}
public void setCred_id(long cred_id) {
    this.cred_id = cred_id;
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
public long getAccountId() {
    return this.accountId;
}
public void setAccountId(long accountId) {
    this.accountId = accountId;
}

@Override
public String toString()
{
    return username;
}


/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 128553479)
public void delete() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.delete(this);
}
/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 1942392019)
public void refresh() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.refresh(this);
}
/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 713229351)
public void update() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.update(this);
}
/** called by internal mechanisms, do not call yourself. */
@Generated(hash = 1895121706)
public void __setDaoSession(DaoSession daoSession) {
    this.daoSession = daoSession;
    myDao = daoSession != null ? daoSession.getCredentialDao() : null;
}

}
