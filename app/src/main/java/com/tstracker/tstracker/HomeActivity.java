package com.tstracker.tstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class HomeActivity extends AppCompatActivity {
    AlarmManager mgr;
    DatabaseHelper dh;
    SQLiteDatabase db;
    static Intent intent2;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.CheckGps(this);
    }

    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;

    private void makeAlarmmanager() {
        try {
            //runservice
            if (intent2 == null)
                intent2 = new Intent(this, LocationService.class);
            // هر بار که سرویس را استارت میکنی از روی کلاس یک بار ساخته میشود
            this.startService(intent2);

            mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            if (!isRunning) {
                Tools.context = this;
                Intent i = new Intent(getApplicationContext(), MyAlarmManager.class);
                pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
                mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), Long.valueOf(Tools.Interval), pi);
                Toast.makeText(getApplicationContext(), "سرویس شروع به کار کرد!", Toast.LENGTH_SHORT).show();
                ((ImageButton) findViewById(R.id.ibtnRun)).setBackground(ContextCompat.getDrawable(this, R.drawable.stop));
                isRunning = true;

                dh = new DatabaseHelper(this);
                db = dh.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DatabaseContracts.Settings.COLUMN_NAME_RunningAlarm, 1);
                String selection = DatabaseContracts.Settings.COLUMN_NAME_ID + " = ?";
                String[] selectionArgs = {String.valueOf(1)};
                db.update(
                        DatabaseContracts.Settings.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                db.close();
                dh.close();
                dh = null;

            } else {
//                mgr.cancel(pi);
//                Toast.makeText(getApplicationContext(), "سرویس متوقف شد.", Toast.LENGTH_SHORT).show();
//                ((ImageButton)findViewById(R.id.ibtnRun)).setBackground(ContextCompat.getDrawable(this, R.drawable.start));
//            isRunning=false;
//                Tools.backeagroundServiceRunning=false;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_RECOVER_PLAY_SERVICES) {

            if (resultCode == RESULT_OK) {
                makeAlarmmanager();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Google Play Services must be installed.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean checkGooglePlayServices() {
        int checkGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
		/*
		* Google Play Services is missing or update is required
		*  return code could be
		* SUCCESS,
		* SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED,
		* SERVICE_DISABLED, SERVICE_INVALID.
		*/
            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices,
                    this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);


        dh = new DatabaseHelper(getApplicationContext());
        try {
            db = dh.getReadableDatabase();
            String[] columns = {DatabaseContracts.Settings.COLUMN_NAME_RunningAlarm, DatabaseContracts.Settings.COLUMN_NAME_Accurate};
            Cursor c = db.query(DatabaseContracts.Settings.TABLE_NAME, columns, "", null, "", "", "");
            c.moveToFirst();
            long itemId = 0;
            itemId = c.getLong(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_RunningAlarm));
            if (itemId == 1) {
                ((ImageButton) findViewById(R.id.ibtnRun)).setBackground(ContextCompat.getDrawable(this, R.drawable.stop));
                isRunning = true;
                Tools.context=getApplicationContext();
                if (!MyAlarmManager.isMyServiceRunning(LocationService.class))
                    makeAlarmmanager();
            }
            Tools.lastAccurate = Tools.curAccurate = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_Accurate));
            c.close();
            db.close();
        } catch (Exception er) {
//Toast.makeText(this,er.getMessage(),Toast.LENGTH_LONG).Show();
        }
        dh.close();
        dh = null;
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
        if (checkGooglePlayServices()) {
            makeAlarmmanager();
        }

    }

    public void btnMap_Click(View view) {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }

    public void NewsClick(View view) {
        Toast.makeText(this, "غیر فعال است!", Toast.LENGTH_LONG).show();
    }

    public void AboutClick(View view) {
        Toast.makeText(this, "غیر فعال است!", Toast.LENGTH_LONG).show();
    }

}