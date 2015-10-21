package com.shadow3x3x3.pathbycar_yi;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;


public class LocationService extends IntentService implements
    ConnectionCallbacks, OnConnectionFailedListener {

  protected static final String TAG = "Service-location";

  public static final int START_LOCATION_UPDATES = 0;
  public static final int STOP_LOCATION_UPDATES  = 1;

  public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
  public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
      UPDATE_INTERVAL_IN_MILLISECONDS / 2;

  protected GoogleApiClient mGoogleApiClient;
  protected LocationRequest mLocationRequest;
  protected Location mCurrentLocation;

  protected String mLastUpdateTime;
  protected Boolean mRequestingLocationUpdates;

  protected ServiceLocationListener serviceLocationListener;

  protected class ServiceLocationListener implements LocationListener {
    @Override
    public void onLocationChanged(Location location) {
      mCurrentLocation = location;
      mLastUpdateTime  = DateFormat.getTimeInstance().format(new Date());
      Log.d(TAG, String.valueOf(mCurrentLocation.getLongitude()) + ", " + String.valueOf(mCurrentLocation.getLatitude()));
    }
  } // LocationListener class end

  public LocationService() {
    super("LocationService");
  }

  @Override
  public void onCreate(){
    Log.e(TAG, "onCreate");
    super.onCreate();
    buildGoogleApiClient();
    mRequestingLocationUpdates = false;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int starId) {
    Log.e(TAG, "onStartCommand");
    super.onStartCommand(intent, flags, starId);
    serviceLocationListener = new ServiceLocationListener();
    mGoogleApiClient.connect();
    return  START_STICKY;
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Log.e(TAG, "onHandleIntent");
    if (!mRequestingLocationUpdates) {
      mRequestingLocationUpdates = true;
      if (!mGoogleApiClient.isConnected()){
        mGoogleApiClient.connect();
      } else {
        startLocationUpdates();
      }

    }

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
    LocationServices.FusedLocationApi.requestLocationUpdates(
        mGoogleApiClient, mLocationRequest, serviceLocationListener);
  }


  @Override
  public void onConnected(Bundle bundle) {
    if (mCurrentLocation == null) {
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
  public void onDestroy() {
    super.onDestroy();
    Log.e(TAG, "onDestroy");
    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, serviceLocationListener);
  }

}
