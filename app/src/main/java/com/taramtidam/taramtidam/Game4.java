package com.taramtidam.taramtidam;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taramtidam.taramtidam.activity.HomeFragment;

import static com.taramtidam.taramtidam.MainActivity.gameData;

/**
 * Created by Asaf on 08/06/2017.
 */

public class Game4 extends Fragment implements View.OnClickListener {

    Button backToMain;

    long dayEastPercent = gameData.getDayEastPercent();
    long nightEastPercent = gameData.getNightEastPercent();

    long dayNorthPercent = gameData.getDayNorthPercent();
    long nightNorthPercent = gameData.getNightNorthPercent();

    long daySouthPercent = gameData.getDaySouthPercent();
    long nightSouthPercent = gameData.getNightSouthPercent();

    long dayWestPercent = gameData.getDayWestPercent();
    long nightWestPercent = gameData.getNightWestPercent();

    TextView t1, t2, t3, t4, t5, t6, t7, t8;


    public Game4() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_game4, container, false);

        //set logout button onCliclListener
        backToMain = (Button) rootView.findViewById(R.id.backToMainButton);
        backToMain.setOnClickListener(this);

        int bitN,bitW,bitE,bitS;
        bitN = dayNorthPercent > nightNorthPercent ? 1 : 0;
        bitW = dayWestPercent > nightWestPercent ? 1 : 0;
        bitE = dayEastPercent > nightEastPercent ? 1 : 0;
        bitS = daySouthPercent > nightSouthPercent ? 1 : 0;

        String binNum = "" + bitN + bitW + bitE + bitS + "";
        int mapNum = Integer.parseInt(binNum, 2);

        Log.d("GAME4", "The binary number is " + binNum);
        Log.d("GAME4", "The integer number is " + mapNum);

        Integer images[] = {
                R.drawable.map0,
                R.drawable.map1,
                R.drawable.map2,
                R.drawable.map3,
                R.drawable.map4,
                R.drawable.map5,
                R.drawable.map6,
                R.drawable.map7,
                R.drawable.map8,
                R.drawable.map9,
                R.drawable.map10,
                R.drawable.map11,
                R.drawable.map12,
                R.drawable.map13,
                R.drawable.map14,
                R.drawable.map15};

        //load image with glide
        ImageView map_result_image_view = (ImageView)rootView.findViewById(R.id.mapresultImageView);
        Glide.with(getContext()).load(images[mapNum]).into(map_result_image_view);

        //load table image
        ImageView table_image_view = (ImageView)rootView.findViewById(R.id.resultstableImageView);
        Glide.with(getContext()).load(R.drawable.resultstable).into(table_image_view);

        //load score title image
        ImageView scores_title_image_view = (ImageView)rootView.findViewById(R.id.currentscoresImageView);
        Glide.with(getContext()).load(R.drawable.currentscores).into(scores_title_image_view);



        return rootView;

    }


    @Override
    public void onStart() {
        super.onStart();

        t1 = (TextView) getView().findViewById(R.id.resultSouthDayTextBox);
        t1.setText(percentToString(daySouthPercent));

        t2 = (TextView) getView().findViewById(R.id.resultWestDayTextBox);
        t2.setText(percentToString(dayWestPercent));

        t3 = (TextView) getView().findViewById(R.id.resultEastDayTextBox);
        t3.setText(percentToString(dayEastPercent));

        t4 = (TextView) getView().findViewById(R.id.resultNorthDayTextBox);
        t4.setText(percentToString(dayNorthPercent));

        t5 = (TextView) getView().findViewById(R.id.resultSouthNightTextBox);
        t5.setText(percentToString(nightSouthPercent));

        t6 = (TextView) getView().findViewById(R.id.resultWestNightTextBox);
        t6.setText(percentToString(nightWestPercent));

        t7 = (TextView) getView().findViewById(R.id.resultEastNightTextBox);
        t7.setText(percentToString(nightEastPercent));

        t8 = (TextView) getView().findViewById(R.id.resultNorthNightTextBox);
        t8.setText(percentToString(nightNorthPercent));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onClick(View arg0) {

        int buttonId = arg0.getId();
        int team = 0;

        if (buttonId == R.id.backToMainButton) {

            //switch to the next fragment
            Fragment f = new HomeFragment();

            if (f != null) {
                Log.d("Game Activity", "loading again home fragemnt...");

                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

                //TODO restore all the state of the buuton (login +logout + join the game)

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, f);
                fragmentTransaction.commit();

            }
        }
    }

    private String percentToString(long percent){

        String res;
        return "" + percent + "%";


    }
}
