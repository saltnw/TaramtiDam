package com.taramtidam.taramtidam;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    public static final String TAG ="AsafMSG" ;
    private FirebaseAuth mAuth;                             // firebase auth object
    private FirebaseAuth.AuthStateListener mAuthListener;   // firebase auth listener

    private static final int RC_SIGN_IN = 12;               // return code from firebase UI

    TaramtiDamUser currentLoggedUser = null ;               // holds the current logged user
    private GoogleApiClient mGoogleApiClient;
    private GeofencingRequest request;
    private PendingIntent mGeofencePendingIntent;
    private List<Geofence> mGeofenceList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();                               // get the shared instance of the FirebaseAuth object:
        TextView infotv = (TextView) findViewById(R.id.infoTextView);    // get a reference to the infoTextView

        //check if the user is already sigen in or not
        if (mAuth.getCurrentUser() != null) {
            // already signed in

            infotv.setText("welcome " + mAuth.getCurrentUser().getUid());  // update the TextView text
        } else {
            //not sigen in
            //infotv.setText("welcome guest!" );                  // update the TextView text
        }

        /* set listeners to the buttons in the view */
        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById(R.id.logoutButton).setOnClickListener(this);

        /* define a listener for changing the auth state  */
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user;                          // current user
                user = firebaseAuth.getCurrentUser();
                TextView infotv = (TextView) findViewById(R.id.infoTextView);    // get a reference to the infoTextView
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    findViewById(R.id.logoutButton).setEnabled(true);
                    findViewById(R.id.loginButton).setEnabled(false);
                    finishLoginAndRegistration();




                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    infotv.setText("Hello, \nPlease sign in to continue");                  // update the TextView text
                    findViewById(R.id.logoutButton).setEnabled(false);
                    findViewById(R.id.loginButton).setEnabled(true);
                    currentLoggedUser = null;

                }
                // ...

            }
        };

        ///------------------------------------------------------/////////////////
        mGeofencePendingIntent = null;
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        List<Geofence> mGeofenceList = new ArrayList<>();
        MDAMobile mda1 = new MDAMobile(1.1, 2.2);
        List<MDAMobile> mdaMobiles= new ArrayList<>();
        mdaMobiles.add(mda1);
        int i = 0;
        for( MDAMobile mobile : mdaMobiles) { // going over the blood-mobiles and add a fence to each of them.
            i++;
            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(String.format("MDA{}",i))
                    .setCircularRegion(
                            mobile.getLatitude(),
                            mobile.getLongitude(),
                            Constants.GEOFENCE_RADIUS_IN_METERS)
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)// user linger in the location
                    .build());
        }



    }

    private GeofencingRequest getGeofencingRequest() {

        request = new GeofencingRequest.Builder()
               // Notification to trigger when the Geofence is created
               .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
               .addGeofences(mGeofenceList) // add the Geofences list
               .build();
        return request;
    }


    /* define behavior to the buttons on the view  */
    public void onClick(View arg0) {

        int i = arg0.getId();
        // login button was pressed
        if ( i == R.id.loginButton){

            Log.i(TAG, "login btn was pressed;");
            //Button btn = (Button)arg0;                                       // cast view to a button
            TextView infotv = (TextView) findViewById(R.id.infoTextView);    // get a reference to the infoTextView

            /* run firebaseUI login screen, with mail and facebook options */
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);

            //firebaseUI finished!




        }
        //logout button was pressed
        if ( i == R.id.logoutButton){
            Log.i(TAG, "logout btn was pressed;");
            mAuth.signOut();
            AuthUI.getInstance().signOut(this);   // logout
            LoginManager.getInstance().logOut();  // clear facebook login also
            Toast.makeText(MainActivity.this, R.string.auth_signout_success,
                    Toast.LENGTH_SHORT).show();  // display message
        }
    }



    @Override
    protected void onStart(){
        mGoogleApiClient.connect();
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private void finishLoginAndRegistration() {

        //check if the user really completed the login using FirebaseUI
        if (mAuth.getCurrentUser() == null){
            Log.d("Login", "the user closed firbaseUI and didnt login / register" );
            return;

        }

        //now lets check if the user is already in our database
         FirebaseDatabase database = FirebaseDatabase.getInstance();
         DatabaseReference usersDatabase = database.getReference("users");

        ((ProgressBar)findViewById(R.id.loadprofileProgressBar)).setVisibility(View.VISIBLE); //display the progress bar


        //using listener get all the users with the same uid
        usersDatabase.orderByKey().equalTo(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DatabaseReference mDatabase;

                //check if the snapshot we got back is not empty (meaning there is no user with uid like this)
                if (snapshot.hasChild(mAuth.getCurrentUser().getUid())){
                    //we found the user in the database

                    // set the current user to the user we found
                    currentLoggedUser = snapshot.child(mAuth.getCurrentUser().getUid()).getValue(TaramtiDamUser.class); //get an object of the user
                    Log.d(TAG,"currentLoggedUser now contain the logged user profile: "+currentLoggedUser.getFullName());
                    displayProfileAfterLoadingfromDtabase();
                    ((ProgressBar)findViewById(R.id.loadprofileProgressBar)).setVisibility(View.GONE); //remove the progress bar

                }
                else{
                    //user is not in database -> so do  a registration
                    Log.d(TAG,"user was not found in datatbase");

                    // create the user a new profile and insert him to the database
                    currentLoggedUser = new TaramtiDamUser(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getEmail() , "Tel Aviv", "a+" );
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").child(currentLoggedUser.getuid()).setValue(currentLoggedUser);
                    Log.d(TAG, "new user was created in database");

                    displayProfileAfterLoadingfromDtabase();
                    ((ProgressBar)findViewById(R.id.loadprofileProgressBar)).setVisibility(View.GONE); //remove the progress bar
                    Toast.makeText(MainActivity.this, R.string.auth_registration_completed,
                            Toast.LENGTH_SHORT).show();  // display message

                }

            }
            @Override public void onCancelled(DatabaseError error) { }


        });




    }

    /*
    this method take the data that is currently stored on 'currentLoggedUser' and display it

    call this method to display the profile after it was loaded from database to "currentLoggedUser" var
    make sure that you call this function only from a firebase database listener, otherwise it is not guaranteed that "currentLoggedUser" contain data at all
    */
    private void displayProfileAfterLoadingfromDtabase (){
        ((TextView)(findViewById(R.id.infoTextView))).setText("logged in!\n"+"userid: "+ currentLoggedUser.getuid() + "\n"+currentLoggedUser.getFullName()+"\n"+currentLoggedUser.getEmail() + "\n" );     // update the TextView text
        //
        // add here for more things to do with profile
        //

    }


    /*
    this function receives a 'uid' and set a listener that retrieve a profile to 'currentLoggedUser' and display it on screen
    used for example when the user exit the app return after a while. that user may be still logged in, we need to load the profile again
     */
    private void loadUserProfileFromDatabase(final String uid){

        //set progress bar next to profile
        ((ProgressBar)findViewById(R.id.loadprofileProgressBar)).setVisibility(View.VISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersDatabase = database.getReference("users");

        usersDatabase.orderByKey().equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DatabaseReference mDatabase;

                //check if the sanpshot we got back is not empty (meaning there is no user with uid like this)
                if (snapshot.hasChild(uid)){
                    //we found the user in the database

                    // set the current user to the user we found
                    currentLoggedUser = snapshot.child(uid).getValue(TaramtiDamUser.class); //get an object of the user
                    Log.d(TAG,"currentLoggedUser now contain the logged user profile: "+currentLoggedUser.getFullName());
                    //after loading from datatbase, set the TextView with the user pofile data
                    displayProfileAfterLoadingfromDtabase();
                    ((ProgressBar)findViewById(R.id.loadprofileProgressBar)).setVisibility(View.GONE); //remove the progress bar = finished loading
                }
                else{
                    //user is not in datatbase
                    Log.d(TAG,"could not find user in database");
                    ((ProgressBar)findViewById(R.id.loadprofileProgressBar)).setVisibility(View.GONE); //remove the progress bar = finished loading


                }

            }
            @Override public void onCancelled(DatabaseError error) { }


        });



    }



    //function for custom email + password login. CAN BE DELETED
   /*
    private void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }
    */

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            // logSecurityException(securityException);TODO return this line and implement method
        }
    }

    public void onResult(Status status) {
    }





    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
