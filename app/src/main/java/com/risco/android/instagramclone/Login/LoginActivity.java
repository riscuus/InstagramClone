package com.risco.android.instagramclone.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.risco.android.instagramclone.Home.HomeActivity;
import com.risco.android.instagramclone.R;

/**
 * Created by Albert Risco on 29/11/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private Context mContext;

    //Firebase stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Layout stuff
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressBar=(ProgressBar) findViewById(R.id.login_loading_progressBar);
        mEmail=(EditText) findViewById(R.id.input_email);
        mPassword=(EditText) findViewById(R.id.input_password);
        mContext=LoginActivity.this;

        mProgressBar.setVisibility(View.GONE);

        setupFirebaseAuth();
        init();

    }

    private Boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: checking if string is null");

        if(string.equals("")){
            return true;
        }
        else return false;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

        /*
    ----------------------------Firebase-------------------------------------
     */

    private void init(){
        Button btnLogin = (Button) findViewById(R.id.login_button);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: Attempting to log in");

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(isStringNull(email) || isStringNull(password)){
                    Toast.makeText(mContext, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                }else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    hideSoftKeyboard(LoginActivity.this);

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.e(TAG, "signInWithEmail:success");

                                        FirebaseUser user = mAuth.getCurrentUser();


                                        mProgressBar.setVisibility(View.GONE);
                                        if(user.isEmailVerified()){
                                            Log.e(TAG, "onComplete: success, email is verified");
                                            Log.d(TAG, "init: attempting to navigate to HomeActivity");
                                            Intent intent = new Intent(mContext, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else{
                                            Log.e(TAG, "onComplete: Email not verified");
                                            Toast.makeText(mContext, "Make sure to verify your email", Toast.LENGTH_SHORT).show();

                                        }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);


                                    }

                                    // ...
                                }
                            });
                }
            }
        });

        TextView newAccountText = (TextView) findViewById(R.id.newAccountText);
        newAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivity(intent);

            }
        });
        /*
        If the user logs in correctly, navigate to HomeActivity
         */
        Log.d(TAG, "init: llega hasta aqui");
        if(mAuth.getCurrentUser()!=null){
            Log.d(TAG, "init: attempting to navigate to HomeActivity");
            Intent intent = new Intent(mContext, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * setup the firebase auth object
     */

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting firebase auth");
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!= null){
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged: signed in"+user.getUid());
                }
                else {
                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
