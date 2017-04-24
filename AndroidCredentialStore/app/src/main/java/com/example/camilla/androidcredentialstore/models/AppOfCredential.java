package com.example.camilla.androidcredentialstore.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by Camilla on 24.04.2017.
 */




@Entity(
        active = true,
        nameInDb = "APPS"
)
public class AppOfCredential implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private long app_id;

    //former website
    @Property(nameInDb = "ACCOUNT_NAME")
    private String account_name;

    @ToMany(referencedJoinProperty = "applicationId")
    @OrderBy("username ASC")
    private List<Credential> credentialList;

/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;

/** Used for active entity operations. */
@Generated(hash = 1561407162)
private transient AppOfCredentialDao myDao;

@Generated(hash = 1296978024)
public AppOfCredential(long app_id, String account_name) {
    this.app_id = app_id;
    this.account_name = account_name;
}

@Generated(hash = 227680436)
public AppOfCredential() {
}

public long getApp_id() {
    return this.app_id;
}

public void setApp_id(long app_id) {
    this.app_id = app_id;
}


@Override
public String toString()
{
    return account_name;
}

/**
 * To-many relationship, resolved on first access (and after reset).
 * Changes to to-many relations are not persisted, make changes to the target entity.
 */
@Generated(hash = 1980609164)
public List<Credential> getCredentialList() {
    if (credentialList == null) {
        final DaoSession daoSession = this.daoSession;
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        CredentialDao targetDao = daoSession.getCredentialDao();
        List<Credential> credentialListNew = targetDao
                ._queryAppOfCredential_CredentialList(app_id);
        synchronized (this) {
            if (credentialList == null) {
                credentialList = credentialListNew;
            }
        }
    }
    return credentialList;
}

/** Resets a to-many relationship, making the next get call to query for a fresh result. */
@Generated(hash = 1413351291)
public synchronized void resetCredentialList() {
    credentialList = null;
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

public String getAccount_name() {
    return this.account_name;
}

public void setAccount_name(String account_name) {
    this.account_name = account_name;
}

/** called by internal mechanisms, do not call yourself. */
@Generated(hash = 429416656)
public void __setDaoSession(DaoSession daoSession) {
    this.daoSession = daoSession;
    myDao = daoSession != null ? daoSession.getAppOfCredentialDao() : null;
}

    //evtl noch type dazufügen?

}
