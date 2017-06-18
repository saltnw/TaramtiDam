package com.taramtidam.taramtidam;

/**
 * Created by shoshan on 18/06/2017.
 */

public class TeamInfo {
    private String area;
    private String vemp;

    public TeamInfo() {
        this.area = "none";
        this.vemp = "none";
    }

    public TeamInfo(String area, String vemp) {
        this.area = area;
        this.vemp = vemp;
    }

    public String getArea() {
        return this.area;
    }

    public String getVemp() {
        return this.vemp;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setVemp(String vemp) {
        this.vemp = vemp;
    }

}
