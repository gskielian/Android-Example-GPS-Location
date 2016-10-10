package org.foodrev.android_example_gps_location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class GpsService extends Service {

    PowerManager.WakeLock mWakeLock;
    GoogleMap mGoogleMap;

    private boolean INIT_NOT_DONE = true;

    List<CustomGeoFences> customGeoFences;
    Location currentLocation;

    private LocationManager mLocationManager = null;
    private LocationListener mLocationListener = null;

    private int counter = 0;

    public GpsService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.customGeoFences = new ArrayList<CustomGeoFences>();
        populateGeofences();
        setupGps();
        startGpsLogging();
        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");
        mWakeLock.acquire();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopGpsLogging();
        mWakeLock.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void startGpsLogging() {
        long minTimeMilli = 4000;
        float minDistanceMeters = 2;
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeMilli, minDistanceMeters, mLocationListener);
        Toast.makeText(getApplicationContext(), "started GPS Logging", Toast.LENGTH_SHORT).show();
    }
    public void stopGpsLogging() {
        mLocationManager.removeUpdates(mLocationListener);
        Toast.makeText(getApplicationContext(), "stopped GPS Logging", Toast.LENGTH_SHORT).show();
    }

    private void setupGps() {

        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
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
    private void populateGeofences() {
        customGeoFences.add(new CustomGeoFencesBuilder()
                .setLocationName("GooglePlex")
                .setLatLng(new LatLng(37.4219999,-122.0840575))
                .setTriggerType("ENTER_OR_EXIT")
                .setTriggerRange(1000)
                .createCustomGeoFences());

        customGeoFences.add(new CustomGeoFencesBuilder()
                .setLocationName("Church")
                .setLatLng(new LatLng(37.7254374,-122.4100932))
                .setTriggerType("ENTER_OR_EXIT")
                .setTriggerRange(1000)
                .createCustomGeoFences());
    }
    private void initializeGeofences() {
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
            Toast.makeText(getApplicationContext(), "results: " + String.valueOf(results[0]) + " " +  customGeoFence.getLocationName(), Toast.LENGTH_SHORT).show();

            if (results[0] < customGeoFence.getTriggerRange()) {
                customGeoFence.setCurrentState("INSIDE");
                Toast.makeText(getApplicationContext(), "currently inside: " + customGeoFence.getLocationName(), Toast.LENGTH_SHORT).show();
            } else {
                customGeoFence.setCurrentState("OUTSIDE");
                Toast.makeText(getApplicationContext(), "currently outside: " + customGeoFence.getLocationName(), Toast.LENGTH_SHORT).show();

            }
        }
    }
    private void makeUseOfNewLocation(Location location) {
        loopThroughGeofences(location);
        //LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        Toast.makeText(getApplicationContext(), "loc "  + String.valueOf(counter++)
                + " lat " + String.valueOf(location.getLatitude())
                + " long " + String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();
    }
    private void loopThroughGeofences(Location currentLocation) {
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
                        if (ENTERED_ZONE) Toast.makeText(getApplicationContext(),"Entered" + customGeoFence.getLocationName(), Toast.LENGTH_SHORT).show();
                        break;
                    case "EXIT":
                        if (EXITED_ZONE) Toast.makeText(getApplicationContext(),"Exited" + customGeoFence.getLocationName(), Toast.LENGTH_SHORT).show();
                        break;
                    case "ENTER_OR_EXIT":
                        if (ENTERED_ZONE) Toast.makeText(getApplicationContext(),"Entered" + customGeoFence.getLocationName(), Toast.LENGTH_SHORT).show();
                        if (EXITED_ZONE) Toast.makeText(getApplicationContext(),"Exited" + customGeoFence.getLocationName(), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                }
            }

        }

    }
}
