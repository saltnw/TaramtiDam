package com.taramtidam.taramtidam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity_FIllForm extends AppCompatActivity {

    EditText userFirstName;
    EditText userLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_profilepage);

        userFirstName      = (EditText) findViewById(R.id.userFirstName);
        userLastName       = (EditText) findViewById(R.id.userLastName);
        Button saveButton = (Button) findViewById(R.id.Savebutton_fillform);
        saveButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String fname = userFirstName.getText().toString();
                String lname = userLastName.getText().toString();
                startActivity(new Intent(getApplicationContext(),MainActivity_profilePage.class));
            }
       });
    }
}
