package com.taramtidam.taramtidam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class tutActi extends AppCompatActivity implements View.OnClickListener {
    int currLook = 1;
    int maxView  = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tut);

        Button nextBtn = (Button) findViewById(R.id.tutNext);
        Button prevBtn = (Button) findViewById(R.id.tutPrev);

        setImageViewer(currLook);
        nextBtn.setOnClickListener(this);
        prevBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Button nextBtn = (Button) findViewById(R.id.tutNext);
        if (v == findViewById(R.id.tutNext)) {
            if (nextBtn.getText().equals("Done")) {
                finish();
            }
            if (nextBtn.getText().equals("Next")) {
                if (currLook < maxView) {
                    currLook = currLook + 1;
                    setImageViewer(currLook);
                }
                if (currLook == maxView) {
                    nextBtn.setText("Done");
                }
            }
        }
        else if (v == findViewById(R.id.tutPrev)) {
            if(currLook == maxView){
                nextBtn.setText("Next");
            }
            if(currLook > 1){
                currLook = currLook-1;
                setImageViewer(currLook);

            }
        }
    }


    public void setImageViewer(int viewNumber){
        Log.d("###t###","entered setImageViewer with " + viewNumber);
        ImageView imageView = (ImageView) findViewById(R.id.tutImage);
        if(viewNumber == 1){
            Glide.with(imageView).load(R.drawable.tut1).into(imageView);
        }
        else if(viewNumber == 2){
            Glide.with(imageView).load(R.drawable.tut2).into(imageView);
        }
    }
}