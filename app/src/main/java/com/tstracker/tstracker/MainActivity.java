package com.tstracker.tstracker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.content.ContentValues;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView txtImei,txtResult;
    String imei,url,key,logo,site,tell;
    boolean RegisteredIMEI;
    DatabaseHelper dh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtImei = (TextView) findViewById(R.id.txtImei);
        txtResult = (TextView) findViewById(R.id.txtvDeviceRegistrationState);
        imei = Tools.GetImei(this);
        txtImei.setText("IMEI: " + imei);
        RegisteredIMEI = false;
        dh = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db;
        try {
            db = dh.getReadableDatabase();
            String[] columns = {DatabaseContracts.Settings.COLUMN_NAME_ID,
                    DatabaseContracts.Settings.COLUMN_NAME_interval
                    , DatabaseContracts.Settings.COLUMN_NAME_endTime
                    , DatabaseContracts.Settings.COLUMN_NAME_fromTime
                    , DatabaseContracts.Settings.COLUMN_NAME_days
                    , DatabaseContracts.Settings.COLUMN_NAME_key};
            Cursor c = db.query(DatabaseContracts.Settings.TABLE_NAME, columns, "", null, "", "", "");
            c.moveToFirst();
            long itemId = 0;
            try {
                itemId = c.getLong(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_ID));
                Tools.Interval = String.valueOf(c.getLong(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_interval)));
                Tools.days = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_days));
                Tools.EndTime = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_endTime));
                Tools.startTime = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_fromTime));
               // Tools.SiteUrl = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_site));
                Tools.key = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_key));
                if (Tools.key.length() > 1) {
                    //IMEI/key
                    imei = imei + "/" + Tools.key;
                }
                else
                {
                    imei = imei + "/0";
                }
            } catch (Exception er) {
                // txtResult.setText(er.getMessage());
                imei = imei + "/0";
            }
            if (itemId < 1) {
                mHandler.sendEmptyMessageDelayed(DISPLAY_DATA, 5000);
            } else {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
               this.finish();
            }
        } catch (android.database.sqlite.SQLiteException er) {
            // txtResult.setText(er.getMessage());
        }
    }

    private static final int DISPLAY_DATA = 1;
    // this handler will receive a delayed message
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // Do task here
            if (msg.what == DISPLAY_DATA) {
                url = "http://tstracker.ir/services/webbasedefineservice.asmx/CheckRegistration";
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("pData",imei);// "351520060796671");
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,
                        new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String data = response.getString("d");
                            key = new JSONObject(data).getString("key");
                            logo = new JSONObject(data).getString("logo");
                            site = new JSONObject(data).getString("site");
                            tell = new JSONObject(data).getString("tell");

                            SQLiteDatabase db = dh.getWritableDatabase();
                            // Create a new map of values, where column names are the keys
                            ContentValues values = new ContentValues();
                            values.put(DatabaseContracts.Settings.COLUMN_NAME_ID, 1);
                            values.put(DatabaseContracts.Settings.COLUMN_NAME_key, key);
                            values.put(DatabaseContracts.Settings.COLUMN_NAME_days, "0,1,2,3,4,5,6");
                            values.put(DatabaseContracts.Settings.COLUMN_NAME_endTime, "14");
                            values.put(DatabaseContracts.Settings.COLUMN_NAME_fromTime, "07");
                            values.put(DatabaseContracts.Settings.COLUMN_NAME_logo, logo);
                            values.put(DatabaseContracts.Settings.COLUMN_NAME_site, site);
                            values.put(DatabaseContracts.Settings.COLUMN_NAME_tell, tell);
                            values.put(DatabaseContracts.Settings.COLUMN_NAME_Accurate, "h");
                            values.put(DatabaseContracts.Settings.COLUMN_NAME_interval, 5000);
                            // Insert the new row, returning the primary key value of the new row
                            long newRowId;
                            newRowId = db.insert(DatabaseContracts.Settings.TABLE_NAME, "", values);
                            if (newRowId > 0) {
                                RegisteredIMEI = true;
                                Tools.Interval="5000";
                                Tools.days= "0,1,2,3,4,5,6";
                                Tools.startTime="07";
                                Tools.EndTime="14";
                                Tools.curAccurate="h";
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                txtResult.setText("");
                                if(key==null)
                                    RegisteredIMEI=false;
                            }
                        } catch (Exception er) {
                            key = er.getMessage();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                        txtResult.setText("دستگاه در سرور ثبت نشده است.");

                    }
                });
                Volley.newRequestQueue(getApplicationContext()).add(jsObjRequest);
                if (!RegisteredIMEI)
                    mHandler.sendEmptyMessageDelayed(DISPLAY_DATA, 5000);

            }
        }
    };


    private static final int DISPLAY_DATA2 = 1;
    // this handler will receive a delayed message
    private Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       // int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
          //  return true;
        //}

       return super.onOptionsItemSelected(item);
    }
}
