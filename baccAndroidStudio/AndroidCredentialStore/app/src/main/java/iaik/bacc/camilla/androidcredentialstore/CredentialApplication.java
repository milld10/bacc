package iaik.bacc.camilla.androidcredentialstore;
/**
 * Created by Camilla on 31.03.2017.
 */
import android.app.Application;
import android.util.Log;

import iaik.bacc.camilla.androidcredentialstore.models.DaoMaster;
import iaik.bacc.camilla.androidcredentialstore.models.DaoSession;

import org.greenrobot.greendao.database.Database;


public class CredentialApplication extends Application
{
    private static final String TAG = "CredentialApplication";

    public static final boolean ENCRYPTED = true;

    private DaoSession daoSession;

    private static CredentialApplication singleton;

    @Override
    public void onCreate()
    {
        super.onCreate();

        singleton = this;

        try {
            Log.d(TAG, "trying to open DB");
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"credentials-db");
//            Database db = ENCRYPTED ? encrypted : getWritableDb();
            Database db = helper.getWritableDb();
            daoSession = new DaoMaster(db).newSession();
            Log.d(TAG, "db successfully opened!");

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