package com.tstracker.tstracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.os.BatteryManager;
import android.database.sqlite.SQLiteDatabase;
/**
 * Created by ali on 9/27/15.
 */
public class SaveGps {

    DatabaseHelper dbh;
    SQLiteDatabase db;
    private Context CuContext;
    private static String CuBatteryCharge;
String _Address="";
    String data;
    public SaveGps(Context context, String Lat, String Lon, String alti, String speed, String curse, String datetime) {
        this.CuContext = context;

        CuContext.getApplicationContext().registerReceiver(this.batteryInformationReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        try {

            dbh = new DatabaseHelper(CuContext);
            db = dbh.getWritableDatabase();
            _Address=Tools.GetAddress(Double.valueOf(Lat), Double.valueOf(Lon));
            data =  Lat + "," + Lon + "," + alti + "," + speed + "," + curse + "," + datetime + "," + CuBatteryCharge+","+_Address;
            ContentValues values = new ContentValues();
            values.put(DatabaseContracts.AVLData.COLUMN_NAME_Data, data);
// Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(DatabaseContracts.AVLData.TABLE_NAME, DatabaseContracts.AVLData.COLUMN_NAME_ID, values);
            db.close();
            dbh.close();
        } catch (Exception ioe) {
            throw new Error("Unable to create database");
        }

    }

    BroadcastReceiver batteryInformationReceiver= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            int  level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);

            CuBatteryCharge = String.valueOf(level);;
        }
    };
}
