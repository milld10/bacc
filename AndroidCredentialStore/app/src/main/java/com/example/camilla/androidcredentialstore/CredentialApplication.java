package com.example.camilla.androidcredentialstore;
/**
 * Created by Camilla on 31.03.2017.
 */
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.camilla.androidcredentialstore.models.DaoMaster;
import com.example.camilla.androidcredentialstore.models.DaoSession;

import org.greenrobot.greendao.database.Database;



public class CredentialApplication extends Application
{
    private static final String TAG = "APPLICATION";

    public static final boolean ENCRYPTED = true;

    private DaoSession daoSession;

    private static CredentialApplication singleton;

    @Override
    public void onCreate()
    {
        Log.w(TAG, "something");
        super.onCreate();

        singleton = this;

        try {
            Log.w(TAG, "something");
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"credentials-db");
            //Database db = ENCRYPTED ? //encrypted : getwritabledb();
            Database db = helper.getWritableDb();
            daoSession = new DaoMaster(db).newSession();

        } catch (Exception e) {
            Log.e(TAG, "Could not open database", e);
        }

    }

    public DaoSession getDaoSession()
    {
        return daoSession;
    }

    public static CredentialApplication getInstance()
    {
        return singleton;
    }
}
