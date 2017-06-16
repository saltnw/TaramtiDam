package com.taramtidam.taramtidam;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Lilach Fishman on 26/05/2017.
 */

public class OurPullService extends JobService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Context context;
    public static GoogleApiClient mClient;
    private Geofencing mGeofencing;
    public static List<MDAMobile> mobiles = new ArrayList<>();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;


    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d("FENCE", "OurPullService starting getting locations");
        if (ActivityCompat.checkSelfPermission(OurPullService.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            try {
                Thread.sleep(30_000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.d("FENCE", "Initializing Google API Client");
        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)//todo remove?
                //.enableAutoManage(this, this)
                .build();


        Log.d("FENCE","Connecting Google API Client");

        mClient.connect();


        return false;
    }


    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d("SHOSHAN_JOB","stopped");
        return false;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("FENCE", "API Client Connection Successful!");

        Log.d("FENCE","Initialize Geofencing object");
        mGeofencing = new Geofencing(this, mClient);

        Log.d("FENCE", "Get reference to Firebase Database at MDA");
        // Get a reference to our MDA mobiles
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference("MDA").child("Today");

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("FENCE / SERVICE", "Updating MDA mobiles from the database");
                Iterable<DataSnapshot> locations = dataSnapshot.getChildren();
                int i = 0;
                while (locations.iterator().hasNext()) {
                    MDAMobile currMda = new MDAMobile();
                    DataSnapshot nextMDALoc = locations.iterator().next();
                    currMda.setCity(nextMDALoc.child("city").getValue().toString());
                    currMda.setAddress(nextMDALoc.child("address").getValue().toString());
                    currMda.setLongitude(Double.parseDouble(nextMDALoc.child("longitude").getValue().toString()));
                    currMda.setLatitude(Double.parseDouble(nextMDALoc.child("latitude").getValue().toString()));
                    currMda.setTime(nextMDALoc.child("start time").getValue().toString());
                    currMda.setEndTime(nextMDALoc.child("end time").getValue().toString());
                    currMda.setDate(nextMDALoc.child("date").getValue().toString());
                    currMda.setDescription(nextMDALoc.child("description").getValue().toString());
                    currMda.setId(String.valueOf(i));

                    mobiles.add(currMda);

                    System.out.println("\n");
                    System.out.println("\n");
                    i++;
                }
                Log.d("FENCE", "Done updating mobiles");
                System.out.println("done");
                Log.d("FENCE", "Going to update geofences list to match new MDA mobiles list");
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = new Date();
                //adding a shared preferences
                SharedPreferences pref = getApplicationContext().getSharedPreferences("Pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
//                editor.clear();
//                editor.commit();
                //cheking if we already have a date saved in our map
                String savedDate  = pref.getString("date", null);
                //if not, insert current date
                if(savedDate == null){
                    if(mClient == null){
                        Log.d("FENCE","API client not available-retry later");
                        return;
                    }
                    String stringDate = dateFormat.format(date);
                    editor.putString("date",stringDate);
                    editor.commit();
                    Log.d("FENCE","Initializing date" + stringDate);
                }
                else {
                    Log.d("FENCE", "saved date is: "+savedDate);
                    //if there is a stored date, compare it to current date via DB entry
                    if (mobiles != null && mobiles.get(0) != null) {
                        //if the saved date is equal to the date of mobiles from the DB, no need to re-register geofences
                        if (mobiles.get(0).getDate().equals(savedDate)) {
                            Log.d("FENCE","Data is updated no need to register new geofences");
                            //  cond.open();
                            // return;
                        }
                    }
                }
                // the date that was saved in the machine is different from the DB mobiles date
                //update the date and register new (today's) geofences
                editor.putString("date", dateFormat.format(date));
                editor.commit();
                Log.d("FENCE","updating shared preferences date");
                mGeofencing.updateGeofencesList(mobiles);
                Log.d("FENCE", "Going to register all geofences per all MDA mobiles");
                mGeofencing.registerAllGeofences();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        Log.d("FENCE", "Registering geofences finished");
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i("FENCE", "API Client Connection Suspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("FENCE", "API Client Connection Failed!");
    }
}