package com.example.camilla.androidcredentialstore.database;


import java.util.List;

import com.example.camilla.androidcredentialstore.CredentialApplication;
import com.example.camilla.androidcredentialstore.models.AccountDao;
import com.example.camilla.androidcredentialstore.models.DaoSession;
import com.example.camilla.androidcredentialstore.models.Credential;
import com.example.camilla.androidcredentialstore.models.CredentialDao;
import com.example.camilla.androidcredentialstore.models.Account;



public class DBHelper
{
    private CredentialApplication application;
    private DaoSession daoSession;


    public DBHelper(CredentialApplication application)
    {
        this.application = application;
        this.daoSession = application.getDaoSession();
    }

    public long insertNewAppOfCredential(Account account, Credential credential)
    {
        /**
         * Add new Login (Application and Credentials for it)
         */

        AccountDao accountDao = this.daoSession.getAccountDao();
        CredentialDao credentialDao = this.daoSession.getCredentialDao();

        long id_app = accountDao.insert(account);
        long id_cred = credentialDao.insert(credential);

        for(Credential credentials : account.getCredentialList()){
            credentials.setCred_id(id_app);
            //this.insert(credentials);
        }

        return id_app;
    }

    public long insertNewCredential(Credential credential)
    {
        /**
         * Adds one Credential to the List, which is held by the Account Obj.
         */

        return 1;
    }

    public void deleteOneCredentialOfList(Credential credential)
    {
        /**
         * Deletes just one Credential out the List, which is held by the Account Obj.
         */

        CredentialDao credentialDao = this.daoSession.getCredentialDao();
        credentialDao.delete(credential);
    }

    public long deleteAccount(Account account)
    {
        /**
         * Deletes whole App of Credential, incl all Credentials of the list it holds
         */
        List<Credential> listToDelete = getListOfCredentials();

        AccountDao accountDao = this.daoSession.getAccountDao();
        accountDao.delete(account);

        return 1;
    }

    public List<Account> getAllAccounts()
    {
        /**
         * Returns all listed Apps (for the Adapter)
         */

        AccountDao accountDao = this.daoSession.getAccountDao();

        List<Account> listOfAccounts = accountDao.queryBuilder().
                orderDesc(AccountDao.Properties.Account_name).list();

        return listOfAccounts;
    }

    public List<Credential> getListOfCredentials()
    {
        /**
         * Get List with Credentials of one specific App of Credential
         */

        //TODO: iterate through list and delete all items?
        CredentialDao credentialDao = this.daoSession.getCredentialDao();

        List<Credential> credentials = credentialDao.queryBuilder().
                orderDesc(CredentialDao.Properties.Username).list();

        return credentials;
    }

    public void updateOneCredential(Credential credential)
    {
        /**
         * Update one credential upon changes (and saves it back again to DB?)
         */
    }

    public void updateAccount(Account account)
    {
        /**
         * Update account upon changes (and saves it back again to DB?)
         */

    }
}
