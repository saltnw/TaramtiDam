package com.taramtidam.taramtidam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Spinner;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private Spinner bloodSpinner;
    private EditText address1;
    private EditText address2;
    private TextView full_name;
    private TextView mail;

    //private TextView last_donation;
    //private ImageView image;

    TaramtiDamUser currentLoggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        address1 = (EditText) findViewById(R.id.editAddress1);
        address2 = (EditText) findViewById(R.id.editAddress2);
        bloodSpinner = (Spinner) findViewById(R.id.bloodSpinner);
        full_name = (TextView) findViewById(R.id.NameTextView);
        mail = (TextView) findViewById(R.id.EmailTextView);
        //last_donation = (TextView) findViewById(R.id.dateTextView);

        final Button button = (Button) findViewById(R.id.SubmitButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String add1 = address1.getText().toString().trim();
                String add2 = address2.getText().toString().trim();
                String bloodType = bloodSpinner.getSelectedItem().toString();

                currentLoggedUser.setAddress(add1);
                //currentLoggedUser.setAnotherAddress(add2); //TODO next milestone
                currentLoggedUser.setBloodType(bloodType);

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users").child(currentLoggedUser.getuid()).setValue(currentLoggedUser);
                Toast.makeText(ProfileActivity.this, R.string.profileUpdateSucess,
                        Toast.LENGTH_SHORT).show();  // display message
            }
        });

        Intent intent = getIntent();
        String get_user_id = intent.getStringExtra(MainActivity.EXTRA_ID);

        final String user_id = get_user_id;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersDatabase = database.getReference("users");

        //using listener get all the users with the same uid
        usersDatabase.orderByKey().equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DatabaseReference mDatabase;
                currentLoggedUser = snapshot.child(user_id).getValue(TaramtiDamUser.class); //get an object of the user
                showDetails(currentLoggedUser);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        //currentLoggedUser = new TaramtiDamUser("12345678","Nitzan Frogel","nitzanfrogel@gmail.com");
    }

    public void showDetails(TaramtiDamUser currentLoggedUser){
        //get the user current information
        String current_full_name = currentLoggedUser.getFullName();
        String current_mail = currentLoggedUser.getEmail();
        String current_address1 = currentLoggedUser.getAddress();
        //String current_address2 = currentLoggerUser.getAddress2(); //TODO: add this field to user profile
        //String current_last_donation = null; //TODO: add this field to user profile

        //set content in Text views with the current information of the user
        full_name.setText(current_full_name);
        mail.setText(current_mail);
        address1.setText(current_address1,TextView.BufferType.EDITABLE);
        //address2.setText(current_address2); //TODO next milestone
        //last_donation.setText(current_last_donation.toString()); //TODO next milestone
    }
}
