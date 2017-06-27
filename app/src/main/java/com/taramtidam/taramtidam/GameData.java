package com.taramtidam.taramtidam;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by shoshan on 21/06/2017.
 */

public class GameData {

    long dayScore;
    long nightScore;

    long dayEastCounter;
    long nightEastCounter;
    long dayEastPercent;
    long nightEastPercent;
    long eastTotal;

    long dayNorthCounter;

    long nightNorthCounter;
    long dayNorthPercent;
    long nightNorthPercent;
    long northTotal;

    long daySouthCounter;
    long nightSouthCounter;
    long daySouthPercent;
    long nightSouthPercent;
    long southTotal;

    long dayWestCounter;
    long nightWestCounter;
    long dayWestPercent;
    long nightWestPercent;
    long westTotal;


    public void updateGameStats(DatabaseReference dbRef) {
        final DatabaseReference gameRef =  dbRef.child("Game");
        gameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //read donation per vampires team
                dayEastCounter = (long)dataSnapshot.child("East").child("Day").child("Donations").getValue();
                //nightEastCounter = (long)dataSnapshot.child("East").child("Night").child("Donations").getValue();
                dayNorthCounter = (long)dataSnapshot.child("North").child("Day").child("Donations").getValue();
                daySouthCounter = (long)dataSnapshot.child("South").child("Day").child("Donations").getValue();
                dayWestCounter = (long)dataSnapshot.child("West").child("Day").child("Donations").getValue();

                //read donations per all area
                eastTotal = (long)dataSnapshot.child("East").child("Donations").getValue();
                northTotal = (long)dataSnapshot.child("North").child("Donations").getValue();
                southTotal = (long)dataSnapshot.child("South").child("Donations").getValue();
                westTotal = (long)dataSnapshot.child("West").child("Donations").getValue();

                Log.d("GAME4", "east total is: " + eastTotal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void computeGameStats(){
        if(southTotal != 0) {
            daySouthPercent = (long)((double) daySouthCounter / (double)(southTotal) * 100);
            nightSouthPercent = 100 - daySouthPercent;
        }

        if(northTotal != 0) {
            dayNorthPercent = (long) ((double) dayNorthCounter / (double) (northTotal) * 100);
            nightNorthPercent = 100 - dayNorthPercent;
        }

        if(eastTotal!= 0) {
            dayEastPercent = (long) ((double) dayEastCounter / (double) (eastTotal) * 100);
            nightEastPercent = 100 - dayEastPercent;
        }

        if(westTotal != 0) {
            dayWestPercent = (long) ((double) dayWestCounter / (double) (westTotal) * 100);
            nightWestPercent = 100 - dayWestPercent;
        }

        Log.d("Game4", "precetage south are: day = "+ daySouthPercent +"% night= "+ nightSouthPercent);
        Log.d("Game4", "precetage north are: day = "+ dayNorthPercent +"% night= "+ nightNorthPercent);
        Log.d("Game4", "precetage east are: day = "+ dayEastPercent +"% night= "+ nightEastPercent);
        Log.d("Game4", "precetage west are: day = "+ dayWestPercent +"% night= "+ nightWestPercent);

    }

    public long getDayEastCounter() {
        return dayEastCounter;
    }

    public void setDayEastCounter(long dayEastCounter) {
        this.dayEastCounter = dayEastCounter;
    }

    public long getNightEastCounter() {
        return nightEastCounter;
    }

    public void setNightEastCounter(long nightEastCounter) {
        this.nightEastCounter = nightEastCounter;
    }

    public long getDayEastPercent() {
        return dayEastPercent;
    }

    public void setDayEastPercent(long dayEastPercent) {
        this.dayEastPercent = dayEastPercent;
    }

    public long getNightEastPercent() {
        return nightEastPercent;
    }

    public void setNightEastPercent(long nightEastPercent) {
        this.nightEastPercent = nightEastPercent;
    }

    public long getEastTotal() {
        return eastTotal;
    }

    public void setEastTotal(long eastTotal) {
        this.eastTotal = eastTotal;
    }

    public long getDayNorthCounter() {
        return dayNorthCounter;
    }

    public void setDayNorthCounter(long dayNorthCounter) {
        this.dayNorthCounter = dayNorthCounter;
    }

    public long getNightNorthCounter() {
        return nightNorthCounter;
    }

    public void setNightNorthCounter(long nightNorthCounter) {
        this.nightNorthCounter = nightNorthCounter;
    }

    public long getDayNorthPercent() {
        return dayNorthPercent;
    }

    public void setDayNorthPercent(long dayNorthPercent) {
        this.dayNorthPercent = dayNorthPercent;
    }

    public long getNightNorthPercent() {
        return nightNorthPercent;
    }

    public void setNightNorthPercent(long nightNorthPercent) {
        this.nightNorthPercent = nightNorthPercent;
    }

    public long getNorthTotal() {
        return northTotal;
    }

    public void setNorthTotal(long northTotal) {
        this.northTotal = northTotal;
    }

    public long getDaySouthCounter() {
        return daySouthCounter;
    }

    public void setDaySouthCounter(long daySouthCounter) {
        this.daySouthCounter = daySouthCounter;
    }

    public long getNightSouthCounter() {
        return nightSouthCounter;
    }

    public void setNightSouthCounter(long nightSouthCounter) {
        this.nightSouthCounter = nightSouthCounter;
    }

    public long getDaySouthPercent() {
        return daySouthPercent;
    }

    public void setDaySouthPercent(long daySouthPercent) {
        this.daySouthPercent = daySouthPercent;
    }

    public long getNightSouthPercent() {
        return nightSouthPercent;
    }

    public void setNightSouthPercent(long nightSouthPercent) {
        this.nightSouthPercent = nightSouthPercent;
    }

    public long getSouthTotal() {
        return southTotal;
    }

    public void setSouthTotal(long southTotal) {
        this.southTotal = southTotal;
    }

    public long getDayWestCounter() {
        return dayWestCounter;
    }

    public void setDayWestCounter(long dayWestCounter) {
        this.dayWestCounter = dayWestCounter;
    }

    public long getNightWestCounter() {
        return nightWestCounter;
    }

    public void setNightWestCounter(long nightWestCounter) {
        this.nightWestCounter = nightWestCounter;
    }

    public long getDayWestPercent() {
        return dayWestPercent;
    }

    public void setDayWestPercent(long dayWestPercent) {
        this.dayWestPercent = dayWestPercent;
    }

    public long getNightWestPercent() {
        return nightWestPercent;
    }

    public void setNightWestPercent(long nightWestPercent) {
        this.nightWestPercent = nightWestPercent;
    }

    public long getWestTotal() {
        return westTotal;
    }

    public void setWestTotal(long westTotal) {
        this.westTotal = westTotal;
    }

    public long getDayScore() {
        long total = dayEastCounter + dayWestCounter + daySouthCounter + dayNorthCounter;
        return total;
    }

    public long getNightScore() {
        long total = nightEastCounter + nightWestCounter + nightSouthCounter + nightNorthCounter;
        return total;
    }

}
