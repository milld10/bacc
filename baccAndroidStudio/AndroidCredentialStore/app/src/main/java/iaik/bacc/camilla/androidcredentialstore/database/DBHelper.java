package iaik.bacc.camilla.androidcredentialstore.database;


import java.util.List;

import iaik.bacc.camilla.androidcredentialstore.CredentialApplication;
import iaik.bacc.camilla.androidcredentialstore.models.Account;
import iaik.bacc.camilla.androidcredentialstore.models.AccountDao;
import iaik.bacc.camilla.androidcredentialstore.models.DaoSession;
import iaik.bacc.camilla.androidcredentialstore.models.MasterPassword;
import iaik.bacc.camilla.androidcredentialstore.models.MasterPasswordDao;

public class DBHelper
{
    private CredentialApplication application;
    private DaoSession daoSession;


    public DBHelper(CredentialApplication application)
    {
        this.application = application;
        this.daoSession = application.getDaoSession();
    }

    /** Add new Login (Application and Credentials for it) */
    public void insertNewAccount(Account account)
    {
        AccountDao accountDao = this.daoSession.getAccountDao();

        accountDao.insert(account);
    }

    public Account getAccountById(Long id)
    {
        AccountDao accountDao = this.daoSession.getAccountDao();

        return accountDao.load(id);
    }

    /** Deletes just one Credential out the List, which is held by the Account Obj. */
    public void deleteAccount(Account account)
    {
        AccountDao accountDao = this.daoSession.getAccountDao();
        accountDao.delete(account);
    }

    public void deleteAccountById(Long id)
    {
        AccountDao accountDao = this.daoSession.getAccountDao();
        Account account = accountDao.load(id);
        accountDao.delete(account);
    }

    /** Returns all listed accounts (for the Adapter); objects of Account */
    public List<Account> getAllAccounts()
    {
        AccountDao accountDao = this.daoSession.getAccountDao();

        List<Account> listOfAccounts = accountDao.queryBuilder().
                orderAsc(AccountDao.Properties.Account_name).list();

        return listOfAccounts;
    }
}