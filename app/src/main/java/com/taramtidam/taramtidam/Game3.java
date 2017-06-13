package com.taramtidam.taramtidam;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.taramtidam.taramtidam.activity.HomeFragment;

import java.util.Arrays;

/**
 * Created by Asaf on 08/06/2017.
 */

public class Game3 extends Fragment implements View.OnClickListener {

    Button dayVampires;
    Button nightVampires;

    public Game3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_game3, container, false);

        //set logout button onCliclListener
        dayVampires = (Button) rootView.findViewById(R.id.dayvampireButton);
        dayVampires.setOnClickListener(this);

        //set logout button onCliclListener
        nightVampires = (Button) rootView.findViewById(R.id.nightvampireButton);
        nightVampires.setOnClickListener(this);

        //load day vampire image
        ImageView day_vampire_image_view = (ImageView)rootView.findViewById(R.id.selectteamdayvampiresImageView);
        Glide.with(getContext()).load(R.drawable.dayvampirewithtitle).into(day_vampire_image_view);

        //load night vampire image
        ImageView night_vampire_image_view = (ImageView)rootView.findViewById(R.id.selectteamnightvampiresImageView);
        Glide.with(getContext()).load(R.drawable.nightvampirewithtitle).into(night_vampire_image_view);

        //load title image
        ImageView title_image_view = (ImageView)rootView.findViewById(R.id.selectteamtitleImageView);
        Glide.with(getContext()).load(R.drawable.selectyourteam).into(title_image_view);

        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onClick(View arg0) {

        int buttonId = arg0.getId();
        int team = 0;

        if (buttonId == R.id.dayvampireButton) {
            team = 1;
            HandleTeam(team);
        }
        if (buttonId == R.id.nightvampireButton) {
            team = 2;
            HandleTeam(team);
        }

        //switch to the next fragment
        Fragment f = new Game4();
        if (f != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_game2, f);
            fragmentTransaction.commit();
        }
    }

    private void HandleTeam (int team){
        //TODO
        Log.d("GAME3","The chosen team is " + team);
    }
}