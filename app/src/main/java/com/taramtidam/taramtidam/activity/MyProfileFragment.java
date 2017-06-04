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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.taramtidam.taramtidam.MainActivity;
import com.taramtidam.taramtidam.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
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
    private ImageView imageToUpload;
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

        CheckBox CB = (CheckBox) rootView.findViewById(R.id.sendMailCB);
        CB.setOnClickListener(this);

        //imageToUpload = (ImageView)  rootView.findViewById(R.id.imageUploaded);
        //imageToUpload.setOnClickListener(this);
        rootView.setOnClickListener(this);

        //read file
        try {
            FileInputStream fis = getApplicationContext().openFileInput("user data");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }


        //update view with ther user info(profile)
        if (MainActivity.currentLoggedUser != null) {
            ((TextView) rootView.findViewById(R.id.emailTextView)).setText(MainActivity.currentLoggedUser.getEmail());
            ((TextView) rootView.findViewById(R.id.fullnameLabel)).setText(MainActivity.currentLoggedUser.getFullName());
            ((AutoCompleteTextView) rootView.findViewById(R.id.homeEditText)).setText(MainActivity.currentLoggedUser.getAddress1());
            ((AutoCompleteTextView) rootView.findViewById(R.id.workEditText)).setText(MainActivity.currentLoggedUser.getAddress2());
            ((CheckBox) rootView.findViewById(R.id.sendMailCB)).setChecked((MainActivity.currentLoggedUser.getMailingBool()));
            //set last donation text accroding to the user
            if (MainActivity.currentLoggedUser.getDonationsCounter() != 0) {
                ((TextView) rootView.findViewById(R.id.lastDonationTextView)).setText(MainActivity.currentLoggedUser.getLastDonationInString());
            } else {
                ((TextView) rootView.findViewById(R.id.lastDonationTextView)).setText("no donation yet");

            }

            //load imgae accroding to to the user
            Integer images[] = {R.drawable.rank0, R.drawable.rank1, R.drawable.rank2, R.drawable.rank3, R.drawable.rank4};
            int user_rank = MainActivity.currentLoggedUser.getRankLevel();
            //display the correct rank image
            ImageView rank_image_view = (ImageView) rootView.findViewById(R.id.rankImageProfile);
            rank_image_view.setImageResource(images[user_rank]);

           /* if( (MainActivity.currentLoggedUser.getUserImage()!=null) && (!MainActivity.currentLoggedUser.getUserImage().equals("")) ) {
                byte[] decodedString = Base64.decode(MainActivity.currentLoggedUser.getUserImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageToUpload.setImageBitmap(decodedByte);
            }*/
        }
//        //set onClicklListener
//        updateBtn = (Button) rootView.findViewById(R.id.updateButton);
//          updateBtn.setOnClickListener(this);

        // Inflate the layout for this fragment
        return rootView;
    }

    public void selectItem(View view){
        if (MainActivity.currentLoggedUser != null) {
            MainActivity.currentLoggedUser.setMailingBool(((CheckBox) view).isChecked());
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
        //getView().findViewById(R.id.homeEditText).setFocusable(false);
        //getView().findViewById(R.id.workEditText).setFocusable(false);

        if (arg0 == getView().findViewById(R.id.saveProfileButton)) {
            //save button pressed :
            Spinner bloodType = (Spinner) (getView().findViewById(R.id.bloodSpinner));

            //encode the user picture
            /*
            Bitmap bm = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] byteArrayImage = baos.toByteArray();
            String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            */

            AutoCompleteTextView homeAddressET = (AutoCompleteTextView) (getView().findViewById(R.id.homeEditText));
            AutoCompleteTextView workAddressET = (AutoCompleteTextView) (getView().findViewById(R.id.workEditText));


            //update currentLoggedUser
            MainActivity.currentLoggedUser.setAddress1(homeAddressET.getText().toString());
            MainActivity.currentLoggedUser.setAddress2(workAddressET.getText().toString());
            MainActivity.currentLoggedUser.setBloodType(bloodType.getSelectedItem().toString());
            // MainActivity.currentLoggedUser.setUserImage(encodedImage);

            //update database
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(MainActivity.currentLoggedUser.getuid()).setValue(MainActivity.currentLoggedUser);
            Toast.makeText(getApplicationContext(), R.string.profileUpdateSucess, Toast.LENGTH_SHORT).show();

            //write to file
            //File file = new File(getApplicationContext().getFilesDir(), "userdata");
            String filename = "user data";
            String string = homeAddressET.getText().toString() + "\n" + workAddressET.getText().toString() + "\n" + bloodType.getSelectedItem().toString() + "\n";
            FileOutputStream outputStream;
            try {
                outputStream = getApplicationContext().openFileOutput(filename, getApplicationContext().MODE_PRIVATE);
                outputStream.write(string.getBytes());
                outputStream.close();
            } catch (Exception e) {
                // e.printStackTrace();
            }
            //
        }
        else if (arg0 == getView().findViewById(R.id.sendMailCB)) {
            //update currentLoggedUser
            MainActivity.currentLoggedUser.setMailingBool(((CheckBox) arg0).isChecked());
            //update database
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(MainActivity.currentLoggedUser.getuid()).setValue(MainActivity.currentLoggedUser);
        }

    }


    //@Override
    // public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    if((requestCode == RESULT_LOAD_IMAGE) && (resultCode == Activity.RESULT_OK) && (data != null)){
    //        Uri selectedImage = data.getData();
    //       imageToUpload.setImageURI(selectedImage);
    //   }
    //}


}