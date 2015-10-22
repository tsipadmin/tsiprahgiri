package com.tstracker.tstracker;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

/**
 * Created by ali on 10/17/15.
 */
public class LocationService extends Service implements
        ConnectionCallbacks, OnConnectionFailedListener , com.google.android.gms.location.LocationListener{

   GoogleApiClient mGoogleApiClient;
    FusedLocationProviderApi fusedLocationProviderApi ;
   int  LOCATION_INTERVAL=3000;
    static LocationRequest locationRequest;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

CreateLocationRequest();
        fusedLocationProviderApi = LocationServices.FusedLocationApi;
    }
    public void CreateLocationRequest(){
            locationRequest = LocationRequest.create();

            locationRequest.setInterval(LOCATION_INTERVAL);
            locationRequest.setFastestInterval(LOCATION_INTERVAL);
            if(Tools.curAccurate==null) {
                DatabaseHelper dh = new DatabaseHelper(getApplicationContext());
                SQLiteDatabase db;
                db = dh.getReadableDatabase();
                String[] columns = {DatabaseContracts.Settings.COLUMN_NAME_Accurate};
                Cursor c = db.query(DatabaseContracts.Settings.TABLE_NAME, columns, "", null, "", "", "");
                c.moveToFirst();
                Tools.curAccurate=c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_Accurate));
            }
            if ( Tools.curAccurate.contains("l")) {
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            } else {
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            }
            Tools.lastAccurate = Tools.curAccurate;

    }

    @Override
    public void onConnectionSuspended(int s){

    }
    Location lastLocation;
    float coarse,speed,distance;
    long lasttime,curenttime;
    Calendar c;
    @Override
    public void onLocationChanged(Location location) {

        c = Calendar.getInstance();
        lasttime = 1;
         coarse = 0;
        curenttime=c.getTimeInMillis();
        speed=0;
        distance=-1;
        if (lastLocation != null) {
            coarse = (float)java.lang.Math.asin((location.getLongitude() - lastLocation.getLongitude()) / (location.getLatitude() - lastLocation.getLatitude()));
            distance=location.distanceTo(lastLocation);
        speed=distance/((curenttime-lasttime)/1000);//m/s
        } else {
            lastLocation = location;
        }
        lasttime=curenttime;
        String s = String.valueOf(speed) + "----" + String.valueOf(distance) + "-----" + String.valueOf(coarse);
        android.widget.Toast.makeText(getApplicationContext(), s, android.widget.Toast.LENGTH_LONG).show();

        if (speed>0.7|| distance> 2 || coarse > 5 || distance==-1) {
            location.setSpeed(speed);
            Tools.SaveLocation(location, coarse);
          lastLocation = location;
        }
        Tools.NotificationClass.Notificationm(getApplicationContext(), "رهگیری", "در حال ذخیره سازی اطلاعات مکانی شما برای ارسال به سرور.", "");
    }

    @Override
    public void onConnectionFailed(ConnectionResult cr){

    }
    @Override
    public void onConnected(Bundle connectionHint) {
       // mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient,  locationRequest,this);


    }
    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        if(Tools.curAccurate!= Tools.lastAccurate) {
            fusedLocationProviderApi.removeLocationUpdates(mGoogleApiClient, this);
            CreateLocationRequest();
            fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }


        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }
    @Override
    public void onCreate() {
buildGoogleApiClient();
        super.onCreate();
        mGoogleApiClient.connect();
    }

}
