package com.taramtidam.taramtidam;

/**
 * Created by Lilach Fishman on 26/04/2017.
 */

public class MDAMobile {
    private double latitude;
    private double longitude;

    public MDAMobile(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
