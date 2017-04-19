package com.taramtidam.taramtidam;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG ="AsafMSG" ;
    private FirebaseAuth mAuth;                             // firebase auth object
    private FirebaseAuth.AuthStateListener mAuthListener;   // firebase auth listener
    FirebaseUser user;                                      // current user

    private static final int RC_SIGN_IN = 12;               //



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();                               // get the shared instance of the FirebaseAuth object:
        TextView infotv = (TextView) findViewById(R.id.infoTextView);    // get a reference to the infoTextView

        //check if the user is already sigen in or not
        if (mAuth.getCurrentUser() != null) {
            // already signed in
            infotv.setText("welcome " + mAuth.getCurrentUser().getUid());  // update the TextView text
        } else {
            //not sigen in
            //infotv.setText("welcome guest!" );                  // update the TextView text
        }

        /* set listeners to the buttons in the view */
        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById(R.id.logoutButton).setOnClickListener(this);

        /* define a listener for changing the auth state  */
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                TextView infotv = (TextView) findViewById(R.id.infoTextView);    // get a reference to the infoTextView
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    infotv.setText("logged in!\n"+"userid: "+ user.getUid() + "\n"+user.getDisplayName()+"\n"+user.getEmail() + "\n" );     // update the TextView text
                    findViewById(R.id.logoutButton).setEnabled(true);
                    findViewById(R.id.loginButton).setEnabled(false);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    infotv.setText("Hello, \nPlease sign in to continue");                  // update the TextView text
                    findViewById(R.id.logoutButton).setEnabled(false);
                    findViewById(R.id.loginButton).setEnabled(true);

                }
                // ...
            }
        };

    }



    /* define behavior to the buttons on the view  */
    public void onClick(View arg0) {

        int i = arg0.getId();
        // login button was pressed
        if ( i == R.id.loginButton){

            Log.i(TAG, "login btn was pressed;");
            Button btn = (Button)arg0;                                       // cast view to a button
            TextView infotv = (TextView) findViewById(R.id.infoTextView);    // get a reference to the infoTextView

            /* run firebaseUI login screen, with mail and facebook options */
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);

            //EditText emailet = (EditText) findViewById(R.id.usernameEditText);
            //EditText passwordet = (EditText) findViewById(R.id.passwordEditText);
            //String email = new String("asaf@asafraviv.com");
            //String password = new String("123456");
            //signIn(emailet.getText().toString(),passwordet.getText().toString());

        }
        //logout button was pressed
        if ( i == R.id.logoutButton){
            Log.i(TAG, "logout btn was pressed;");
            AuthUI.getInstance().signOut(this);   // logout
            LoginManager.getInstance().logOut();  // clear facebook login also
            Toast.makeText(MainActivity.this, R.string.auth_signout_success,
                    Toast.LENGTH_SHORT).show();  // display message
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //function for custom email + password login. CAN BE DELETED
   /*
    private void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }
    */
}
