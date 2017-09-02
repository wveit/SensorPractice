package com.example.androidu.sensorpractice;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MyGps {
    private static final int PERMISSION_REQUEST_CODE = 79;
    private Activity mActivity;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ArrayList<Listener> mListenerList = new ArrayList<Listener>();

    public MyGps(Activity activity){
        mActivity = activity;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public boolean start(){
        boolean havePermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if(havePermission){
            try {
                mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(mActivity, mLocationSuccessListener);

                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(5000);
                locationRequest.setFastestInterval(5000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationUpdatesCallback, null);
                return true;
            }
            catch(SecurityException e){
                e.printStackTrace();
            }
        }
        else{
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
        return false;
    }

    public void stop(){
        mFusedLocationProviderClient.removeLocationUpdates(mLocationUpdatesCallback);
    }

    public void addListener(Listener listener){
        mListenerList.add(listener);
    }

    public interface Listener{
        public void handleLocation(Location location);
    }

    private OnSuccessListener<Location> mLocationSuccessListener = new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            for(Listener listener : mListenerList)
                listener.handleLocation(location);
        }
    };

    private LocationCallback mLocationUpdatesCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            for(Location location : locationResult.getLocations())
                for(Listener listener : mListenerList)
                    listener.handleLocation(location);
        }
    };
}
