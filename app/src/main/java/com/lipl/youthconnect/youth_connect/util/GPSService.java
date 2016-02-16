package com.lipl.youthconnect.youth_connect.util;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.lipl.youthconnect.youth_connect.pojo.CurrentLocation;

import java.util.Calendar;

public class GPSService extends Service implements LocationListener {

    private LocationManager locationManager;

    public GPSService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        //Toast.makeText(this, "GPS Service Started", Toast.LENGTH_LONG).show();

        /********** get Gps location service LocationManager object ***********/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                /* CAL METHOD requestLocationUpdates */

        // Parameters :
        //   First(provider)    :  the name of the provider with which to register
        //   Second(minTime)    :  the minimum time interval for notifications,
        //                         in milliseconds. This field is only used as a hint
        //                         to conserve power, and actual time between location
        //                         updates may be greater or lesser than this value.
        //   Third(minDistance) :  the minimum distance interval for notifications, in meters
        //   Fourth(listener)   :  a {#link LocationListener} whose onLocationChanged(Location)
        //                         method will be called for each location update


        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled
        } else {
            // First get location from Network Provider

        }

        return START_STICKY;
    }

    /************* Called after each 3 sec **********/
    @Override
    public void onLocationChanged(Location location) {

        String str = "Latitude: "+location.getLatitude()+"\nLongitude: "+location.getLongitude();
        //Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
        CurrentLocation currentLocation = new CurrentLocation(Parcel.obtain());
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        currentLocation.setDate(day+" / "+month+" / "+year);
        currentLocation.setTime(hour + " : " + minute);
        currentLocation.setLatitude(location.getLatitude() + "");
        currentLocation.setLongitude(location.getLongitude() + "");
    }

    @Override
    public void onProviderDisabled(String provider) {
        /******** Called when User off Gps *********/
        //Toast.makeText(getBaseContext(), "Gps turned off ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

        /******** Called when User on Gps  *********/
        //Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "GPS Service Destroyed", Toast.LENGTH_LONG).show();
    }
}