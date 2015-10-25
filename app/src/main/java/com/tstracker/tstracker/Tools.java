package com.tstracker.tstracker;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.Geocoder;
import android.location.Address;
import android.location.LocationManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.telephony.TelephonyManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import java.io.IOException;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ali on 9/27/15.
 */
public class Tools {
    public Tools(){

    }
    public static Context context;
    public static  String curAccurate,lastAccurate;
    public  static  Location currentLocation;
    public  static  String days;
    public  static  String startTime;
    public  static  String EndTime;
    public  static  String Interval;
    public  static  String SiteUrl;
    public  static  String IMEI;
    public  static  String key;
    public  static  boolean LocationServiceRunning=false;
    public  static String GetImei(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        IMEI= tm.getDeviceId();
        return IMEI;
    }
public  static  boolean GpsState(Context contxt){
    final LocationManager manager = (LocationManager) contxt.getSystemService(contxt.LOCATION_SERVICE);
    if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        return true;
    }
    else{
        return false;
    }

}
    public  static  boolean IsNetworProviderEnabled(Context contxt){
        final LocationManager manager = (LocationManager) contxt.getSystemService(contxt.LOCATION_SERVICE);
       return manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    public  static void CheckGps(Context contxt) {
        boolean buildAlertMessageNoGps=GpsState(contxt);
        final Context con = contxt;
        if (!buildAlertMessageNoGps) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(con);
            builder.setMessage("برنامه برای ارسال اطلاعات نیاز دارد تا موقعیت مکانی شما روشن باشد.")
                    .setCancelable(false)
                    .setPositiveButton("روشن کردن", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            con.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("خاموش بماند", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            try {
                alert.show();
            } catch (Exception ex) {
                String s = ex.getMessage();
            }
        }
    }


    private static String IsTimeToSend(long TimeMilliseconds ) {
        DatabaseHelper dh = new DatabaseHelper(Tools.context);
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
                Date date;
                if(TimeMilliseconds ==0)
                    date = cal.getTime();
                else
                    date = new Date(TimeMilliseconds);
                String datetime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date);
                //(String) DateFormat.format("yyyy-MM-dd HH:mm:ss",c );
                Integer dayWeek = cal.get(Calendar.DAY_OF_WEEK);
                int hourOfDay = Integer.valueOf(new SimpleDateFormat("k", Locale.US).format(date));

                if (dayWeek == 7)//shanbe
                    dayWeek = 0;
                if (days.contains(String.valueOf(dayWeek)))//check day
                    if (hourOfDay < Integer.valueOf(EndTime) && hourOfDay >= Integer.valueOf(startTime))//check hour
                    {
                        c.close();
                        db.close();
                        dh.close();
                        return datetime;
                    }
            } catch (Exception er) {
                // txtResult.setText(er.getMessage());
            }
            c.close();
            db.close();
        } catch (Exception ex) {

        }
        dh.close();
        return null;
    }

    public  static   void SaveLocation(Location location,int _corase,int _speed) {

        currentLocation = location;
        String Lat, Lon, alti, speed, coarse, datetime;
        try {
            Lat = String.valueOf(location.getLatitude());
            Lon = String.valueOf(location.getLongitude());
            alti = String.valueOf(location.getAltitude());
            speed = String.valueOf(_speed);
            coarse = String.valueOf(_corase);
            if (coarse == "NaN")
                coarse = "0";
            datetime = IsTimeToSend(location.getTime());
            if (datetime != null) {

                SaveGps s = new SaveGps(Tools.context, Lat, Lon, alti, speed, coarse, datetime);
//              Toast.makeText(getApplicationContext(),datetime, Toast.LENGTH_LONG).show();
            }
        } catch (Exception er) {
            String g = "";
        }
        Lat = Lon = alti = speed = coarse = datetime = null;
    }
    public static  String GetAddress(double latitude,double longitude){
        String result="";
        Geocoder geocoder = new Geocoder(context, new Locale("Fa"));
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                sb.append(address.getLocality()).append("\n");
                sb.append(address.getPostalCode()).append("\n");
                sb.append(address.getCountryName());
                result = sb.toString();
            }
        }
        catch (IOException e) {
            result= "Unable connect to Geocoder";
        }
        return result;
    }

    public static class NotificationClass {
        public static void Notificationm(Context context,String Title,String Details,String packge){
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.notification_icon)
                            .setContentTitle(Title)
                            .setContentText(Details);
// Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(context, HomeActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(HomeActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
            mNotificationManager.notify(0, mBuilder.build());
        }
    }

}
