package com.taramtidam.taramtidam;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by Lilach Fishman on 26/05/2017.
 */

public class OurPullService extends IntentService {
    private Context context;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public OurPullService(String name) {
        super(name);
    }
    public OurPullService(){
        super("string");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);
        return START_STICKY;
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int i =0;
        while(i<10) {
             /*
         * Creates a new Intent to get the full picture for the thumbnail that the user clicked.
         * The full photo is loaded into a separate Fragment
         */
            // Broadcasts the Intent to receivers in this app. See DisplayActivity.FragmentDisplayer.




            Intent newintent = new Intent(Constants.BROADCAST_ACTION);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(newintent);
        //    LocalBroadcastManager.getInstance(this.context, sendBroadcast(intent));
            Log.i("INTENT", "kookoooo");
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
    }
}
