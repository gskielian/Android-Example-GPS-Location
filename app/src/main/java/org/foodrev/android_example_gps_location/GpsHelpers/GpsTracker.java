package org.foodrev.android_example_gps_location.GpsHelpers;

/**
 * Created by magulo on 10/2/16.
 *
 * GPS Singleton
 */

public class GpsTracker {

    private static GpsTracker mGpsTracker = null;

    protected GpsTracker() {}

    public static GpsTracker getInstance() {
        if(mGpsTracker == null) {
            mGpsTracker = new GpsTracker();
        }
        return mGpsTracker;
    }

}
