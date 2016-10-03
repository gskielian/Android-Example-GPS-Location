package org.foodrev.android_example_gps_location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by magulo on 10/2/16.
 */

public class GpsHelper {

    //hashtables for our geofence equivalent


    //gps setup related
    private LocationManager mLocationManager = null;
    private LocationListener mLocationListener = null;

    //maps placeholder from main context
    private GoogleMap mGoogleMap;

    //context placeholder later passed in from main context
    private Context mContext;

    public static GpsHelper mGpsHelper = null;
    private GpsHelper(GoogleMap mGoogleMap, Context mContext) {
        this.mGoogleMap = mGoogleMap;
        this.mContext = mContext;

        setupGps();
        startGpsLogging();
    }

    public static synchronized GpsHelper getInstance(GoogleMap mGoogleMap, Context mContext) {

        if(mGpsHelper == null) {
            mGpsHelper = new GpsHelper(mGoogleMap, mContext);
        }

        return mGpsHelper;
    }

    public void setupGps() {
        mLocationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }


    private void makeUseOfNewLocation(Location location) {
        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
        mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Current Location"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void startGpsLogging() {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        Toast.makeText(mContext, "started GPS Logging", Toast.LENGTH_SHORT).show();
    }


    public void stopGpsLogging() {
        mLocationManager.removeUpdates(mLocationListener);
        Toast.makeText(mContext, "stopped GPS Logging", Toast.LENGTH_SHORT).show();
    }

    //TODO
    private void triggerWhenEnter() {
        Toast.makeText(mContext, "entered zone", Toast.LENGTH_SHORT).show();
    }

    //TODO
    private void triggerWhenLeave() {
        Toast.makeText(mContext, "left zone", Toast.LENGTH_SHORT).show();
    }


    private void moveMarker() {
    }

}
