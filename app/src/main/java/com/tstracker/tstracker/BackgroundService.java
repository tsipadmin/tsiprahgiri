//package com.tstracker.tstracker;
//
//
//import android.app.Service;
//import android.content.Intent;
//import android.widget.Toast;
//import android.location.LocationManager;
//import android.content.Context;
//import android.location.Location;
//import android.os.IBinder;
//import android.util.Log;
//import android.os.Bundle;
//
///**
// * Created by ali on 9/25/15.
// */
//public class BackgroundService extends Service {
//
//    public String TAG="GPSLog";
//    public static LocationManager mLocationManager = null;
//    private static final int LOCATION_INTERVAL = 1000;
//    private static final float LOCATION_DISTANCE = 0;
//
//    public BackgroundService() {
//
//    }
//
//    @Override
//    public IBinder onBind(Intent arg0)
//    {
//        return null;
//    }
//    @Override
//    public void onCreate() {
//       // LOCATION_INTERVAL=Integer.valueOf(Tools.Interval);
//        if (mLocationManager == null) {
//            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//
//            try {
//
//            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, new LocationListener(LocationManager.NETWORK_PROVIDER));
//            } catch (java.lang.SecurityException ex) {
//                Toast.makeText(context, "fail to request location update, ignore", Toast.LENGTH_LONG).show();
//            } catch (IllegalArgumentException ex) {
//                Toast.makeText(context, "network provider does not exist, " + ex.getMessage(), Toast.LENGTH_LONG).show();
//
//            }
//            try {
//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, new LocationListener(LocationManager.GPS_PROVIDER));
//            } catch (java.lang.SecurityException ex) {
//                Toast.makeText(context, "fail to request location update, ignore", Toast.LENGTH_LONG).show();
//            } catch (IllegalArgumentException ex) {
//                Toast.makeText(context, "GPS provider does not exist, " + ex.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }
//        super.onCreate();
//    }
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId)
//    {
//        Log.e(TAG, "onStartCommand");
//        super.onStartCommand(intent, flags, startId);
//
//        return START_STICKY;
//    }
//
//    public class LocationListener implements android.location.LocationListener {
//        Location mLastLocation;
//        public LocationListener(String provider)
//        {
//            Log.e(TAG, "LocationListener " + provider);
//            mLastLocation = new Location(provider);
//        }
//        @Override
//        public void onLocationChanged(Location location) {
//int h=0;
//        }
//
//        @Override
//        public void onProviderDisabled(String provider)
//        {
////            Toast.makeText( getApplicationContext(), "Gps غیر فعال شد", Toast.LENGTH_SHORT ).show();
//
//        }
//
//@Override
//        public void onProviderEnabled(String provider)
//        {
////            Toast.makeText( getApplicationContext(), "Gps فعال شد", Toast.LENGTH_SHORT).show();
//        }
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras)
//        {
//
//            Log.e(TAG, "onStatusChanged: " + provider);
//        }
//    }
//}
