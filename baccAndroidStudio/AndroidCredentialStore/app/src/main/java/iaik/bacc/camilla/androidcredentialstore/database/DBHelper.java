package iaik.bacc.camilla.androidcredentialstore.database;


import org.greenrobot.greendao.annotation.Convert;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import iaik.bacc.camilla.androidcredentialstore.CredentialApplication;
import iaik.bacc.camilla.androidcredentialstore.models.Account;
import iaik.bacc.camilla.androidcredentialstore.models.AccountDao;
import iaik.bacc.camilla.androidcredentialstore.models.DaoSession;
import iaik.bacc.camilla.androidcredentialstore.tools.Converter;


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
    public long insertNewAccount(Account account)
    {
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


    /** Returns all listed Apps (for the Adapter); objects of Account */
    public List<Account> getAllAccounts()
    {
        //TODO: convert them into strings before putting them into list.
        AccountDao accountDao = this.daoSession.getAccountDao();

        List<Account> listOfAccounts = accountDao.queryBuilder().
                orderAsc(AccountDao.Properties.Account_name).list();

        return listOfAccounts;
    }


//    public List<String> getAllAccountStrings() throws UnsupportedEncodingException {
//        //TODO: convert them into strings before putting them into list.
//        AccountDao accountDao = this.daoSession.getAccountDao();
//
//        List<Account> listOfAccounts = accountDao.queryBuilder().
//                orderAsc(AccountDao.Properties.Account_name).list();
//
//
//        List<String> strings = new ArrayList<>();
//
//        Iterator<Account> iterator = listOfAccounts.iterator();
//        while(iterator.hasNext()) {
//            Account item = iterator.next();
//
//            strings.add(Converter.byteToString(item.getAccount_name()));
//        }
//
//        return strings;
//    }

    /**TODO: Update account upon changes (and saves it back again to DB?) */
    public void updateAccount(Account account)
    {

    }

}