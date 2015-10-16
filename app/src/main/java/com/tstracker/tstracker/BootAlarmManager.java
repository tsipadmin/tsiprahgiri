package com.tstracker.tstracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.widget.ImageButton;
import android.widget.Toast;

public class BootAlarmManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctxt, Intent arg1) {
        // TODO Auto-generated method stub
        try {
            DatabaseHelper dh = new DatabaseHelper(ctxt);
            SQLiteDatabase db;
            try {
                db = dh.getReadableDatabase();
                String[] columns = {DatabaseContracts.Settings.COLUMN_NAME_interval, DatabaseContracts.Settings.COLUMN_NAME_RunningAlarm};
                Cursor c = db.query(DatabaseContracts.Settings.TABLE_NAME, columns, "", null, "", "", "");
                c.moveToFirst();
                Tools.Interval = String.valueOf(c.getLong(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_interval)));
                long itemId = c.getLong(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_RunningAlarm));
                if (itemId == 1) {
                    AlarmManager mgr = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
                    Intent i = new Intent(ctxt, MyAlarmManager.class);
                    PendingIntent pi = PendingIntent.getBroadcast(ctxt, 0, i, 0);
                    mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 5000, pi);

                }
            }catch (Exception e) {
                Toast.makeText(ctxt, e.getMessage(), Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}