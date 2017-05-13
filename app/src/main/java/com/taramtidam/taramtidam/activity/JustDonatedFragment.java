package com.taramtidam.taramtidam.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.taramtidam.taramtidam.MainActivity;
import com.taramtidam.taramtidam.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;


public class JustDonatedFragment extends Fragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* increase the rank of the user */

        if (isLegalDonatoin()){
            //update the rank
            MainActivity.currentLoggedUser.increaseRankByOne();

            //update last donation date
            MainActivity.currentLoggedUser.SetLastDonationDateToToday();

            //update database
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(MainActivity.currentLoggedUser.getuid()).setValue(MainActivity.currentLoggedUser);
        }
        else{
            Toast.makeText(getApplicationContext(), R.string.ilegallDonation, Toast.LENGTH_LONG).show();

        }


        //Date d = MainActivity.currentLoggedUser.getLastDonation();
        //DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        //Log.d("JustDonated", df.format(d));



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_justdonated, container, false);

        // Inflate the layout for this fragment



        Integer images[] = {R.drawable.rank0,R.drawable.rank1,R.drawable.rank2,R.drawable.rank3,R.drawable.rank4};
        int user_rank = MainActivity.currentLoggedUser.getRankLevel();

        ImageView rank_image_view = (ImageView)rootView.findViewById(R.id.rankImageView);
        rank_image_view.setImageResource(images[user_rank]);


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

    public static boolean isLegalDonatoin(){

        // check date margin
        return isLegalDate();




        //check distance
        //TODO add distance checks

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

