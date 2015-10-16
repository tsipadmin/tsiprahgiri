package com.tstracker.tstracker;

/**
 * Created by ali on 9/27/15.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetConnectivity {

    private Context _Cucontext;

    public InternetConnectivity(Context context){
        this._Cucontext = context;
    }
    public Boolean isConnectedToInternet()
    {
        boolean isConnected=false;
        ConnectivityManager connectivity = (ConnectivityManager) _Cucontext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            isConnected= activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return isConnected;
    }
}

