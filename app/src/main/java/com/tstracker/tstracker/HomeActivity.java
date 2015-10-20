package com.tstracker.tstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.widget.ImageButton;
import android.widget.Toast;
import android.provider.Settings;

public class HomeActivity extends AppCompatActivity {

    DatabaseHelper dh;
    SQLiteDatabase db;
    @Override
    protected void onStart(){
super.onStart();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
try {
}
        catch (Exception er){
    int a=0;
}

        Tools.CheckGps(this);
        setContentView(R.layout.activity_home);
        //Check if already alarmmanager is running

        dh = new DatabaseHelper(getApplicationContext());

        try {
            db = dh.getReadableDatabase();
            String[] columns = {DatabaseContracts.Settings.COLUMN_NAME_RunningAlarm,DatabaseContracts.Settings.COLUMN_NAME_Accurate};
            Cursor c = db.query(DatabaseContracts.Settings.TABLE_NAME, columns, "", null, "", "", "");
            c.moveToFirst();
            long itemId = 0;
            itemId = c.getLong(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_RunningAlarm));
            if (itemId == 1) {
                ((ImageButton) findViewById(R.id.ibtnRun)).setBackground(ContextCompat.getDrawable(this, R.drawable.stop));
                AlarmManager  mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                isRunning = true;
               Intent i = new Intent(this, MyAlarmManager.class);
                PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_NO_CREATE);
                if (pi == null) {
                   pi= PendingIntent.getBroadcast(this, 0, i, 0);
                    mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), Integer.valueOf(Tools.Interval), pi);
                }
                else
                    mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), Integer.valueOf(Tools.Interval), pi);
            }
            Tools.lastAccurate = Tools.curAccurate=c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_Accurate));
        } catch (Exception er) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void settings_Click(View view) {
        Intent i = new Intent(this, SettingActivity.class);
        startActivity(i);
    }

    boolean isRunning = false;
    PendingIntent pi;
    public void RunClick(View view) {
        try
        {
            AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            if(!isRunning) {
                Tools.context = this;
                Intent i = new Intent(getApplicationContext(), MyAlarmManager.class);
                pi= PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
                mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), Long.valueOf(Tools.Interval), pi);
                Toast.makeText(getApplicationContext(), "سرویس شروع به کار کرد!", Toast.LENGTH_SHORT).show();
                ((ImageButton)findViewById(R.id.ibtnRun)).setBackground(ContextCompat.getDrawable(this, R.drawable.stop));
                isRunning=true;

                dh = new DatabaseHelper(this);
                db = dh.getWritableDatabase();
  ContentValues values = new ContentValues();
                values.put(DatabaseContracts.Settings.COLUMN_NAME_RunningAlarm, 1);
                String selection = DatabaseContracts.Settings.COLUMN_NAME_ID + " = ?";
                String[] selectionArgs = {String.valueOf(1)};
                int count = db.update(
                        DatabaseContracts.Settings.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);

            } else{
//                mgr.cancel(pi);
//                Toast.makeText(getApplicationContext(), "سرویس متوقف شد.", Toast.LENGTH_SHORT).show();
//                ((ImageButton)findViewById(R.id.ibtnRun)).setBackground(ContextCompat.getDrawable(this, R.drawable.start));
//            isRunning=false;
//                Tools.backeagroundServiceRunning=false;
            }
        }catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void btnMap_Click(View view) {
//        String url;
//        try {
//            if (Tools.SiteUrl.contains("http"))
//                url = Tools.SiteUrl;
//            else
//                url = "http://" + Tools.SiteUrl;
//            if (Tools.SiteUrl.length() < 1)
//                url = "http://www.tstracker.ir";
//        }
//        catch (Exception ex){
//            url = "http://www.tstracker.ir";
//        }
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
//        startActivity(browserIntent);

        Intent i = new Intent(this,  MapsActivity.class);
        startActivity(i);
    }

    public void NewsClick(View view) {
        Toast.makeText(this, "غیر فعال است!", Toast.LENGTH_LONG).show();
    }

    public void AboutClick(View view) {
        Toast.makeText(this, "غیر فعال است!", Toast.LENGTH_LONG).show();
    }

}