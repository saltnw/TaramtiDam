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

/**
 * Created by Asaf on 08/06/2017.
 */

public class Game2 extends Fragment implements View.OnClickListener{

    Button nextButton;


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
        nextButton = (Button) rootView.findViewById(R.id.nextButton2);
        nextButton.setOnClickListener(this);

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

        if (buttonId == R.id.nextButton2) {
            Fragment f = new Game3();

            if (f != null) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_game2, f);
                fragmentTransaction.commit();


            }
        }
    }

}

