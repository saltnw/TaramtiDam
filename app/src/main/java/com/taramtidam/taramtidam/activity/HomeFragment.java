package com.taramtidam.taramtidam.activity;

/**
 * Created by Asaf on 06/05/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.taramtidam.taramtidam.Game1;
import com.taramtidam.taramtidam.GameActivity;
import com.taramtidam.taramtidam.MainActivity;
import com.taramtidam.taramtidam.R;

import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;


public class HomeFragment extends Fragment implements View.OnClickListener {

    Button loginBtn;
    Button logoutbtn;
    Button gamebtn;
    private static final int RC_SIGN_IN = 12;               // return code from firebase UI


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("asaf","HF the size is " +MainActivity.mobiles.size());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        View rootView = inflater.inflate(R.layout.fragment_home,  null);

        //set login button onCliclListener
        loginBtn = (Button) rootView.findViewById(R.id.loginButton);
        loginBtn.setOnClickListener(this);

        //set logout button onCliclListener
        logoutbtn = (Button) rootView.findViewById(R.id.logoutButton);
        logoutbtn.setOnClickListener(this);

        //set game button onCliclListener
        gamebtn = (Button) rootView.findViewById(R.id.gamecubeButton);
        gamebtn.setOnClickListener(this);

        if (MainActivity.currentLoggedUser == null){

            loginBtn.setVisibility(View.VISIBLE);
            loginBtn.setEnabled(true);

            logoutbtn.setVisibility(View.INVISIBLE);
            logoutbtn.setEnabled(false);
        }
        else{
            loginBtn.setVisibility(View.INVISIBLE);
            loginBtn.setEnabled(false);

            logoutbtn.setVisibility(View.VISIBLE);
            logoutbtn.setEnabled(true);

            //check if to display join the game or not
            if(MainActivity.currentLoggedUser.isAlreadyJoinedTheGame()==true){
                //user joined the game so remove game button and display the correct team

                //hide "join the game" button
                gamebtn.setVisibility(View.INVISIBLE);
                gamebtn.setEnabled(false);

                //show correct team image
                if (MainActivity.currentLoggedUser.getTeam().getVemp().equals("Night")){
                    ImageView vampire_image_view = (ImageView)rootView.findViewById(R.id.vampireImageView);
                    Glide.with(getContext()).load(R.drawable.youareanightvampire).into(vampire_image_view);

                }
                else if(MainActivity.currentLoggedUser.getTeam().getVemp().equals("Day")){
                    ImageView vampire_image_view = (ImageView)rootView.findViewById(R.id.vampireImageView);
                    Glide.with(getContext()).load(R.drawable.youareadayvamprie).into(vampire_image_view);
                }

            }
            else{
                //user didnt signup for the game yet so show the game button
                gamebtn.setVisibility(View.VISIBLE);
                gamebtn.setEnabled(true);

            }

        }

        //load tipat dam image
        ImageView tipat_dam_image_view = (ImageView)rootView.findViewById(R.id.tipatDamImgaeView);
        Glide.with(getContext()).load(R.drawable.blooddrop).into(tipat_dam_image_view);





        // Inflate the layout for this fragment
        return rootView;

    }

    @Override
    public void onStart(){
        super.onStart();
        //TextView myAwesomeTextView = (TextView)getView().findViewById(R.id.WelcomTV);
        //myAwesomeTextView.setText(MainActivity.testString);
    }




    public void onClick(View arg0) {

        int buttonId = arg0.getId();

        if (buttonId == R.id.loginButton) {
            Log.d("HOME_FREAMENT", "login btn click!");
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                            .setTheme(R.style.MyMaterialTheme)
                            .build(),
                    RC_SIGN_IN);
        }
        if (buttonId == R.id.logoutButton){
            Log.d("HOME_FREAMENT", "logout btn click!");
            MainActivity.signOutFromOurApplication();
            Toast.makeText(getApplicationContext(), R.string.auth_signout_success, Toast.LENGTH_SHORT).show();

        }

        if (buttonId == R.id.gamecubeButton){
            Log.d("HOME_FREAMENT", "game cube btn click!");

            Fragment f = new Game1();

            if (f != null) {
                Log.d("Game Activity", "loading game1 fragemnt...");

                //Not good
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

                //TODO save the state of the login + logout + join the game buttons

                //TODO set all the buttons to Disabled

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_game2, f);
                fragmentTransaction.commit();
            }



        }

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}