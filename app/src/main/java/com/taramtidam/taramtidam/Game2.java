package com.taramtidam.taramtidam;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.taramtidam.taramtidam.MainActivity.currentLoggedUser;

/**
 * Created by Asaf on 08/06/2017.
 */

public class Game2 extends Fragment implements View.OnClickListener {

    Button dayVampires;
    Button nightVampires;

    public Game2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_game2, container, false);

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
         String team = null;

        if (buttonId == R.id.dayvampireButton) {
            team = "Day";
            HandleTeam(team);
        }
        if (buttonId == R.id.nightvampireButton) {
            team = "Night";
            HandleTeam(team);
        }

        //switch to the next fragment
        Fragment f = new Game3();
        if (f != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_game2, f);
            fragmentTransaction.commit();
        }
    }

    private void HandleTeam (String team){
        String userId = currentLoggedUser.getuid();
        Log.d("GAME2", "user id is " + userId);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userId).child("team").child("vemp").setValue(team);
        MainActivity.currentLoggedUser.getTeam().setVemp(team);
        //MainActivity.team = team;

        Log.d("GAME2","The chosen team is " + team);
    }
}