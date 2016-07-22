package com.shadow3x3x3.pathbycar_yi.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Date;


public class LocationService extends Service implements
        ConnectionCallbacks, OnConnectionFailedListener {

    protected static final String TAG = "Service-location";

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;

    protected String mLastUpdateTime;
    protected Boolean mRequestingLocationUpdates;

    protected ServiceLocationListener serviceLocationListener;

    private URI uri;
    protected LocationWebSocket locationWebSocket;

    protected class ServiceLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            mCurrentLocation = location;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            Log.d(TAG, String.valueOf(mCurrentLocation.getLongitude()) + ", " + String.valueOf(mCurrentLocation.getLatitude()));
            locationWebSocket.send(String.valueOf(mCurrentLocation.getLongitude()) + ", " + String.valueOf(mCurrentLocation.getLatitude()));
        }
    } // LocationListener class end

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        super.onCreate();
        buildGoogleApiClient();
        setUri();
        locationWebSocket = new LocationWebSocket(uri);
        locationWebSocket.connect();
        mRequestingLocationUpdates = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int starId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, starId);
        serviceLocationListener = new ServiceLocationListener();
        mGoogleApiClient.connect();
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            } else {
                startLocationUpdates();
            }

        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, serviceLocationListener);
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        }

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroy(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, serviceLocationListener);
        locationWebSocket.close();
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private void setUri(){
        try {
            uri = new URI("ws://140.134.26.68:4567/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
