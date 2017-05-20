package com.taramtidam.taramtidam.activity;

/**
 * Created by Asaf on 06/05/2017.
 */


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.taramtidam.taramtidam.MainActivity;
import com.taramtidam.taramtidam.R;

import com.google.android.gms.location.places.AutocompleteFilter;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyProfileFragment extends Fragment implements View.OnClickListener {

    private Spinner bloodSpinner;
    private EditText address1;
    private EditText address2;
    private TextView full_name;
    private TextView mail;
    private Button saveBtn;
    private Button editBtn;
    private PlaceAutocompleteFragment autocompleteFragment_home;
    private PlaceAutocompleteFragment autocompleteFragment_work;
    private int autocomplete_caller = 0;
    private ImageView imageToUpload;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 123;
    private static final int RESULT_LOAD_IMAGE = 124;
    //private FragmentManager fm;
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

        //set onClicklListener
        saveBtn = (Button) rootView.findViewById(R.id.saveProfileButton);
        saveBtn.setOnClickListener(this);

        editBtn = (Button) rootView.findViewById(R.id.editProfileButton);
        editBtn.setOnClickListener(this);

        imageToUpload = (ImageView)  rootView.findViewById(R.id.imageUploaded);
        imageToUpload.setOnClickListener(this);

        saveBtn.setVisibility(View.INVISIBLE);
        editBtn.setVisibility(View.VISIBLE);



        if (MainActivity.currentLoggedUser != null) {
            ((TextView) rootView.findViewById(R.id.emailTextView)).setText(MainActivity.currentLoggedUser.getEmail());
            ((TextView) rootView.findViewById(R.id.fullnameLabel)).setText(MainActivity.currentLoggedUser.getFullName());
            ((EditText) rootView.findViewById(R.id.homeEditText)).setText(MainActivity.currentLoggedUser.getAddress1());
            ((EditText) rootView.findViewById(R.id.workEditText)).setText(MainActivity.currentLoggedUser.getAddress2());

            if(MainActivity.currentLoggedUser.getUserImage()!="") {
                byte[] decodedString = Base64.decode(MainActivity.currentLoggedUser.getUserImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageToUpload.setImageBitmap(decodedByte);
            }
        }
        ((EditText) rootView.findViewById(R.id.homeEditText)).setFocusable(false);
        ((EditText) rootView.findViewById(R.id.workEditText)).setFocusable(false);

        //set the listen of editText to call the autocomplete intent
        ((EditText) rootView.findViewById(R.id.homeEditText)).setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (saveBtn.getVisibility() == View.VISIBLE){
                    autocomplete_caller = 1;
                    //Toast.makeText(getApplicationContext(),  "workEditText", Toast.LENGTH_SHORT).show();
                    callPlaceAutocompleteActivityIntent();
                }
                return false;
            }

        });

        ((EditText) rootView.findViewById(R.id.workEditText)).setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (saveBtn.getVisibility() == View.VISIBLE){
                    autocomplete_caller = 2;
                    //Toast.makeText(getApplicationContext(),  "workEditText", Toast.LENGTH_SHORT).show();
                    callPlaceAutocompleteActivityIntent();
                }
                return false;
            }

        });
        // end of listeners

        // Inflate the layout for this fragment
        return rootView;
   }

    AutocompleteFilter countryFilter = new AutocompleteFilter.Builder()
            .setCountry("IL")
            .build();

//functions to handle the autocomplete

    private void callPlaceAutocompleteActivityIntent() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(countryFilter)
                            .build(this.getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
//PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this.getActivity(), data);

                if(autocomplete_caller==1){
                    EditText homeAddressET = (EditText) (getView().findViewById(R.id.homeEditText));
                    homeAddressET.setText(place.getName().toString());

                }
                else if(autocomplete_caller==2){
                    EditText workAddressET = (EditText) (getView().findViewById(R.id.workEditText));
                    workAddressET.setText(place.getName().toString());
                }
                return;
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this.getActivity(), data);
                return;
            } else if (requestCode == Activity.RESULT_CANCELED) {
                return;
            }
        }else if((requestCode == RESULT_LOAD_IMAGE) && (resultCode == Activity.RESULT_OK) && (data != null)){
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
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
        if (arg0 == getView().findViewById(R.id.saveProfileButton)) {
            //save button pressed : switch button to edit and disable textbox edit
            saveBtn.setVisibility(View.INVISIBLE);
            editBtn.setVisibility(View.VISIBLE);

            getView().findViewById(R.id.homeEditText).setFocusable(false);
            getView().findViewById(R.id.workEditText).setFocusable(false);

            EditText homeAddressET = (EditText) (getView().findViewById(R.id.homeEditText));
            EditText workAddressET = (EditText) (getView().findViewById(R.id.workEditText));
            Spinner bloodType = (Spinner) (getView().findViewById(R.id.bloodSpinner));
            //imageToUpload = (ImageView) (getView().findViewById(R.id.imageUploaded));

            //encode the user picture
            Bitmap bm = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
            //Bitmap bm = imageToUpload.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] byteArrayImage = baos.toByteArray();
            String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

            //update currentLoggedUser
            MainActivity.currentLoggedUser.setAddress1(homeAddressET.getText().toString());
            MainActivity.currentLoggedUser.setAddress2(workAddressET.getText().toString());
            MainActivity.currentLoggedUser.setBloodType(bloodType.getSelectedItem().toString());
            MainActivity.currentLoggedUser.setUserImage(encodedImage);


            //update database
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(MainActivity.currentLoggedUser.getuid()).setValue(MainActivity.currentLoggedUser);
            Toast.makeText(getApplicationContext(), R.string.profileUpdateSucess, Toast.LENGTH_SHORT).show();
        }else if(arg0 == getView().findViewById(R.id.editProfileButton)){
            //edit button pressed : switch button to save and allow textbox edit
            saveBtn.setVisibility(View.VISIBLE);
            editBtn.setVisibility(View.INVISIBLE);

            getView().findViewById(R.id.homeEditText).setFocusableInTouchMode(true);
            getView().findViewById(R.id.workEditText).setFocusableInTouchMode(true);

        }else if(arg0 == getView().findViewById(R.id.imageUploaded)){
            if (saveBtn.getVisibility() == View.VISIBLE){
                Intent gallaryIntetnt = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallaryIntetnt,RESULT_LOAD_IMAGE);
            }




        }


    }


}