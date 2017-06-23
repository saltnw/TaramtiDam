package com.taramtidam.taramtidam;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taramtidam.taramtidam.activity.DonateNowFragment;
import com.taramtidam.taramtidam.activity.FragmentDrawer;
import com.taramtidam.taramtidam.activity.HomeFragment;
import com.taramtidam.taramtidam.activity.JustDonatedFragment;
import com.taramtidam.taramtidam.activity.MyProfileFragment;
import com.taramtidam.taramtidam.activity.NavigationFragment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class  MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, View.OnClickListener {


    /* login and firebase auth vars*/
    public static final String TAG ="Main activity" ;
    private static FirebaseAuth mAuth;                             // firebase auth object
    private FirebaseAuth.AuthStateListener mAuthListener;   // firebase auth listener

    public static TaramtiDamUser currentLoggedUser = null ;               // holds the current logged user

    public static final String EXTRA_ID = "com.taramtidam.user_id";
    private static final int RC_SIGN_IN = 12;               // return code from firebase UI

    /* Geofencing */
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    public static List<MDAMobile> mobiles =new ArrayList<>();

    /* Notification flag on/off */
    private boolean geofencesNoticiationOn;

    /* Dont show "you must login to continue" toast */
    private boolean dontShowMustLoginToast = true;

    /* UI vars */
    private Toolbar mToolbar; // main upper toolbar
    private FragmentDrawer drawerFragment;

    Fragment fragment = null;
    String title;
    private Intent mServiceIntent;

    //  GAME
    public static GameData gameData = new GameData();
    //public static String team=null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AuthUI.getInstance().signOut(this);   // logout
        SharedPreferences prefs = getSharedPreferences("com.taramtidam.taramtidam", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            prefs.edit().putBoolean("firstrun", false).commit();
            //call the tutorial activity
            Intent tutintent = new Intent(this, tutActi.class);
            startActivity(tutintent);
        }
        //else continue as normal

        setContentView(R.layout.activity_main);

        /* set upper toolbar - UI */
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /* set up drawer fragment */
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);



        displayView(0);         // display the first navigation drawer view on app launch



        /* ---> Firebase-AUTH STUFF GOES HERE < --- */
        mAuth = FirebaseAuth.getInstance();                    // get the shared instance of the FirebaseAuth object:

        if (mAuth.getCurrentUser() != null) {                //check if the user is already sigen in or not

            // already signed in
            Log.d("Main Activity", "User is already looged in...");

        } else {
            //not sigen in
            //infotv.setText("welcome guest!" );                  // update the TextView text
        }
        /* define a listener for changing the auth state  */
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user;                          // current user
                user = firebaseAuth.getCurrentUser();
                //TextView infotv = (TextView) findViewById(R.id.infoTextView);    // get a reference to the infoTextView
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //findViewById(R.id.logoutButton).setEnabled(true);
                    //findViewById(R.id.logoutButton).setVisibility(View.VISIBLE);
                    //findViewById(R.id.EditProfileButton).setEnabled(true);
                    //findViewById(R.id.EditProfileButton).setVisibility(View.VISIBLE);
                    //findViewById(R.id.loginButton).setEnabled(false);
                    //findViewById(R.id.loginButton).setVisibility(View.INVISIBLE);

                    finishLoginAndRegistration();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                    //infotv.setText("" );                  // update the TextView text

                    //findViewById(R.id.logoutButton).setEnabled(false);
                    //findViewById(R.id.logoutButton).setVisibility(View.INVISIBLE);
                    //findViewById(R.id.EditProfileButton).setEnabled(false);
                    //findViewById(R.id.EditProfileButton).setVisibility(View.INVISIBLE);
                   // findViewById(R.id.loginButton).setEnabled(true);
                    //findViewById(R.id.loginButton).setVisibility(View.VISIBLE);

                    currentLoggedUser = null;
                    doThingsAfterLogout();

                }
                // ...
            }
        };


        /* MDA STUFF GOES HERE */

        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                 ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }

        Log.d("FENCE","Get reference to Firebase Database at MDA");
        // Get a reference to our MDA mobiles
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("MDA").child("Today");

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("FENCE","Updating MDA mobiles from the database");
                Iterable<DataSnapshot> locations= dataSnapshot.getChildren();
                int i=0;
                while (locations.iterator().hasNext()) {
                    MDAMobile currMda = new MDAMobile();
                    DataSnapshot nextMDALoc =locations.iterator().next();
                    currMda.setCity(nextMDALoc.child("city").getValue().toString());
                    currMda.setAddress(nextMDALoc.child("address").getValue().toString());
                    currMda.setLongitude(Double.parseDouble(nextMDALoc.child("longitude").getValue().toString()));
                    currMda.setLatitude(Double.parseDouble(nextMDALoc.child("latitude").getValue().toString()));
                    currMda.setTime(nextMDALoc.child("start time").getValue().toString());
                    currMda.setEndTime(nextMDALoc.child("end time").getValue().toString());
                    currMda.setDate(nextMDALoc.child("date").getValue().toString());
                    currMda.setDescription(nextMDALoc.child("description").getValue().toString());
                    currMda.setId(String.valueOf(i));
                    mobiles.add(currMda);

                    System.out.println("\n");
                    System.out.println("\n");
                    i++;
                }
                Log.d("FENCE", "Done updating mobiles");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        /*start the background location services*/
        setupOneTimePull();
        setupScheduledJob();

        //Game stats init
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference gameRef =  dbRef.child("Game");
        gameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //read donation per vampires team
                gameData.setDayEastCounter((long)dataSnapshot.child("East").child("Day").child("Donations").getValue());
                //nightEastCounter = (long)dataSnapshot.child("East").child("Night").child("Donations").getValue();
                gameData.setDayNorthCounter((long)dataSnapshot.child("North").child("Day").child("Donations").getValue());
                gameData.setDaySouthCounter((long)dataSnapshot.child("South").child("Day").child("Donations").getValue());
                gameData.setDayWestCounter((long)dataSnapshot.child("West").child("Day").child("Donations").getValue());

                //read donations per all area
                gameData.setEastTotal((long)dataSnapshot.child("East").child("Donations").getValue());
                gameData.setNorthTotal((long)dataSnapshot.child("North").child("Donations").getValue());
                gameData.setSouthTotal((long)dataSnapshot.child("South").child("Donations").getValue());
                gameData.setWestTotal((long)dataSnapshot.child("West").child("Donations").getValue());

                Log.d("GAME4", "done importing game stats update" );

                gameData.computeGameStats();
                Log.d("GAME4", "done computing stats from updated data");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    /* define behavior to the buttons on the view  */
    public void onClick(View arg0) {

        int i = arg0.getId();
        // login button was pressed

    }


    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
      //  LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(ourReceiver, new IntentFilter(Constants.BROADCAST_ACTION));
        Log.d("asaf","MA the size is " +mobiles.size());


    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume(){
        super.onResume();
    }





    @Override
    public boolean onPrepareOptionsMenu (Menu menu){

        //change notification image according to flag

        //read notification settings from file to geofenceNotification
        try {
            FileInputStream fis = getApplicationContext().openFileInput("geofencesNotification");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line + "\n");
            }
            Log.d("NotificationFlag", "File content: "+sb.toString());

            if (sb.toString().contains("OFF")){
                geofencesNoticiationOn = false;
                Log.d("NotificationFlag", "Notification settings read from file and now set to OFF");
                MenuItem notificationIcon = menu.getItem(0);
                Drawable myIcon = getResources().getDrawable( R.drawable.ic_action_notificationoff);
                notificationIcon.setIcon(myIcon);

            }
            else{
                geofencesNoticiationOn = true;
                Log.d("NotificationFlag", "Notification settings read from file and now set to ON");
                MenuItem notificationIcon = menu.getItem(0);
                Drawable myIcon = getResources().getDrawable( R.drawable.ic_action_notificationon);
                notificationIcon.setIcon(myIcon);

            }

        }
        catch (FileNotFoundException e){
            geofencesNoticiationOn = true;
            Log.d("NotificationFlag", "Notification settings read from file and now set to ON");
            MenuItem notificationIcon = menu.getItem(0);
            Drawable myIcon = getResources().getDrawable( R.drawable.ic_action_notificationon);
            notificationIcon.setIcon(myIcon);
        }
        catch (Exception e) {
            Log.d("NotificationFlag", "Error reading from file"+e.toString());

        }


        return true;
    }

    /* UI function */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);



        return true;


    }

    /* UI function */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
          //  return true;
        //}

        if(id == R.id.action_search){
            //noticiationon or off button was pressed

            if (geofencesNoticiationOn == true){
                // change flag
                geofencesNoticiationOn = false;

                //write settings to file
                String filename = "geofencesNotification";
                String string = "OFF";
                FileOutputStream outputStream;
                try {
                    outputStream = getApplicationContext().openFileOutput(filename, getApplicationContext().MODE_PRIVATE);
                    outputStream.write(string.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    // e.printStackTrace();
                    Log.d("NotificationFlag", "Erroe writing notification setting to file");
                }


                //change image
                ActionMenuItemView notificationIcon = (ActionMenuItemView)findViewById(R.id.action_search);
                Drawable myIcon = getResources().getDrawable( R.drawable.ic_action_notificationoff);
                notificationIcon.setIcon(myIcon);

                //show message to user
                Toast t = Toast.makeText(getApplicationContext(), "notification are off", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP, 0, 0);
                t.show();

                Log.d("NotificationFlag", "Notification set to off");

            }
            else{
                //change flag
                geofencesNoticiationOn = true;

                //write settings to file
                String filename = "geofencesNotification";
                String string = "ON";
                FileOutputStream outputStream;
                try {
                    outputStream = getApplicationContext().openFileOutput(filename, getApplicationContext().MODE_PRIVATE);
                    outputStream.write(string.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    // e.printStackTrace();
                    Log.d("NotificationFlag", "Erroe writing notification setting to file");
                }

                //change image
                ActionMenuItemView notificationIcon = (ActionMenuItemView)findViewById(R.id.action_search);
                Drawable myIcon = getResources().getDrawable( R.drawable.ic_action_notificationon);
                notificationIcon.setIcon(myIcon);

                //show message to user
                Toast t = Toast.makeText(getApplicationContext(), "notification are on", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP, 0, 0);
                t.show();

                Log.d("NotificationFlag", "Notification set to on");


            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);

    }

    private void displayView(int position) {



        //Fragment fragment = null;
        //String title = getString(R.string.app_name);
        title = getString(R.string.app_name);

        if (currentLoggedUser == null){
            fragment = new HomeFragment();
            title = getString(R.string.title_home);

            if (dontShowMustLoginToast){
                dontShowMustLoginToast=false;
            }
            else{
                Toast.makeText(MainActivity.this, R.string.auth_must_login_first, Toast.LENGTH_SHORT).show();  // display messages
            }

        }
        else {


            switch (position) {
                case 0:
                    fragment = new HomeFragment();
                    title = getString(R.string.title_home);
                    break;
                case 1:
                    fragment = new MyProfileFragment();
                    title = getString(R.string.title_myprofile);
                    break;
                case 2:
                    if (JustDonatedFragment.isLegalDonation()) {
                        //fragment = new JustDonatedFragment();
                        //title = getString(R.string.title_just_donated);
                        showBloodDonationCinfirmDialog().show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, R.string.ilegallDonation, Toast.LENGTH_LONG).show();  // display message

                    }
                    break;

                case 3:
                    fragment = new DonateNowFragment();
                    title = getString(R.string.title_donate_now);
                    break;

                case 4:
                    fragment = new Game4();
                    title = getString(R.string.title_gameprogress);
                    break;
                case 5:
                    fragment = new NavigationFragment();
                    title = getString(R.string.title_navigation);
                    break;
                default:
                    break;
            }
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
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
        try{
            ((ProgressBar)findViewById(R.id.loadprofileProgressBar)).setVisibility(View.VISIBLE); //display the progress bar

            //remove the buttons while loading data (added with nitzan & asaf 6.6.2017)
            ((Button) findViewById(R.id.loginButton)).setVisibility(View.INVISIBLE);
            ((Button) findViewById(R.id.loginButton)).setEnabled(false);
            ((Button) findViewById(R.id.logoutButton)).setVisibility(View.INVISIBLE);
            ((Button) findViewById(R.id.logoutButton)).setEnabled(false);


        }catch (NullPointerException e){
            //could not load the progress bar. probably was logged in not through home fragment
        }


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
                    Log.d(TAG,"currentLoggedUser now contain the logged user profile: "+currentLoggedUser.getEmail());
                    //displayProfileAfterLoadingfromDtabase();
                    try{
                        ((ProgressBar)findViewById(R.id.loadprofileProgressBar)).setVisibility(View.GONE); //remove the progress bar

                    }catch (NullPointerException e){
                        //could not load the progress bar. probably was logged in not through home fragment
                    }
                    doThingsAfterLogin();

                }
                else{
                    //user is not in database -> so do  a registration
                    Log.d(TAG,"user was not found in datatbase");

                    // create the user a new profile and insert him to the database

                    currentLoggedUser = new TaramtiDamUser(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getEmail() , "", "A+" );
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").child(currentLoggedUser.getuid()).setValue(currentLoggedUser);

                    Log.d(TAG, "new user was created in database");

                    //displayProfileAfterLoadingfromDtabase();
                    doThingsAfterLogin();
                    try{
                       // ((ProgressBar)findViewById(R.id.loadprofileProgressBar)).setVisibility(View.GONE); //remove the progress bar

                    }catch (NullPointerException e){
                        //could not load the progress bar. probably was logged in not through home fragment
                    }
                    Toast.makeText(MainActivity.this, R.string.auth_registration_completed, Toast.LENGTH_SHORT).show();  // display message

                }

            }
            @Override public void onCancelled(DatabaseError error) { }


        });


    }

    private void doThingsAfterLogin(){

        try {

            ((ProgressBar)findViewById(R.id.loadprofileProgressBar)).setVisibility(View.GONE); //remove the progress bar

            ((Button) findViewById(R.id.loginButton)).setVisibility(View.INVISIBLE);
            ((Button) findViewById(R.id.loginButton)).setEnabled(false);

            ((Button) findViewById(R.id.logoutButton)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.logoutButton)).setEnabled(true);

            //check if the user signed up to game or not
            if(currentLoggedUser.isAlreadyJoinedTheGame()==true){
                //user joined the game so remove game button
                ((Button) findViewById(R.id.gamecubeButton)).setVisibility(View.INVISIBLE);
                ((Button) findViewById(R.id.gamecubeButton)).setEnabled(false);

                //show the vampire image
                //show correct team image
                ImageView vampire_image_view = (ImageView)findViewById(R.id.vampireImageView);
                vampire_image_view.setVisibility(View.VISIBLE);
                if (MainActivity.currentLoggedUser.getTeam().getVemp().equals("Night")){
                     vampire_image_view = (ImageView)findViewById(R.id.vampireImageView);
                    Glide.with(this).load(R.drawable.youareanightvampire).into(vampire_image_view);

                }
                else if(MainActivity.currentLoggedUser.getTeam().getVemp().equals("Day")){
                     vampire_image_view = (ImageView)findViewById(R.id.vampireImageView);
                    Glide.with(this).load(R.drawable.youareadayvamprie).into(vampire_image_view);
                }
            }
            else{
                //user didnt signup for the game yet so show the game button
                ((Button) findViewById(R.id.gamecubeButton)).setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.gamecubeButton)).setEnabled(true);

            }
        }
        catch (NullPointerException e) {

        }
    }
    private void doThingsAfterLogout(){
        try {
            //show login button
            ((Button) findViewById(R.id.loginButton)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.loginButton)).setEnabled(true);

            //remove logout button
            ((Button) findViewById(R.id.logoutButton)).setVisibility(View.INVISIBLE);
            ((Button) findViewById(R.id.logoutButton)).setEnabled(false);

            //remove join the game button
            ((Button) findViewById(R.id.gamecubeButton)).setVisibility(View.INVISIBLE);
            ((Button) findViewById(R.id.gamecubeButton)).setEnabled(false);

            //remove the vampire image
            ImageView vampire_image_view = (ImageView)findViewById(R.id.vampireImageView);
            vampire_image_view.setVisibility(View.INVISIBLE);


        }
        catch (NullPointerException e) {

        }


    }

    public static void signOutFromOurApplication(){

        mAuth.signOut();
        //AuthUI.getInstance().signOut();
        //FirebaseAuth.getInstance().getCurrentUser().
        LoginManager.getInstance().logOut();  // clear facebook login also


    }



    public Dialog showBloodDonationCinfirmDialog(){

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Confirm Blood Donation");
        adb.setMessage("Do you wish to continue?");
        adb.setIcon(R.drawable.donationdialod);
        adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    //User confirmed the donation
                    public void onClick(DialogInterface dialog, int whichButton) {
                        fragment = new JustDonatedFragment();
                        title = getString(R.string.title_just_donated);
                        if (fragment != null) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.container_body, fragment);
                            fragmentTransaction.commit();

                            // set the toolbar title
                            getSupportActionBar().setTitle(title);
                        }

                    }});
        adb.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

            //user canceled the Dialog
            public void onClick(DialogInterface dialog, int whichButton) {

                //Do Nothing
            }});

        return adb.create();



    }

    void setupOneTimePull()
    {
        FirebaseJobDispatcher dispatcher =
                new FirebaseJobDispatcher(
                        new GooglePlayDriver(MainActivity.this)
                );


        Bundle extras = new Bundle();
        extras.putString("bypassNeedToRun", "true");

        dispatcher.mustSchedule(
                dispatcher.newJobBuilder()
                        .setService(OurPullService.class)
                        .setTag("OneTimePull")
                        .setRecurring(false)
                        .setTrigger(Trigger.NOW) //for the first pull
                        .setExtras(extras)
                        .build()
        );
    }


    void setupScheduledJob()
    {
        FirebaseJobDispatcher dispatcher =
                new FirebaseJobDispatcher(
                        new GooglePlayDriver(MainActivity.this)
                );

        Bundle extras = new Bundle();
        extras.putString("bypassNeedToRun", "false");

        dispatcher.mustSchedule(
                dispatcher.newJobBuilder()
                        .setService(OurPullService.class)
                        .setTag("OurPullService")
                        .setRecurring(true)
                        .setTrigger(Trigger.executionWindow(55*60, 60*60)) //every hour
                        .setExtras(extras)
                        .setReplaceCurrent(false)
                        .build()
        );
    }

       /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("Home Fragment", "on activity result called");

        if (requestCode == 11){
            if (resultCode == RESULT_OK){
                Log.d("Home Fragment", data.getDataString());
            }
        }

    }
    */

    @Override
    public void onBackPressed() {

        //if the game in
        if (getSupportActionBar().getTitle() == getString(R.string.title_home) ){
            super.onBackPressed();
        }
        else{

            fragment = new HomeFragment();
            title = getString(R.string.title_home);
            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();

                // set the toolbar title
                getSupportActionBar().setTitle(title);
            }

        }

    }

}
