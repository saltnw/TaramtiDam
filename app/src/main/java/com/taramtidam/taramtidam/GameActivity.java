package com.taramtidam.taramtidam;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class GameActivity extends AppCompatActivity {

    public static String str = "nitzan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        /*Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "melech");

        setResult(Activity.RESULT_OK,returnIntent);
        finish();*/

        Fragment f = new Game1();

        if (f != null) {
            Log.d("Game Activity", "loading game1 fragemnt...");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.game_body, f);
            fragmentTransaction.commit();
        }
    }






}
