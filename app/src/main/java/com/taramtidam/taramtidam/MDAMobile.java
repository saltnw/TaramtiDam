package com.taramtidam.taramtidam;

/**
 * Created by Lilach Fishman on 29/04/2017.
 */

public class MDAMobile {
    private double latitude;
    private double longitude;
    private String id;
    private String address;
    private String city;
    private String date;
    private String time;
    private String endTime;
    private String description;

    public MDAMobile(double latitude, double longitude, String id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }

    public MDAMobile(double latitude, double longitude, String id, String address, String city, String date, String time, String endTime, String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.address = address;
        this.city = city;
        this.date = date;
        this.time = time;
        this.endTime = endTime;
        this.description = description;
    }

    public MDAMobile() {
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

    public String getEndTime() {  return endTime; }

    public void setEndTime(String endTime) {    this.endTime = endTime;}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
