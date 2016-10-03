package org.foodrev.android_example_gps_location;

import com.google.android.gms.maps.model.LatLng;

public class CustomGeoFencesBuilder {
    private String locationName;
    private String triggerType;
    private LatLng latLng;
    private float triggerRange = 100;
    private int secondsDelay = 0;

    public CustomGeoFencesBuilder setLocationName(String locationName) {
        this.locationName = locationName;
        return this;
    }

    public CustomGeoFencesBuilder setTriggerType(String triggerType) {
        this.triggerType = triggerType;
        return this;
    }

    public CustomGeoFencesBuilder setLatLng(LatLng latLng) {
        this.latLng = latLng;
        return this;
    }

    public CustomGeoFencesBuilder setTriggerRange(float triggerRange) {
        this.triggerRange = triggerRange;
        return this;
    }

    public CustomGeoFencesBuilder setSecondsDelay(int secondsDelay) {
        this.secondsDelay = secondsDelay;
        return this;
    }

    public CustomGeoFences createCustomGeoFences() {
        return new CustomGeoFences(locationName, triggerType, latLng, triggerRange, secondsDelay);
    }
}