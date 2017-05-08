package com.taramtidam.taramtidam.activity;

/**
 * Created by Asaf on 06/05/2017.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.taramtidam.taramtidam.MainActivity;
import com.taramtidam.taramtidam.R;

import java.util.Arrays;


public class HomeFragment extends Fragment implements View.OnClickListener {

    Button loginBtn;
    private static final int RC_SIGN_IN = 12;               // return code from firebase UI


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //set onCliclListener
        loginBtn = (Button) rootView.findViewById(R.id.loginButton);
        loginBtn.setOnClickListener(this);

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


        MainActivity.signOutFromOurApplication();
        //AuthUI.getInstance().signOut();
        Log.d("HOME_FREAMENT","login btn click!");
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


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}