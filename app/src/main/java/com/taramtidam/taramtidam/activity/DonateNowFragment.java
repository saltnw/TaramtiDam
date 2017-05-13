package com.taramtidam.taramtidam.activity;

import android.app.Activity;
import android.location.Location;
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
import android.widget.TextView;

import com.taramtidam.taramtidam.R;

public class DonateNowFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;


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

    }

    @Override
    public void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //TextView mLatitudeText = (TextView)getView().findViewById(R.id.textView3);
        //TextView mLongitudeText = (TextView)getView().findViewById(R.id.textView2);
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
            ((TextView) getView().findViewById(R.id.nearsetStationTextView)).setText("Right Now the Nearset Station is located at: "+ MainActivity.mobiles.get(nearsetStationIndex).getAddress() +", "  +MainActivity.mobiles.get(nearsetStationIndex).getCity());
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

