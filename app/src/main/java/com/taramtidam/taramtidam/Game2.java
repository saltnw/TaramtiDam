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

/**
 * Created by Asaf on 08/06/2017.
 */

public class Game2 extends Fragment implements View.OnClickListener{

    Button nextButton;
    Button southButton;
    Button northButton;
    Button eastButton;
    Button westButton;






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

        int area = 0;
        int buttonId = arg0.getId();

        if (buttonId == R.id.mapsouthButton) {
            area = 1;
            HandleArea(area);
        }
        if (buttonId == R.id.mapeastButton) {
            area = 2;
            HandleArea(area);
        }
        if (buttonId == R.id.mapnorthButton) {
            area = 3;
            HandleArea(area);
        }
        if (buttonId == R.id.mapwestButton) {
            area = 4;
            HandleArea(area);
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

    private void HandleArea (int area){
        Log.d("GAME2", "The  chosen area is " + area);
        //TODO
    }
}

