package org.foodrev.android_example_gps_location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magulo on 10/2/16.
 */

public class GpsHelper {

    private int counter = 0;
    //initialization flag
    private boolean INIT_NOT_DONE = true;

    //ArrayList for our geofence equivalent
    List<CustomGeoFences> customGeoFences;
    Location currentLocation;

    //gps setup related
    private LocationManager mLocationManager = null;
    private LocationListener mLocationListener = null;

    //maps placeholder from main context
    private GoogleMap mGoogleMap;

    //context placeholder later passed in from main context
    private Context mContext;
    private boolean isLogging = false;

    public static GpsHelper mGpsHelper = null;
    private GpsHelper(GoogleMap mGoogleMap, Context mContext) {
        this.mGoogleMap = mGoogleMap;
        this.mContext = mContext;


        this.customGeoFences = new ArrayList<CustomGeoFences>();
        populateGeofences();

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

        if (mLocationManager == null) {
            mLocationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        }
        // Define a listener that responds to location updates
        if (mLocationListener == null) {
            mLocationListener = new LocationListener() {
                public void onLocationChanged(Location location) {

                    currentLocation = location;

                    if (INIT_NOT_DONE) {
                        initializeGeofences();
                        INIT_NOT_DONE = false;
                    }

                    makeUseOfNewLocation(currentLocation);

                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {

                }

                public void onProviderDisabled(String provider) {
                }
            };
        }
    }

//TODO look into creating hysterysis, or number of times max that it can be activated (probably the latter actually)
    public void populateGeofences() {
        customGeoFences.add(new CustomGeoFencesBuilder()
                .setLocationName("GooglePlex")
                .setLatLng(new LatLng(37.4219999,-122.0840575))
                .setTriggerType("ENTER_OR_EXIT")
                .setTriggerRange(100)
                .createCustomGeoFences());

        customGeoFences.add(new CustomGeoFencesBuilder()
                .setLocationName("Church")
                .setLatLng(new LatLng(37.7254374,-122.4100932))
                .setTriggerType("ENTER_OR_EXIT")
                .setTriggerRange(100)
                .createCustomGeoFences());
    }

    public void initializeGeofences() {
        //set current state to inside or outside
        float[] results = new float[1];
        for (CustomGeoFences customGeoFence : customGeoFences) {
            Location.distanceBetween(
                    customGeoFence.getLatLng().latitude,
                    customGeoFence.getLatLng().longitude,
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    results
            );
            Toast.makeText(mContext, "results: " + String.valueOf(results[0]) + " " +  customGeoFence.getLocationName(), Toast.LENGTH_SHORT).show();

            if (results[0] < customGeoFence.getTriggerRange()) {
                customGeoFence.setCurrentState("INSIDE");
                Toast.makeText(mContext, "currently inside: " + customGeoFence.getLocationName(), Toast.LENGTH_SHORT).show();
            } else {
                customGeoFence.setCurrentState("OUTSIDE");
                Toast.makeText(mContext, "currently outside: " + customGeoFence.getLocationName(), Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void loopThroughGeofences(Location currentLocation) {
        float[] results = new float[1];
        String currentStateHolder;
        for (CustomGeoFences customGeoFence : customGeoFences) {
            Location.distanceBetween(
                    customGeoFence.getLatLng().latitude,
                    customGeoFence.getLatLng().longitude,
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    results
            );
            if (results[0] < customGeoFence.getTriggerRange()) {
                currentStateHolder = "INSIDE";
            } else {
                currentStateHolder = "OUTSIDE";
            }

            //test if state is equal
            if (!currentStateHolder.equals(customGeoFence.getCurrentState())) {

                boolean ENTERED_ZONE = customGeoFence.getCurrentState().equals("OUTSIDE")
                        && currentStateHolder.equals("INSIDE");
                boolean EXITED_ZONE = !(ENTERED_ZONE); //only two cases, but keeping this for readability

                switch (customGeoFence.getTriggerType()){
                    case "ENTER":
                        if (ENTERED_ZONE) Toast.makeText(mContext,"Entered" + customGeoFence.getLocationName(), Toast.LENGTH_SHORT).show();
                        break;
                    case "EXIT":
                        if (EXITED_ZONE) Toast.makeText(mContext,"Exited" + customGeoFence.getLocationName(), Toast.LENGTH_SHORT).show();
                        break;
                    case "ENTER_OR_EXIT":
                        if (ENTERED_ZONE) Toast.makeText(mContext,"Entered" + customGeoFence.getLocationName(), Toast.LENGTH_SHORT).show();
                        if (EXITED_ZONE) Toast.makeText(mContext,"Exited" + customGeoFence.getLocationName(), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                }
            }

        }

    }

    private void makeUseOfNewLocation(Location location) {
        loopThroughGeofences(location);
        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
        mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Current Location"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        Toast.makeText(mContext, "new location number"  + String.valueOf(counter++), Toast.LENGTH_SHORT).show();
    }

    public void startGpsLogging() {
        long minTimeMilli = 4000;
        float minDistanceMeters = 2; 
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeMilli, minDistanceMeters, mLocationListener);
        Toast.makeText(mContext, "started GPS Logging", Toast.LENGTH_SHORT).show();
        this.isLogging = true;
    }


    public void stopGpsLogging() {
        mLocationManager.removeUpdates(mLocationListener);
        Toast.makeText(mContext, "stopped GPS Logging", Toast.LENGTH_SHORT).show();
        this.isLogging = false;
    }


    public boolean isGpsLogging() {
        return isLogging;
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
