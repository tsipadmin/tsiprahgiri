package com.tstracker.tstracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MyAlarmManager extends BroadcastReceiver {

    private String IsTimeToSend() {
        DatabaseHelper dh = new DatabaseHelper(BackgroundService.context);
        SQLiteDatabase db;
        try {
            db = dh.getReadableDatabase();
            String[] columns = {DatabaseContracts.Settings.COLUMN_NAME_endTime
                    , DatabaseContracts.Settings.COLUMN_NAME_fromTime
                    , DatabaseContracts.Settings.COLUMN_NAME_days};
            Cursor c = db.query(DatabaseContracts.Settings.TABLE_NAME, columns, "", null, "", "", "");
            c.moveToFirst();
            try {
                String days = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_days));
                String EndTime = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_endTime));
                String startTime = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_fromTime));

                Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();
                String datetime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date);
                //(String) DateFormat.format("yyyy-MM-dd HH:mm:ss",c );
                Integer dayWeek = cal.get(Calendar.DAY_OF_WEEK);
                int hourOfDay = Integer.valueOf(new SimpleDateFormat("k", Locale.US).format(date));

                if (dayWeek == 7)//shanbe
                    dayWeek = 0;
                if (days.contains(String.valueOf(dayWeek)))//check day
                    if (hourOfDay < Integer.valueOf(EndTime) && hourOfDay >= Integer.valueOf(startTime))//check hour
                    {
                        return datetime;
                    }
            } catch (Exception er) {
                // txtResult.setText(er.getMessage());
            }
        } catch (Exception ex) {

        }
        return null;
    }

    public   void SaveLocation(Location location){

        String Lat, Lon, alti, speed, coarse, datetime;
        try {
            Lat = String.valueOf(location.getLatitude());
            Lon = String.valueOf(location.getLongitude());
            alti = String.valueOf(location.getAltitude());
            speed = String.valueOf(location.getSpeed());
            coarse = "0";
            datetime = IsTimeToSend();

            if ( datetime != null  ) {
                SaveGps s = new SaveGps(BackgroundService.context, Lat, Lon, alti, speed, coarse, datetime);
//                    Toast.makeText(getApplicationContext(),datetime, Toast.LENGTH_LONG).show();
            }
        } catch (Exception er) {

        }
        Lat = Lon = alti = speed = coarse = datetime = null;
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        // TODO Auto-generated method stub
        if (!Tools.LocationServiceRunning) {
            BackgroundService.context = context;
            Intent intent2 = new Intent(context, BackgroundService.class);
            context.startService(intent2);
            Tools.LocationServiceRunning = true;
        }
if(BackgroundService.mLocationManager!=null) {
    Location l=BackgroundService.mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    if(l==null)
        l=BackgroundService.mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    SaveLocation(l);
}
        AsyncCallWS task = new AsyncCallWS(context);
        //  task.execute();
        task.DoJob();
    if(!Tools.GpsState(context))
        Tools.NotificationClass.Notificationm(context, "رهگیری","در حال ذخیره سازی اطلاعات مکانی شما برای ارسال به سرور.","");
    else
        Tools.NotificationClass.Notificationm(context, "رهگیری", "موقعیت مکانی شما خاموش است. لطفا روشن کنید.", "");
    }

    static String url = "http://tstracker.ir/services/webbasedefineservice.asmx/SaveAvlMobile";

    //Async Method
    private class AsyncCallWS extends AsyncTask<String, Void, String> {
        Context context;
        public AsyncCallWS() {
        }
        public AsyncCallWS(Context c) {
            this.context=c;
        }
       @Override
        protected String doInBackground(String... parameters) {
           return  DoJob();
       }

         public String DoJob(){
            String result = null;
            try {

                //android.widget.Toast.makeText(context," hi from run", android.widget.Toast.LENGTH_LONG).show();
                String Data  ="";
                DatabaseHelper dh = new DatabaseHelper(context);
                SQLiteDatabase db;
                try {
                    db = dh.getReadableDatabase();
                    String[] columns = {DatabaseContracts.AVLData.COLUMN_NAME_ID, DatabaseContracts.AVLData.COLUMN_NAME_Data};
                    Cursor c = db.query(DatabaseContracts.AVLData.TABLE_NAME, columns, "", null, "", "", "");
                    c.moveToFirst();
                    long itemId = 0;
                    try {
                        while (true) {
                            Data += c.getString(c.getColumnIndexOrThrow(DatabaseContracts.AVLData.COLUMN_NAME_Data)) + "#";
                            if (c.isLast())
                                break;
                            c.moveToNext();
                        }
                    } catch (Exception er) {
                    }
                } catch (Exception er) {
                }

                if(Data.length()>1)
                    Data=Tools.GetImei(context)+"|"+Data;
                Map<String, String> params = new HashMap<>();
                // the POST parameters
                params.put("Data", Data);// "351520060796671");
//                Toast.makeText(context,Data,Toast.LENGTH_LONG).show();

                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,
                        new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String data = response.getString("d");
                            if (data.contains("1")) {
                                DatabaseHelper dh = new DatabaseHelper(context);
                                SQLiteDatabase db = dh.getReadableDatabase();
                                if(db.delete(DatabaseContracts.AVLData.TABLE_NAME, "", null)>0){
                                }
                            }
                        } catch (Exception er) {

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(context,error.getMessage(),Toast.LENGTH_LONG).show();
//                        Tools.NotificationClass.Notificationm(context, "رهگیری", "");

                    }
                });
                if (Data.length() > 1) {

                    Volley.newRequestQueue(context).add(jsObjRequest);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

        }
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

}