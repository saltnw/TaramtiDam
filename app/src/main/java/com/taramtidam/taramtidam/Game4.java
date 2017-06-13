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

import com.taramtidam.taramtidam.activity.HomeFragment;

import java.util.Arrays;

/**
 * Created by Asaf on 08/06/2017.
 */

public class Game4 extends Fragment implements View.OnClickListener {

    Button backToMain;

    public Game4() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_game4, container, false);

        //set logout button onCliclListener
        backToMain = (Button) rootView.findViewById(R.id.backToMainButton);
        backToMain.setOnClickListener(this);

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

        if (buttonId == R.id.backToMainButton) {

            //switch to the next fragment
            Fragment f = new HomeFragment();

            if (f != null) {
                Log.d("Game Activity", "loading again home fragemnt...");

                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, f);
                fragmentTransaction.commit();

            }
        }
    }
}
