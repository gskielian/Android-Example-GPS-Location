package org.foodrev.android_example_gps_location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by magulo on 10/2/16.
 */

public class CustomGeoFences {

    private String locationName;
    private String triggerType;
    private float triggerRange;
    private LatLng latLng;
    private float longitude;
    private int secondsDelay; //mainly for dwell
    private String currentState; //inside or outside


    public CustomGeoFences(String locationName,
                           String triggerType,
                           LatLng latLng,
                           float triggerRange,
                           int secondsDelay) {
        this.locationName = locationName;
        this.latLng = latLng;
        this.triggerType = triggerType;
        this.triggerRange = triggerRange;
        this.secondsDelay = secondsDelay;
    }


    //getters
    public String getLocationName() {
        return locationName;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public float getTriggerRange() {
        return triggerRange;
    }

    public int getSecondsDelay() {
        return secondsDelay;
    }

    public String getCurrentState() {
        return currentState;
    }

    //setters
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public void setTriggerRange(float triggerRange) {
        this.triggerRange = triggerRange;
    }

    public void setSecondsDelay(int secondsDelay) {
        this.secondsDelay = secondsDelay;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }


}
