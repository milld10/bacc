package com.example.camilla.androidcredentialstore;
/**
 * Created by Camilla on 31.03.2017.
 */
import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import org.greenrobot.greendao.database.Database;

import com.example.camilla.androidcredentialstore.models.DaoMaster;
import com.example.camilla.androidcredentialstore.models.DaoSession;


public class CredentialApplication extends Application
{
    private static final String TAG = "APPLICATION";

    public static final boolean ENCRYPTED = true;

    private DaoSession daoSession;

    @Override
    public void onCreate()
    {
        super.onCreate();

        try {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"credentials-db", null);
            //Database db = ENCRYPTED ? //encrypted : getwritabledb();
            Database db = helper.getWritableDatabase();
            daoSession = new DaoMaster.newSession();
        } catch (Exception e) {
            Log.e(TAG, "Could not open database", e);
        }

    }

    public DaoSession getDaoSession()
    {
        return daoSession;
    }
}
