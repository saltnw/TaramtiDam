package com.taramtidam.taramtidam.model;

/**
 * Created by shoshan on 14/08/2017.
 */

public class DistanceIndex {
    private int mobileIndex;
    private double mobileDistance;

    public DistanceIndex(int mobileIndex, double mobileDistance) {
        this.mobileIndex = mobileIndex;
        this.mobileDistance = mobileDistance;
    }

    public int getMobileIndex() {
        return mobileIndex;
    }

    public void setMobileIndex(int mobileIndex) {
        this.mobileIndex = mobileIndex;
    }

    public double getMobileDistance() {
        return mobileDistance;
    }

    public void setMobileDistance(double mobileDistance) {
        this.mobileDistance = mobileDistance;
    }


}
