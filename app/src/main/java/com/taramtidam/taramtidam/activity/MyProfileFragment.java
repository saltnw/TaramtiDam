package com.taramtidam.taramtidam.activity;

/**
 * Created by Asaf on 06/05/2017.
 */


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.taramtidam.taramtidam.MainActivity;
import com.taramtidam.taramtidam.R;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyProfileFragment extends Fragment implements View.OnClickListener {

    private Spinner bloodSpinner;
    private EditText address1;
    private EditText address2;
    private TextView full_name;
    private TextView mail;
    private Button updateBtn;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_myprofile, container, false);

        if (MainActivity.currentLoggedUser != null) {
            ((TextView) rootView.findViewById(R.id.emailTextView)).setText(MainActivity.currentLoggedUser.getEmail());
            ((TextView) rootView.findViewById(R.id.fullnameLabel)).setText(MainActivity.currentLoggedUser.getFullName());
            ((EditText) rootView.findViewById(R.id.homeEditText)).setText(MainActivity.currentLoggedUser.getAddress1());
            ((EditText) rootView.findViewById(R.id.workEditText)).setText(MainActivity.currentLoggedUser.getAddress2());
        }

        //set onClicklListener
        updateBtn = (Button) rootView.findViewById(R.id.updateButton);
        updateBtn.setOnClickListener(this);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (MainActivity.currentLoggedUser != null) {
            addItemsOnSpinner();
        }


    }

    public void addItemsOnSpinner() {
        bloodSpinner = (Spinner) getView().findViewById(R.id.bloodSpinner);
        String bloodTypes[] = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        List<String> list = new ArrayList<String>();
        list.add(MainActivity.currentLoggedUser.getBloodType());
        for (int i = 0; i < bloodTypes.length; i++) {
            if (bloodTypes[i] != MainActivity.currentLoggedUser.getBloodType()) {
                list.add(bloodTypes[i]);
            }
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        bloodSpinner.setAdapter(adapter);

    }

    public void onClick(View arg0) {
        EditText homeAddressET = (EditText)(getView().findViewById(R.id.homeEditText));
        EditText workAddressET = (EditText)(getView().findViewById(R.id.workEditText));
        Spinner bloodType = (Spinner) (getView().findViewById(R.id.bloodSpinner));

        //update currentLoggedUser
        MainActivity.currentLoggedUser.setAddress1(homeAddressET.getText().toString());
        MainActivity.currentLoggedUser.setAddress2(workAddressET.getText().toString());
        MainActivity.currentLoggedUser.setBloodType(bloodType.getSelectedItem().toString());

        //update database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(MainActivity.currentLoggedUser.getuid()).setValue(MainActivity.currentLoggedUser);
        Toast.makeText(getApplicationContext(), R.string.profileUpdateSucess, Toast.LENGTH_SHORT).show();



    }
}