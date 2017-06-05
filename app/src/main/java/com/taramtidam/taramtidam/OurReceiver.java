package com.taramtidam.taramtidam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Lilach Fishman on 26/05/2017.
 */

public class OurReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "haleluja", Toast.LENGTH_LONG).show();
        Log.i("INTENT","got broadcast");

    }
}
