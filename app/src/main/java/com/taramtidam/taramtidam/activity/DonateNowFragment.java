package com.taramtidam.taramtidam.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import android.widget.TextView;

import com.taramtidam.taramtidam.R;

public class DonateNowFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    GoogleApiClient mGoogleApiClient;
    TextView addressTV;
    TextView hoursTV;
    TextView informationTV;


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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_donatenow, container, false);


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
        Uri intentUri = Uri.parse("google.navigation:q="+addressTV.getText()+"&mode=w");

        // Create an Intent from intentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);

        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

        // Attempt to start an activity that can handle the Intent
        startActivity(mapIntent);

    }

        @Override
    public void onConnected(Bundle connectionHint) {
        addressTV = (TextView)getView().findViewById(R.id.addressTextView);
        hoursTV = (TextView)getView().findViewById(R.id.hoursTextView);
        informationTV = (TextView)getView().findViewById(R.id.infoTextView);
        String openings_hours;
        String info;
        String address;

        int nearsetStationIndex;
        //Log.d("ASAF","google api connected!");
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient); //TODO to check it with lilach
        if (mLastLocation != null) {
            Log.d("Donate Now Fragment","mLastLocation != NULL");

            //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            //Log.d("ASAF", "the are :"+Integer.toString(MainActivity.mobiles.size()) + " mobiles");

            nearsetStationIndex = Distances.findNearestBloodMobile(mLastLocation.getLatitude() ,mLastLocation.getLongitude(),MainActivity.mobiles );
            Log.d("Donate Now Fragment", "The closest station index is: "+ Integer.toString(nearsetStationIndex) );
            Log.d("Donate Now Fragment","The Closest station is: "+ MainActivity.mobiles.get(nearsetStationIndex).getAddress() +", " +MainActivity.mobiles.get(nearsetStationIndex).getCity());
            //((TextView) getView().findViewById(R.id.nearsetStationTextView)).setText("Right Now the Nearset Station is located at: "+ MainActivity.mobiles.get(nearsetStationIndex).getAddress() +", "  +MainActivity.mobiles.get(nearsetStationIndex).getCity());
            address = MainActivity.mobiles.get(nearsetStationIndex).getAddress() +"\n"+MainActivity.mobiles.get(nearsetStationIndex).getCity();
            addressTV.setText(address);
            openings_hours = "From: "+(MainActivity.mobiles.get(nearsetStationIndex).getTime()) + " To: "+ (MainActivity.mobiles.get(nearsetStationIndex).getEndTime());
            hoursTV.setText(openings_hours);

            if (MainActivity.currentLoggedUser.getDonationsCounter() == 0){
                info = "You still haven't donated yet. Go for it!";
            }
            else{
                info = "Your last donation was at: "+MainActivity.currentLoggedUser.getLastDonationInString();
                info += "\n";
                if (MainActivity.currentLoggedUser.daysFromLastDonation() > 90){
                    info += "Seems like you haven't donated for a while\nGo for it!";
                }
                else{
                    info+= "For your safety, it is recommanded to\nwait at least 3 monthes between donations ";
                }

            }
            informationTV.setText(info);




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

