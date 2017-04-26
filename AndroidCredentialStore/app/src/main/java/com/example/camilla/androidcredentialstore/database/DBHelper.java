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

    public long insert(AppOfCredential appOfCredential)
    {
        AppOfCredentialDao appOfCredentialDao = this.daoSession.getAppOfCredentialDao();
        long id = appOfCredentialDao.insert(appOfCredential);

        for(Credential credentials : appOfCredential.getCredentialList()){
            credentials.setCred_id(id);
            //this.insert(credentials);
        }

        return id;
    }
}
