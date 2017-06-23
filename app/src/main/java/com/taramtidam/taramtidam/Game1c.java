package com.taramtidam.taramtidam;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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

public class Game1c extends Fragment implements View.OnClickListener{

    Button nextButton;



    public Game1c() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_game1c, container, false);

        //set logout button onCliclListener
        nextButton = (Button) rootView.findViewById(R.id.nextButton1);
        nextButton.setOnClickListener(this);

        //load backgroud image
        //ImageView background_image_view = (ImageView)rootView.findViewById(R.id.imageView2);
        //Glide.with(getContext()).load(R.drawable.gameinstuctions3).into(background_image_view);

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

        if (buttonId == R.id.nextButton1) {
            Fragment f = new Game2();

            if (f != null) {
                Log.d("Game Activity", "loading game1 fragemnt...");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, f);
                fragmentTransaction.commit();

                // set the toolbar title
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Game Registration");

            }
        }
    }
}
