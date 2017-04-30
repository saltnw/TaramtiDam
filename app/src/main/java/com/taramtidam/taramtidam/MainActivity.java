package com.taramtidam.taramtidam;

import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG ="Main activity" ;
    private FirebaseAuth mAuth;                             // firebase auth object
    private FirebaseAuth.AuthStateListener mAuthListener;   // firebase auth listener

    private static final int RC_SIGN_IN = 12;               // return code from firebase UI

    TaramtiDamUser currentLoggedUser = null ;               // holds the current logged user
    //Geofencing
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private GoogleApiClient mClient;
    private Geofencing mGeofencing;
    List<MDAMobile> mobiles =new ArrayList<>();
    private boolean isFinished = false;


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
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                 ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.d("FENCE","Initializing Google API Client");
        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)//todo remove?
                .enableAutoManage(this, this)
                .build();

        Log.d("FENCE","Get reference to Firebase Database at MDA");
        // Get a reference to our MDA mobiles
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("MDA");

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("FENCE","Updating MDA mobiles from the database");
                Iterable<DataSnapshot> locations= dataSnapshot.getChildren();
                int i=0;
                while (locations.iterator().hasNext() && i<10) {
                    MDAMobile currMda = new MDAMobile();
                    DataSnapshot nextMDALoc =locations.iterator().next();
                 //   String nextMDALoc = String.valueOf(locations.iterator().next());
                   // System.out.println(nextMDALoc);
                    currMda.setCity(nextMDALoc.child("city").getValue().toString());
                    currMda.setAddress(nextMDALoc.child("description").getValue().toString());
                    currMda.setLongitude(Double.parseDouble(nextMDALoc.child("longitude").getValue().toString()));
                    currMda.setLatitude(Double.parseDouble(nextMDALoc.child("latitude").getValue().toString()));
                    currMda.setTime(nextMDALoc.child("start time").getValue().toString());
                    currMda.setDate(nextMDALoc.child("date").getValue().toString());
                    currMda.setId(String.valueOf(i));

                    mobiles.add(currMda);

                    System.out.println("\n");
                    System.out.println("\n");
                    i++;
                }
                Log.d("FENCE", "Done updating mobiles");
                System.out.println("done");
                mobiles.add(new MDAMobile(32.0852,34.7818,"54"));
                Log.d("FENCE","Going to update geofences list to match new MDA mobiles list");
                mGeofencing.updateGeofencesList(mobiles);
                Log.d("FENCE","Going to register all geofences per all MDA mobiles");
                mGeofencing.registerAllGeofences();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

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
        Log.d("FENCE","Connecting Google API Client");
        mClient.connect();//TODO put it here for now. dosent help
        Log.d("FENCE","Initialize Geofencing object");
        mGeofencing = new Geofencing(this, mClient);
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    public void onStop() {
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.i("FENCE", "API Client Connection Successful!");
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i("FENCE", "API Client Connection Suspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("FENCE", "API Client Connection Failed!");
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
}
