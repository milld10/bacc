package com.example.camilla.androidcredentialstore.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.camilla.androidcredentialstore.models.Credential;

public class DBHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "credentials.db";
    //credentials table
    public static final String TABLE_NAME = "credentialsTable";

    //credentials table columns
    //columns: login_id, website, username, password
    public static final String COLUMN_LID = "id";
    public static final String COLUMN_WEBSITE = "website";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    private static final String[] COLUMNS =
            {COLUMN_LID, COLUMN_WEBSITE, COLUMN_USERNAME, COLUMN_PASSWORD};

    protected SQLiteDatabase database;
    private Context context;

    //Constructor
    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.e("MyApp", "onCreate invoked");

        //tables creation queries; maybe put it out of onCreate
        String CREATE_CREDENTIALS_TABLE = "CREATE TABLE " + TABLE_NAME + "( "
                + COLUMN_LID + " Integer PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_WEBSITE  + " TEXT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT);";

        //create table
        db.execSQL(CREATE_CREDENTIALS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //output für info
        Log.w(DBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    //***********************************

        public boolean insertLogin(Credential credential)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //needs to be changed to input values of user.
        contentValues.put(COLUMN_WEBSITE, credential.getWebsite());
        contentValues.put(COLUMN_USERNAME, credential.getUsername());
        //contentValues.put(COLUMN_PASSWORD, credential.getPassword());
        db.insert(TABLE_NAME, null, contentValues);

        return true;
    }


    //CHANGE TO WEBSITE
    public Cursor getLoginById(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where id="+id+"", null);
        return res;
    }

    public int getNumberOfRows()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    //TODO: delete entries based on id or website?
    public boolean deleteLogin(Credential credential)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //delete
        //db.delete

        return true;
    }

    //TODO: update Credential

}
