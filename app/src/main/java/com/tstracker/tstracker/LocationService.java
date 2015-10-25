package com.tstracker.tstracker;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

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
public class LocationService extends IntentService implements
        ConnectionCallbacks, OnConnectionFailedListener , com.google.android.gms.location.LocationListener{

    GoogleApiClient mGoogleApiClient;
    FusedLocationProviderApi fusedLocationProviderApi ;
    int  LOCATION_INTERVAL=1000;
    static LocationRequest locationRequest;

    public LocationService(){
        super("LocationService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {

    }
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
            if(locationRequest != null)
            {
            }
            else {
                locationRequest = LocationRequest.create();
            }
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

                c.close();
                db.close();
                dh.close();
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

    Location lastLocation,lastSendLocation;
    int coarse,LastSpeed,speed,distance,Senddistance, LastCoarse;
    long lasttime,curenttime;
    Calendar c;
    @Override
    public void onLocationChanged(Location location) {
        c = Calendar.getInstance();
        curenttime=c.getTimeInMillis();
        coarse = 0;
        speed=0;
        distance=-1;
        if (lastLocation != null) {

            coarse =(int) location.bearingTo(lastSendLocation);
            Senddistance=(int)location.distanceTo(lastSendLocation);

            distance=(int)location.distanceTo(lastLocation);
            double difTime = (curenttime - lasttime) / 1000.0;
            speed = (int)( (distance*1.0) / difTime * 3.6); //KM/H
        }
        else{
            lastSendLocation = location;
            LastCoarse = coarse;
            LastSpeed = speed;
        }

        lasttime=curenttime; // use calc current speed
        lastLocation=location;

//        String s = String.valueOf(speed) +
//                "----" + String.valueOf(Senddistance) +
//                "----" + String.valueOf(distance) +
//                "----" + String.valueOf(LastCoarse) +
//                "-----" + String.valueOf(coarse);

        if (
                        (speed == 0 && LastSpeed !=0) // Move and stop
                        ||
                        (speed > 0 && LastSpeed ==0) // Move after stop
                        ||
                         (Senddistance > 50 ) // 50
                        ||
                        ( speed >0 && Senddistance > 5 && Math.abs(LastCoarse -coarse)>10 ) //
                ) {
//            s+="----- Send";
            Tools.SaveLocation(location, coarse,speed);
            lastSendLocation = location;
            LastCoarse = coarse;
            LastSpeed = speed;
//            android.widget.Toast.makeText(getApplicationContext(), s, android.widget.Toast.LENGTH_LONG).show();
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
       if(Tools.curAccurate!=null && Tools.lastAccurate!=null && Tools.curAccurate!= Tools.lastAccurate) {
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
