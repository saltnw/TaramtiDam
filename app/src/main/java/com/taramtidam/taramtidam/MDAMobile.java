package com.taramtidam.taramtidam;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lilach Fishman on 29/04/2017.
 */

public class MDAMobile {
    private double latitude;
    private double longitude;
    private String id;


    public MDAMobile(double latitude, double longitude, String id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MDAMobile mdaMobile = (MDAMobile) o;

        if (Double.compare(mdaMobile.latitude, latitude) != 0) return false;
        if (Double.compare(mdaMobile.longitude, longitude) != 0) return false;
        return id != null ? id.equals(mdaMobile.id) : mdaMobile.id == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
