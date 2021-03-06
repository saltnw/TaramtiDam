package com.taramtidam.taramtidam.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.taramtidam.taramtidam.Distances;
import com.taramtidam.taramtidam.MainActivity;
import com.taramtidam.taramtidam.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.module.*;
import com.bumptech.glide.*;
import android.graphics.drawable.Drawable;
import  android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.taramtidam.taramtidam.R;

public class DonateNowFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    GoogleApiClient mGoogleApiClient;
    TextView addressTV;
    TextView hoursTV;
    TextView informationTV;
    ImageView addressIV;
    ImageView hoursIV;
    ImageView informationIV;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Connect to Google API
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        Log.d("asaf","DN the size is " +MainActivity.mobiles.size());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_donatenow, container, false);

        addressIV = (ImageView)rootView.findViewById(R.id.adressIcon);
        hoursIV = (ImageView)rootView.findViewById(R.id.hoursIcon);
        informationIV = (ImageView)rootView.findViewById(R.id.infoIcon);

        //load images
        Glide.with(getContext()).load(R.drawable.locationicon).into(addressIV);
        Glide.with(getContext()).load(R.drawable.hoursicon).into(hoursIV);
        Glide.with(getContext()).load(R.drawable.sakitdamicon).into(informationIV);

       /* Button a = (Button) rootView.findViewById(R.id.naviagteButton);
        Glide.with(getContext()).load(R.drawable.navigatebtnbackground).into(new SimpleTarget<Bitmap>(200, 200) {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                // Do something with bitmap here.
            }
        });*/



        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onStart(){
        mGoogleApiClient.connect();
        super.onStart();
        ((Button) getView().findViewById(R.id.naviagteButton)).setOnClickListener(this);

    }

    @Override
    public void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();

    }

    public void onClick(View arg0) {

        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri intentUri = Uri.parse("google.navigation:q="+addressTV.getText()+"&mode=d");

        // Create an Intent from intentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);

        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

        // Attempt to start an activity that can handle the Intent
        startActivity(mapIntent);

    }

        @Override
    public void onConnected(Bundle connectionHint) {
        addressTV = (TextView) getView().findViewById(R.id.addressTextView);
        hoursTV = (TextView) getView().findViewById(R.id.hoursTextView);
        informationTV = (TextView) getView().findViewById(R.id.infoTextView);
        String openings_hours;
        String info;
        String address;

        int nearsetStationIndex;
        if (ActivityCompat.checkSelfPermission(this.getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (MainActivity.mobiles.size() > 0) { //check if the there are mobiles (at least 1)
            if (mLastLocation != null) {
                Log.d("Donate Now Fragment", "mLastLocation != NULL");

                if (Distances.findNearestBloodMobile(mLastLocation.getLatitude(), mLastLocation.getLongitude(), MainActivity.mobiles).getMobileIndex() == -1) {
                    addressTV.setText("No Active Station Available");
                    hoursTV.setText("N/A");
                    informationTV.setText("N/A");
                    return;
                }
                nearsetStationIndex = Distances.findNearestBloodMobile(mLastLocation.getLatitude(), mLastLocation.getLongitude(), MainActivity.mobiles).getMobileIndex();
                Log.d("Donate Now Fragment", "The closest station index is: " + Integer.toString(nearsetStationIndex));
                Log.d("Donate Now Fragment", "The numbers of stations is: " + MainActivity.mobiles.size());
                Log.d("Donate Now Fragment", "The Closest station is: " + MainActivity.mobiles.get(nearsetStationIndex).getAddress() + ", " + MainActivity.mobiles.get(nearsetStationIndex).getCity() +"\n"+MainActivity.mobiles.get(nearsetStationIndex).getDescription());
                //((TextView) getView().findViewById(R.id.nearsetStationTextView)).setText("Right Now the Nearset Station is located at: "+ MainActivity.mobiles.get(nearsetStationIndex).getAddress() +", "  +MainActivity.mobiles.get(nearsetStationIndex).getCity());
                address = MainActivity.mobiles.get(nearsetStationIndex).getAddress() + "\n" + MainActivity.mobiles.get(nearsetStationIndex).getCity() + "\n"+MainActivity.mobiles.get(nearsetStationIndex).getDescription();
                addressTV.setText(address);
                openings_hours = "From: " + (MainActivity.mobiles.get(nearsetStationIndex).getTime()) + " To: " + (MainActivity.mobiles.get(nearsetStationIndex).getEndTime());
                hoursTV.setText(openings_hours);


                if (MainActivity.currentLoggedUser.getDonationsCounter() == 0) {
                    info = "You still haven't donated yet. Go for it!";
                } else {
                    info = "Your last donation was at: " + MainActivity.currentLoggedUser.getLastDonationInString();
                    info += "\n";
                    if (MainActivity.currentLoggedUser.daysFromLastDonation() > 90) {
                        info += "Seems like you haven't donated for a while\nGo for it!";
                    } else {
                        info += "For your safety, it is recommanded to\nwait at least 3 monthes between donations ";
                    }

                }
                informationTV.setText(info);
            }
        }
        else{
            //if there are no blood mobiles on MainActivity.mobiles array
            addressTV.setText("There are no stations today\n Please check tomorrow");
            hoursTV.setText("");
            informationTV.setText("");

        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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

