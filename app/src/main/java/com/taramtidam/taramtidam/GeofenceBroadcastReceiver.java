package com.taramtidam.taramtidam;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import java.text.ParseException;

import static com.facebook.FacebookSdk.getApplicationContext;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();
    private String MDANear;

    /***
     * Handles the Broadcast message sent when the Geofence Transition is triggered
     * Careful here though, this is running on the main thread so make sure you start an AsyncTask for
     * anything that takes longer than say 10 second to run
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("FENCE","Received location in broadcast receiver");
        // Get the Geofence Event from the Intent sent through
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, String.format("Error code : %d", geofencingEvent.getErrorCode()));
            return;
        }

        boolean isNotificationOn = true;
        //read the value for the notification flag from file
        try {
            FileInputStream fis = getApplicationContext().openFileInput("geofencesNotification");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line + "\n");
            }
            Log.d("FENCE", "File content: "+sb.toString());

            if (sb.toString().contains("OFF")){
                isNotificationOn = false;
                Log.d("FENCE", "Notification settings read from file and now set to OFF");
            }
        }
        catch (Exception e) {
            Log.d("FENCE", "Error reading from file"+e.toString());
        }

        if (isNotificationOn) {
            // Send the notification
            Geofence geofence = geofencingEvent.getTriggeringGeofences().get(0);
            MDANear = geofence.getRequestId();
            String[] details = MDANear.split("@");
            String startTime = details[1];
            String endTime = details[2];
            DateFormat date = new SimpleDateFormat("HH:mm");
            Date start = null;
            Date end = null;

            try {
                start = date.parse(startTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                end = date.parse(endTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jerusalem"));
            Date currentLocalTime = cal.getTime();

            date.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
            String localTime = date.format(currentLocalTime);
            Date local = null;
            try {
                local = date.parse(localTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (local.getTime() > start.getTime() && local.getTime() < end.getTime()) {
                sendNotification(context, details[0]);
                Log.d("FENCE", "Notification was sent");
                List toRemove= new ArrayList();//TODO remove and handle double notifications
                toRemove.add(MDANear);
                if (OurPullService.mClient != null)
                {
                    LocationServices.GeofencingApi.removeGeofences(OurPullService.mClient, toRemove);
                    Log.d("FENCE", "geofence removed " + details[0]);
                }
            } else {
                Log.d("FENCE", "Not in operating hours. Notification was not sent");
            }
        }
        else {
            Log.d("FENCE", "Notifications are muted");
        }
    }


    /**
     * Posts a notification in the notification bar when a transition is detected
     * Uses different icon drawables for different transition types
     * If the user clicks the notification, control goes to the MainActivity
     *
     * @param context        The calling context for building a task stack
     * @param msg The notification text message
     */
    private void sendNotification(Context context, String msg) {
        Log.d("NOTI", "notification should be sent now!");

        String notifyMsg = msg;
        msg.replaceAll(" ","+");


        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri intentUri = Uri.parse("google.navigation:q="+msg+"&mode=w");

        // Create an Intent from intentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);

        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

        // Attempt to start an activity that can handle the Intent
//        startActivity(mapIntent);


        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(context, MainActivity.class);


        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(mapIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Check the transition type to display the relevant icon image

            builder.setSmallIcon(R.mipmap.td)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.mipmap.td))
                    .setContentTitle("ניידת התרמת דם בסביבה");//todo change to this style:context.getString(R.string.silent_mode_activated


        // Continue building the notification
        builder.setContentText(notifyMsg);
        builder.setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }
    
}
