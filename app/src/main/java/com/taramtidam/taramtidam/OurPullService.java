package com.taramtidam.taramtidam;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lilach Fishman on 26/05/2017.
 */

public class OurPullService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Context context;
    public static GoogleApiClient mClient;
    private Geofencing mGeofencing;
    public static List<MDAMobile> mobiles = new ArrayList<>();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public OurPullService(String name) {
        super(name);
    }

    public OurPullService() {
        super("string");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
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
        mClient.connect();//TODO put it here for now. dosent help
        Log.d("FENCE","Initialize Geofencing object");
        mGeofencing = new Geofencing(this, mClient);

        Log.d("FENCE", "Get reference to Firebase Database at MDA");
        // Get a reference to our MDA mobiles
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("MDA").child("Today");
        DatabaseReference db_details = database.getReference("users").child("4KULKNGfznR7hdCRFmms5RVC2mn1").child("completedRegisteration");
        db_details.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value =  dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

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
                    //   String nextMDALoc = String.valueOf(locations.iterator().next());
                    // System.out.println(nextMDALoc);
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
                mGeofencing.updateGeofencesList(mobiles);
                Log.d("FENCE", "Going to register all geofences per all MDA mobiles");
                mGeofencing.registerAllGeofences();

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }





    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("FENCE", "API Client Connection Successful!");
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