package com.tstracker.tstracker;


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
/**
 * Created by ali on 9/27/15.
 */
public class Tools {
    public Tools(){

    }

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
    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        return true;
    }
    else{
        return false;
    }

}public  static  boolean IsNetworProviderEnabled(Context contxt){
        final LocationManager manager = (LocationManager) contxt.getSystemService(contxt.LOCATION_SERVICE);
       return manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    public  static void CheckGps(Context contxt) {
        boolean buildAlertMessageNoGps=GpsState(contxt);
        final Context con = contxt;
        if (buildAlertMessageNoGps) {
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
