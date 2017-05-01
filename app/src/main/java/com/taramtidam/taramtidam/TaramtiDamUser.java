package com.taramtidam.taramtidam;

/**
 * Created by Asaf on 21/04/2017.
 */

import java.util.Date;

        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;

        import android.widget.Toast;


        import java.util.Arrays;
        import java.util.Date;

public class TaramtiDamUser {

    private boolean completedRegisteration;
    private String uid;
    private String email;
    private String fullName;
    private String address1;
    private String address2;
    private String bloodType;
    private int rankLevel;
    private Date lastDonation;
    private Date lastPosition;

    public TaramtiDamUser(){}

    public TaramtiDamUser(String uid, String fullName,String email, String address, String bloodType) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.address1 = address;
        this.address2 = "";
        this.bloodType = "";
        this.completedRegisteration = false;
    }

    public TaramtiDamUser(String uid, String fullName,String email) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.address1 = "";
        this.address2 = "";
        this.bloodType = "";
        this.completedRegisteration = false;
    }

    public boolean isCompletedRegisteration() {
        return completedRegisteration;
    }

    public void setCompletedRegisteration(boolean completedRegisteration) {
        this.completedRegisteration = completedRegisteration;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress1(String address) {
        this.address1 = address;
    }

    public void setAddress2(String address) {
        this.address2 = address;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public int getRankLevel() {
        return rankLevel;
    }

    public void setRankLevel(int rankLevel) {
        this.rankLevel = rankLevel;
    }

    public Date getLastDonation() {
        return lastDonation;
    }
    public String getuid() {
        return uid;
    }
    public void setuid(String uid) {
        this.uid = uid;
    }

    public void setLastDonation(Date lastDonation) {
        this.lastDonation = lastDonation;
    }

    public Date getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(Date lastPosition) {
        this.lastPosition = lastPosition;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
