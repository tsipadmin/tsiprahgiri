package com.tstracker.tstracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.content.ContentValues;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {

    DatabaseHelper dh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        dh = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db;
        try {
            db = dh.getReadableDatabase();
            String[] columns = {DatabaseContracts.Settings.COLUMN_NAME_days,
                    DatabaseContracts.Settings.COLUMN_NAME_fromTime,
                    DatabaseContracts.Settings.COLUMN_NAME_endTime,
                    DatabaseContracts.Settings.COLUMN_NAME_interval};
            Cursor c = db.query(DatabaseContracts.Settings.TABLE_NAME, columns, "", null, "", "", "");
            c.moveToFirst();
            String val = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_days));

            if (val.contains("0"))
                ((CheckBox) findViewById(R.id.chkShabne)).setChecked(true);
            if (val.contains("1"))
                ((CheckBox) findViewById(R.id.chkYekShabne)).setChecked(true);
            if (val.contains("2"))
                ((CheckBox) findViewById(R.id.chkDoShabne)).setChecked(true);
            if (val.contains("3"))
                ((CheckBox) findViewById(R.id.chkSeShabne)).setChecked(true);
            if (val.contains("4"))
                ((CheckBox) findViewById(R.id.chkCharShabne)).setChecked(true);
            if (val.contains("5"))
                ((CheckBox) findViewById(R.id.chkPanjShabne)).setChecked(true);
            if (val.contains("6"))
                ((CheckBox) findViewById(R.id.chkJomeh)).setChecked(true);

             val = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_endTime));
            ((EditText)findViewById(R.id.edittextTaSaat)).setText(val);
            val = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_fromTime));
            ((EditText)findViewById(R.id.edittextAzSaat)).setText(val);
            val = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_interval));
            val=String.valueOf(Integer.valueOf(val)/1000);
                    ((EditText) findViewById(R.id.edittextSendServerTime)).setText(val);
        } catch (Exception er) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_setting, menu);
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
//
     return super.onOptionsItemSelected(item);
    }

    public  void btnREfresh(View view) {
    GetDetails();
    }

    static String urlConfig = "http://tstracker.ir/services/webbasedefineservice.asmx/GetMobileConfig";
    public void GetDetails() {
        Map<String, String> params = new HashMap<>();
        // the POST parameters
        params.put("IMEI", Tools.IMEI);// "351520060796671");
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, urlConfig,
                new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String data = response.getString("d");
                    if (data.length() > 1) {
                        Tools.days = "";
                        String[] params = data.split(",");
                        if (params[0].contains("1"))
                            Tools.days += "0,";
                        if (params[1].contains("1"))
                            Tools.days += "1,";
                        if (params[2].contains("1"))
                            Tools.days += "2,";
                        if (params[3].contains("1"))
                            Tools.days += "3,";
                        if (params[4].contains("1"))
                            Tools.days += "4,";
                        if (params[5].contains("1"))
                            Tools.days += "5,";
                        if (params[6].contains("1"))
                            Tools.days += "6";
                        Tools.startTime = params[7];
                        Tools.EndTime = params[8];
                        Tools.Interval = String.valueOf(Integer.valueOf(params[9]) * 1000);
                        SQLiteDatabase db = dh.getReadableDatabase();
// New value for one column
                        ContentValues values = new ContentValues();
                        values.put(DatabaseContracts.Settings.COLUMN_NAME_days, Tools.days);
                        values.put(DatabaseContracts.Settings.COLUMN_NAME_fromTime, Tools.startTime);
                        values.put(DatabaseContracts.Settings.COLUMN_NAME_endTime, Tools.EndTime);
                        values.put(DatabaseContracts.Settings.COLUMN_NAME_interval, Tools.Interval);

// Which row to update, based on the ID
                        String selection = DatabaseContracts.Settings.COLUMN_NAME_ID + " = ?";
                        String[] selectionArgs = {String.valueOf(1)};

                        int count = db.update(
                                DatabaseContracts.Settings.TABLE_NAME,
                                values,
                                selection,
                                selectionArgs);
                        String val = Tools.days;

                        if (val.contains("0"))
                            ((CheckBox) findViewById(R.id.chkShabne)).setChecked(true);
                        else ((CheckBox) findViewById(R.id.chkShabne)).setChecked(false);
                        if (val.contains("1"))
                            ((CheckBox) findViewById(R.id.chkYekShabne)).setChecked(true);
                        else ((CheckBox) findViewById(R.id.chkYekShabne)).setChecked(false);
                        if (val.contains("2"))
                            ((CheckBox) findViewById(R.id.chkDoShabne)).setChecked(true);
                        else ((CheckBox) findViewById(R.id.chkDoShabne)).setChecked(false);
                        if (val.contains("3"))
                            ((CheckBox) findViewById(R.id.chkSeShabne)).setChecked(true);
                        else ((CheckBox) findViewById(R.id.chkSeShabne)).setChecked(false);
                        if (val.contains("4"))
                            ((CheckBox) findViewById(R.id.chkCharShabne)).setChecked(true);
                        else ((CheckBox) findViewById(R.id.chkCharShabne)).setChecked(false);
                        if (val.contains("5"))
                            ((CheckBox) findViewById(R.id.chkPanjShabne)).setChecked(true);
                        else ((CheckBox) findViewById(R.id.chkPanjShabne)).setChecked(false);
                        if (val.contains("6"))
                            ((CheckBox) findViewById(R.id.chkJomeh)).setChecked(true);
                        else ((CheckBox) findViewById(R.id.chkJomeh)).setChecked(false);

                        val = Tools.EndTime;
                        ((EditText) findViewById(R.id.edittextTaSaat)).setText(val);
                        val = Tools.startTime;
                        ((EditText) findViewById(R.id.edittextAzSaat)).setText(val);
                        val = Tools.Interval;
                        val = String.valueOf(Integer.valueOf(val) / 1000);
                        ((EditText) findViewById(R.id.edittextSendServerTime)).setText(val);
                        Toast.makeText(getApplicationContext(), "اطلاعات ثبت شد.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception er) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(jsObjRequest);
    }


    public  void button_Click(View view){
        String days="",fromTime,endTime,interval;

        if(((CheckBox) findViewById(R.id.chkShabne)).isChecked())
            days+="0,";
        if(((CheckBox) findViewById(R.id.chkYekShabne)).isChecked())
            days+="1,";
        if(((CheckBox) findViewById(R.id.chkDoShabne)).isChecked())
            days+="2,";
        if(((CheckBox) findViewById(R.id.chkSeShabne)).isChecked())
            days+="3,";
        if(((CheckBox) findViewById(R.id.chkCharShabne)).isChecked())
            days+="4,";
        if(((CheckBox) findViewById(R.id.chkPanjShabne)).isChecked())
            days+="5,";
        if(((CheckBox) findViewById(R.id.chkJomeh)).isChecked())
            days+="6";
        fromTime=((EditText)findViewById(R.id.edittextAzSaat)).getText().toString();
        endTime=((EditText)findViewById(R.id.edittextTaSaat)).getText().toString();
        interval=String.valueOf(Integer.valueOf(((EditText) findViewById(R.id.edittextSendServerTime)).getText().toString())*1000);

        SQLiteDatabase db = dh.getReadableDatabase();
// New value for one column
        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.Settings.COLUMN_NAME_days, days);
        values.put(DatabaseContracts.Settings.COLUMN_NAME_fromTime, fromTime);
        values.put(DatabaseContracts.Settings.COLUMN_NAME_endTime, endTime);
        values.put(DatabaseContracts.Settings.COLUMN_NAME_interval, interval);

// Which row to update, based on the ID
        String selection = DatabaseContracts.Settings.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = { String.valueOf(1) };

        int count = db.update(
                DatabaseContracts.Settings.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Tools.days=days;
        Tools.EndTime=endTime;
        Tools.startTime=fromTime;
        Tools.Interval=interval;
        Toast.makeText(this,"اطلاعات ثبت شد.",Toast.LENGTH_LONG).show();
    }
}
