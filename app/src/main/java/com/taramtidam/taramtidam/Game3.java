package com.taramtidam.taramtidam;

import android.app.Activity;
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
import com.bumptech.glide.load.model.StringLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.taramtidam.taramtidam.MainActivity.currentLoggedUser;
//import static com.taramtidam.taramtidam.MainActivity.team;

/**
 * Created by Asaf on 08/06/2017.
 */

public class Game3 extends Fragment implements View.OnClickListener{

    Button nextButton;
    Button southButton;
    Button northButton;
    Button eastButton;
    Button westButton;
    public static String area;


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

        //set next button onCliclListener
        //nextButton = (Button) rootView.findViewById(R.id.nextButton2);
        //nextButton.setOnClickListener(this);

        //set west button onCliclListener
        westButton = (Button) rootView.findViewById(R.id.mapwestButton);
        westButton.setOnClickListener(this);

        //set east button onCliclListener
        eastButton = (Button) rootView.findViewById(R.id.mapeastButton);
        eastButton.setOnClickListener(this);

        //set north button onCliclListener
        northButton = (Button) rootView.findViewById(R.id.mapnorthButton);
        northButton.setOnClickListener(this);

        //set south button onCliclListener
        southButton = (Button) rootView.findViewById(R.id.mapsouthButton);
        southButton.setOnClickListener(this);

        //load map image
        ImageView map_image_view = (ImageView)rootView.findViewById(R.id.mapChooseAreaImageView);
        Glide.with(getContext()).load(R.drawable.map).into(map_image_view);

        //load title image
        ImageView title_image_view = (ImageView)rootView.findViewById(R.id.selectareatitleImageView);
        Glide.with(getContext()).load(R.drawable.selectareatext).into(title_image_view);

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

        area = null;
        int buttonId = arg0.getId();

        if (buttonId == R.id.mapsouthButton) {
            area = "South";
            HandleArea(area);
        }
        if (buttonId == R.id.mapeastButton) {
            area = "East";
            HandleArea(area);
        }
        if (buttonId == R.id.mapnorthButton) {
            area = "North";
            HandleArea(area);
        }
        if (buttonId == R.id.mapwestButton) {
            area = "West";
            HandleArea(area);
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

    private void HandleArea (String area){
        String userId = currentLoggedUser.getuid();
        Log.d("GAME3", "The  chosen area is " + area);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userId).child("team").child("area").setValue(area);
        mDatabase.child("users").child(userId).child("alreadyJoinedTheGame").setValue(true);

        MainActivity.currentLoggedUser.getTeam().setArea(area);
        MainActivity.currentLoggedUser.setAlreadyJoinedTheGame(true);

        final String AREA = area;
        final String UID = userId;
        final String VEMP = MainActivity.currentLoggedUser.getTeam().getVemp();


        final DatabaseReference gameRef =  mDatabase.child("Game");
        //add the user to the team
        Map<String,Object> map = new HashMap<String, Object>();
        map.put(UID,UID);
        gameRef.child(AREA).child(VEMP).child("Users").updateChildren(map);

        //increase the global game members counter
        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long globalMembers = (long)dataSnapshot.child("_GlobalMemberCounter").getValue();
                globalMembers++;
                gameRef.child("_GlobalMemberCounter").setValue(globalMembers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //increase number of members in area
        final DatabaseReference areaRef =  mDatabase.child("Game").child(AREA);
        areaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long members = (long) dataSnapshot.child("Members").getValue();
                members++;
                areaRef.child("Members").setValue(members);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //increase number of members in team (team = area + day or night)
        final DatabaseReference teamRef =  mDatabase.child("Game").child(AREA).child(VEMP);
        teamRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long members = (long)dataSnapshot.child("Members").getValue();
                members++ ;
                teamRef.child("Members").setValue(members);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

