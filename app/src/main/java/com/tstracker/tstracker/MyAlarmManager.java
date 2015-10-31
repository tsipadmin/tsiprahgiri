package com.tstracker.tstracker;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.os.IBinder;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MyAlarmManager extends BroadcastReceiver {
    Calendar c;
    int hour;
     static com.android.volley.RequestQueue queue;
    static boolean SendData;
    static String IDSend="";
    AsyncCallWS task;
    public static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) Tools.context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            // TODO Auto-generated method stub
            if (!Tools.LocationServiceRunning || !isMyServiceRunning(LocationService.class)) {
                Tools.context = context;
                Tools.LocationServiceRunning = true;
            }
            if(Tools.startTime == null || Tools.EndTime==null) {
                DatabaseHelper dh = new DatabaseHelper(context);
                SQLiteDatabase db;
                db = dh.getReadableDatabase();
                String[] columns = {DatabaseContracts.Settings.COLUMN_NAME_fromTime,DatabaseContracts.Settings.COLUMN_NAME_endTime};
                Cursor c = db.query(DatabaseContracts.Settings.TABLE_NAME, columns, "", null, "", "", "");
                c.moveToFirst();
                Tools.startTime = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_fromTime));
                Tools.EndTime = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_endTime));

                c.close();
                db.close();
                dh.close();
                dh = null;
            }
            if (task == null)
                task = new AsyncCallWS();
            task.execute();
            c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR);
            if (!Tools.GpsState(context)) {
                if (hour > Integer.valueOf(Tools.startTime)) {
                    Tools.NotificationClass.Notificationm(context, "رهگیری", "موقعیت مکانی شما خاموش است. لطفا روشن کنید.", "");
                }
            } else {
                if (hour > Integer.valueOf(Tools.EndTime)) {
                    Tools.NotificationClass.Notificationm(context, "رهگیری", "برنامه رهگیری به کار خود پایان داد. می توانید موقعیت را خاموش کنید.", "");
                }
            }
        } catch (Exception er) {

            Toast.makeText(context,er.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    static String url = "http://tstracker.ir/services/webbasedefineservice.asmx/SaveAvlMobile";


    //Async Method
    private class AsyncCallWS extends AsyncTask<String, Void, String> {

        public AsyncCallWS() {
        }
       @Override
        protected String doInBackground(String... parameters) {
           return  DoJob();
       }

        public String DoJob(){
            if(SendData)
                return "";
            SendData=true;
            String result = null;
            try {

                //android.widget.Toast.makeText(context," hi from run", android.widget.Toast.LENGTH_LONG).show();
                String Data  ="";
                DatabaseHelper dh = new DatabaseHelper(Tools.context);
                SQLiteDatabase db;
                try {
                    db = dh.getReadableDatabase();
                    String[] columns = {DatabaseContracts.AVLData.COLUMN_NAME_ID, DatabaseContracts.AVLData.COLUMN_NAME_Data};
                    Cursor c = db.query(DatabaseContracts.AVLData.TABLE_NAME, columns, "", null, "", "", "");
                    c.moveToFirst();
                    long itemId = 0;
                    try {
                        int Counter=0;
                        if(c.getCount() > 0)
                            while (true || Counter < 20) {
                                Counter++;
                                Data += c.getString(c.getColumnIndexOrThrow(DatabaseContracts.AVLData.COLUMN_NAME_Data)) + "#";
                                if(IDSend.length()>0)
                                    IDSend+=',';
                                IDSend +=c.getString(c.getColumnIndexOrThrow(DatabaseContracts.AVLData.COLUMN_NAME_ID));
                                if (c.isLast())
                                    break;
                                c.moveToNext();
                            }
                    } catch (Exception er) {
                        SendData = false;
                        return "";

                    }
                    c.close();
                    db.close();
                } catch (Exception er) {
                }
                dh.close();
                dh=null;
                if(Data.length()>1)
                    Data=Tools.GetImei(Tools.context)+"|"+Data;
                Map<String, String> params = new HashMap<>();
                // the POST parameters
                params.put("Data", Data);// "351520060796671");

                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,
                        new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String data = response.getString("d");
                            if (data.contains("1")) {
                                DatabaseHelper dh = new DatabaseHelper(Tools.context);
                                SQLiteDatabase db = dh.getReadableDatabase();
                                if(db.delete(DatabaseContracts.AVLData.TABLE_NAME, DatabaseContracts.AVLData.COLUMN_NAME_ID+ " in ("+IDSend+")", null)>0){
                                    IDSend="";
                                }
                                db.close();
                                dh.close();
                                dh=null;
                                SendData=false;
                            }
                        } catch (Exception er) {
                            SendData=false;
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(context,error.getMessage(),Toast.LENGTH_LONG).show();
//                        Tools.NotificationClass.Notificationm(context, "رهگیری", "");
                        SendData=false;
                    }
                });
                if (Data.length() > 1) {
                    if(queue == null)
                        queue = Volley.newRequestQueue(Tools.context);
                    queue.add(jsObjRequest);
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