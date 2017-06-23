package com.taramtidam.taramtidam.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taramtidam.taramtidam.Game4;
import com.taramtidam.taramtidam.MainActivity;
import com.taramtidam.taramtidam.R;
import com.taramtidam.taramtidam.model.ShareOnFacebook;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.taramtidam.taramtidam.Game3.area;
import static com.taramtidam.taramtidam.MainActivity.currentLoggedUser;

//twitter share
import io.fabric.sdk.android.Fabric;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;


public class JustDonatedFragment extends Fragment implements View.OnClickListener {

    Button facebookBtn;
    Button toGameProgressBtn;
    Button twitterBtn;
    int prevRank;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* increase the rank of the user */

        if (isLegalDonation()){
            //save the new rank
            prevRank = MainActivity.currentLoggedUser.getRankLevel();

            //update the rank
            MainActivity.currentLoggedUser.increaseRankByOne();

            //update donations counter by one
            MainActivity.currentLoggedUser.setDonationsCounter(MainActivity.currentLoggedUser.getDonationsCounter()+1);

            //update last donation date
            MainActivity.currentLoggedUser.SetLastDonationDateToToday();

            //update database
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(MainActivity.currentLoggedUser.getuid()).setValue(MainActivity.currentLoggedUser);

            //if the user is in the game than update the game state
            if (MainActivity.currentLoggedUser.isAlreadyJoinedTheGame()) {

                final String UID = MainActivity.currentLoggedUser.getuid();
                final DatabaseReference ref = mDatabase;
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // get user team (area + vampires team)
                        String area = currentLoggedUser.getTeam().getArea();
                        String vempTeam = currentLoggedUser.getTeam().getVemp();
                        Log.d("GAME", "user area: " + area);
                        Log.d("GAME", "user vemp: " + vempTeam);

                        //Update global donations counter
                        long globalDonations = (long) dataSnapshot.child("Game").child("_GlobalDonationCounter").getValue();
                        globalDonations++;
                        ref.child("Game").child("_GlobalDonationCounter").setValue(globalDonations);

                        //update area counter
                        long areaDonations = (long) dataSnapshot.child("Game").child(area).child("Donations").getValue();
                        areaDonations++;
                        ref.child("Game").child(area).child("Donations").setValue(areaDonations);

                        //update team counter
                        long teamDonations = (long) dataSnapshot.child("Game").child(area).child(vempTeam).child("Donations").getValue();
                        teamDonations++;
                        ref.child("Game").child(area).child(vempTeam).child("Donations").setValue(teamDonations);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }
        else{
            Toast.makeText(getApplicationContext(), R.string.ilegallDonation, Toast.LENGTH_LONG).show();

        }

        //twitter share
        TwitterAuthConfig authConfig =  new TwitterAuthConfig("consumerKey", "consumerSecret");
        Fabric.with(getActivity(), new TwitterCore(authConfig), new TweetComposer());


        //Date d = MainActivity.currentLoggedUser.getLastDonation();
        //DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        //Log.d("JustDonated", df.format(d));



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_justdonated, container, false);

        // Inflate the layout for this fragment

        //set facebook share button onCliclListener
        facebookBtn = (Button) rootView.findViewById(R.id.facebookButton);
        facebookBtn.setOnClickListener(this);

        //set facebook share button onCliclListener
        twitterBtn = (Button) rootView.findViewById(R.id.twitterButton);
        twitterBtn.setOnClickListener(this);

        //set facebook share button onCliclListener
        toGameProgressBtn = (Button) rootView.findViewById(R.id.togameresultButton);
        toGameProgressBtn.setOnClickListener(this);

        //load blood bag image
        ImageView sakit_dam_image_view = (ImageView)rootView.findViewById(R.id.bloodCounterImgaeView);
        Glide.with(getContext()).load(R.drawable.sakitdamicon).into(sakit_dam_image_view);

        //display the correct rank image
        Integer images[] = {R.drawable.rank0,R.drawable.rank1,R.drawable.rank2,R.drawable.rank3,R.drawable.rank4};
        int user_rank = MainActivity.currentLoggedUser.getRankLevel();
        ImageView rank_image_view = (ImageView)rootView.findViewById(R.id.rankImageView);
        Glide.with(getContext()).load(images[user_rank]).into(rank_image_view);

        //display correct text above charcther image
        if (prevRank < MainActivity.currentLoggedUser.getRankLevel()){
            ((TextView)rootView.findViewById(R.id.newCharTextView)).setText("Your new level");
        }
        else{
            ((TextView)rootView.findViewById(R.id.newCharTextView)).setText("You've reached top level");
        }

        //display the correct blood counter
        ((TextView)rootView.findViewById(R.id.BloodcounterTextView)).setText(Integer.toString(MainActivity.currentLoggedUser.getDonationsCounter()));


        return rootView;


    }


    private static boolean isLegalDate (){
        Date user_last_donation = MainActivity.currentLoggedUser.getLastDonation();
        Date today_date = new Date();
        long diff, diffDays;
        if (user_last_donation == null){
            return true;
        }
        else{
            diff =  today_date.getTime() -  user_last_donation.getTime();
            diffDays = diff / (24*60*60*1000);
        }
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Log.d("JustDonated","last donation: "+df.format((user_last_donation)));
        Log.d("JustDonated","today: "+df.format((today_date)));
        Log.d("JustDonated","diff days: "+diffDays);


        if (diffDays >= 88){
            return true;
        }
        else{
            return false;
        }

    }


    public static boolean isLegalDonation(){
        return true;
        // check date margin
        //return isLegalDate();


        //check distance
        //TODO add distance checks

    }

    public void onClick(View arg0) {


        int buttonId = arg0.getId();

        if (buttonId == R.id.facebookButton) {

            Log.d("Just Donated FRAGMENT","facebook button pressed");
            Intent facebook_share_intent = new Intent(getActivity(), ShareOnFacebook.class);
            getActivity().startActivity(facebook_share_intent);

        }

        if (buttonId == R.id.twitterButton){
            Log.d("Just Donated FRAGMENT","twitter button pressed");
            //String twitterUri = "http://m.twitter.com/?status=";
            //String marketUri = Uri.encode("I have just donated blood using TarmatiDam App");
            //Intent shareOnTwitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUri + marketUri));
            //startActivity(shareOnTwitterIntent);

            TweetComposer.Builder builder = new TweetComposer.Builder(getActivity())
                    .text("I just donated blood using TarmtiDam app. Go Get It!");
            builder.show();
        }

        if (buttonId == R.id.togameresultButton) {

        }
            Fragment fragment = new Game4();
            String title = getString(R.string.title_gameprogress);
            if (fragment != null) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();

                // set the toolbar title
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);

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

