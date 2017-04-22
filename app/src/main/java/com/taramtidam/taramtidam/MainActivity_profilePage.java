package com.taramtidam.taramtidam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MainActivity_profilePage extends AppCompatActivity {

    EditText userName;
    EditText userEmail;
    TextView lastDonateDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_profilepage);


        lastDonateDate = (TextView) findViewById(R.id.lastdonate);
        userName       = (EditText) findViewById(R.id.UserName);
        userEmail       = (EditText) findViewById(R.id.UserEmail);
        Button saveButton = (Button) findViewById(R.id.Savebutton);
        saveButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String name  = userName.getText().toString();
                String email = userEmail.getText().toString();
            }
        });

        Button updateDateButton = (Button) findViewById(R.id.donatedToday);
        updateDateButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                Date date = new Date();
                lastDonateDate.setText(dateFormat.format(date));
            }
        });
    }
}
