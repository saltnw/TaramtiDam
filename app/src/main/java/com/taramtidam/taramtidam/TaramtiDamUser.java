package com.taramtidam.taramtidam;

/**
 * Created by Asaf on 21/04/2017.
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private String imagestring;
    private int donationsCounter;
    private boolean sendMails;

    public TaramtiDamUser(){}

    public TaramtiDamUser(String uid, String fullName,String email, String address, String bloodType) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.address1 = address;
        this.address2 = "";
        this.bloodType = "";
        this.completedRegisteration = false;
        this.lastDonation = null;
        this.imagestring = "";
        this.donationsCounter = 0;
        this.sendMails = true;
    }

    public TaramtiDamUser(String uid, String fullName,String email) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.address1 = "";
        this.address2 = "";
        this.bloodType = "";
        this.completedRegisteration = false;
        this.lastDonation = null;
        this.imagestring = "";
        this.donationsCounter = 0;
        this.sendMails = true;
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

    public void setUserImage(String strimg) { this.imagestring = strimg;}
    public String getUserImage(){return imagestring;}

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


    public boolean getMailingBool() {
        return sendMails;
    }

    public void setMailingBool(boolean send) {
        this.sendMails = send;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void increaseRankByOne (){

        if (this.rankLevel <= 3){
            this.rankLevel++;
        }
    }
    public int getDonationsCounter() {
        return donationsCounter;
    }

    public void setDonationsCounter(int donationsCounter) {
        this.donationsCounter = donationsCounter;
    }

    public void SetLastDonationDateToToday(){
        this.lastDonation = new Date();
    }

    public String getLastDonationInString (){
        if(donationsCounter==0) {
            return "";
        }
        else {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            return df.format(this.lastDonation);
        }

    }
    /*
    returns the number of days from the user's last donation
    returns -1 if the users didn't donated yet
     */
    public long daysFromLastDonation(){
        if (donationsCounter == 0){
            return -1;
        }

        Date user_last_donation = this.lastDonation;
        Date today_date = new Date();
        long diff, diffDays;

        diff =  today_date.getTime() -  user_last_donation.getTime();
        diffDays = diff / (24*60*60*1000);

        return diffDays;
    }


}
