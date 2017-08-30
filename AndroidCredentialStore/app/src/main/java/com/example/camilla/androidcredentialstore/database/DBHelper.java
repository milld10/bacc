package com.example.camilla.androidcredentialstore.database;


import java.util.List;

import com.example.camilla.androidcredentialstore.CredentialApplication;
import com.example.camilla.androidcredentialstore.models.Account;
import com.example.camilla.androidcredentialstore.models.AccountDao;
import com.example.camilla.androidcredentialstore.models.DaoSession;


public class DBHelper
{
    private CredentialApplication application;
    private DaoSession daoSession;


    public DBHelper(CredentialApplication application)
    {
        this.application = application;
        this.daoSession = application.getDaoSession();
    }

    public long insertNewAccount(Account account)
    {
        /**
         * Add new Login (Application and Credentials for it)
         */

        AccountDao accountDao = this.daoSession.getAccountDao();
        long id_app = accountDao.insert(account);


        return id_app;
    }

    public Account getAccountById(Long id)
    {
        AccountDao accountDao = this.daoSession.getAccountDao();
        Account account = accountDao.load(id);

        return account;
    }


    public void deleteAccount(Account account)
    {
        /**
         * Deletes just one Credential out the List, which is held by the Account Obj.
         */

        AccountDao accountDao = this.daoSession.getAccountDao();
        accountDao.delete(account);
    }

    public void deleteAccountById(Long id)
    {
        AccountDao accountDao = this.daoSession.getAccountDao();
        Account account = accountDao.load(id);
        accountDao.delete(account);
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



    public void updateAccount(Account account)
    {
        /**
         * Update account upon changes (and saves it back again to DB?)
         */
    }
}
