package com.example.camilla.androidcredentialstore.database;


import android.util.Log;
import java.util.List;

import com.example.camilla.androidcredentialstore.CredentialApplication;
import com.example.camilla.androidcredentialstore.models.DaoMaster;
import com.example.camilla.androidcredentialstore.models.DaoSession;
import com.example.camilla.androidcredentialstore.models.Credential;
import com.example.camilla.androidcredentialstore.models.CredentialDao;
import com.example.camilla.androidcredentialstore.models.AppOfCredential;
import com.example.camilla.androidcredentialstore.models.AppOfCredentialDao;



public class DBHelper
{
    private CredentialApplication application;
    private DaoSession daoSession;


    public DBHelper(CredentialApplication application)
    {
        this.application = application;
        this.daoSession = application.getDaoSession();
    }

    public long insertNewAppOfCredential(AppOfCredential appOfCredential, Credential credential)
    {
        /**
         * Add new Login (Application and Credentials for it)
         */

        AppOfCredentialDao appOfCredentialDao = this.daoSession.getAppOfCredentialDao();
        CredentialDao credentialDao = this.daoSession.getCredentialDao();

        long id_app = appOfCredentialDao.insert(appOfCredential);
        long id_cred = credentialDao.insert(credential);

        for(Credential credentials : appOfCredential.getCredentialList()){
            credentials.setCred_id(id_app);
            //this.insert(credentials);
        }

        return id_app;
    }

    public long insertNewCredential(Credential credential)
    {
        /**
         * Adds one Credential to the List, which is held by the AppOfCredential Obj.
         */

        return 1;
    }

    public void deleteOneCredentialOfList(Credential credential)
    {
        /**
         * Deletes just one Credential out the List, which is held by the AppOfCredential Obj.
         */

        CredentialDao credentialDao = this.daoSession.getCredentialDao();
        credentialDao.delete(credential);
    }

    public long deleteAppOfCredential(AppOfCredential appOfCredential)
    {
        /**
         * Deletes whole App of Credential, incl all Credentials of the list it holds
         */
        List<Credential> listToDelete = getListOfCredentials();

        AppOfCredentialDao appOfCredentialDao = this.daoSession.getAppOfCredentialDao();
        appOfCredentialDao.delete(appOfCredential);

        return 1;
    }

    public long getAllAppsOfCredential(AppOfCredential appOfCredential)
    {
        /**
         * Returns all listed Apps (for the Adapter)
         */

        return 1;
    }

    public List<Credential> getListOfCredentials()
    {
        /**
         * Get List with Credentials of one specific App of Credential
         */

        //TODO: iterate through list and delete all items?
        CredentialDao credentialDao = this.daoSession.getCredentialDao();

        List<Credential> credentials = credentialDao.queryBuilder().orderDesc(CredentialDao.
                Properties.Username).list();

        return credentials;
    }

    public long updateOneCredential(Credential credential)
    {
        /**
         * Update one credential upon changes (and saves it back again to DB?)
         */

        return 1;
    }
}
