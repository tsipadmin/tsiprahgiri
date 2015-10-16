package com.tstracker.tstracker;

import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
/**
 * Created by ali on 9/26/15.
 */
public class DatabaseHelper extends android.database.sqlite.SQLiteOpenHelper {

    public static final String DATABASE_NAME = "tsTrackerDB.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContracts.Settings.SQL_CREATE_Table);
        db.execSQL(DatabaseContracts.AVLData.SQL_CREATE_Table);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DatabaseContracts.Settings.SQL_DELETE_Table);
        db.execSQL(DatabaseContracts.AVLData.SQL_DELETE_Table);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
