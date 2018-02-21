package com.risco.android.instagramclone.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.risco.android.instagramclone.R;
import com.risco.android.instagramclone.models.User;
import com.risco.android.instagramclone.utils.FirebaseMethods;

/**
 * Created by Albert Risco on 29/11/2017.
 */

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private Context mContext;


    //Firebase stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods firebaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private String append;

    //Layout stuff
    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword;
    private ProgressBar mProgressBar;
    private Button btnRegister;
    private LinearLayout mLoginLink;

    private String username;
    private String email;
    private String password;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initWidgets();

        mProgressBar.setVisibility(View.GONE);
        setupFirebaseAuth();

        init();

    }

    private void initWidgets(){
        mContext = RegisterActivity.this;
        mUsername=(EditText) findViewById(R.id.register_input_username);
        mEmail=(EditText) findViewById(R.id.register_input_email);
        mPassword=(EditText) findViewById(R.id.register_input_password);
        mProgressBar=(ProgressBar) findViewById(R.id.register_progressBar);
        btnRegister =(Button) findViewById(R.id.register_btn);
        mLoginLink=(LinearLayout) findViewById(R.id.login_text);
        firebaseMethods = new FirebaseMethods(mContext);
        append="";
        mLoginLink=(LinearLayout) findViewById(R.id.login_text);
    }

    private Boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: checking if string is null");

        if(string.equals("")){
            Log.d(TAG, "isStringNull: es null");
            return true;
        }
        else {
            Log.d(TAG, "isStringNull: "+string+"en teoria no es null");
            return false;
        }
    }

    private void init(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email=mEmail.getText().toString();
                password=mPassword.getText().toString();
                username=mUsername.getText().toString();

                if(isStringNull(email) || isStringNull(password) || isStringNull(username)){
                    Toast.makeText(mContext, "Ups! You must fill all the fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    firebaseMethods.registerNewEmail(email, password, username);
                    mProgressBar.setVisibility(View.GONE);


                }
            }
        });

        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });


    }


    /*
    ----------------------------Firebase-------------------------------------
     */

    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: Checking if the username already exists");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){

                }
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if(singleSnapshot.exists()){
                        Log.d(TAG, "checkIfUserNameExists: FOUND A MATCH:" + singleSnapshot.getValue(User.class).getUsername());

                        String key = mAuth.getCurrentUser().getUid();
                        Log.d(TAG, "onDataChange: key:"+key);
                        append = key.substring(2,9);
                        Log.d(TAG, "onDataChange: Username already exists. " +
                                "Appending random string to name "+append);
                    }
                }
                //1st check: Make sure the username is not already in use.
                Log.d(TAG, "onDataChange: append: "+append);
                String mUsername = "";
                mUsername =username+append;

                //add new user to the database
                firebaseMethods.addNewUser(email, mUsername, "", "", "");

                Toast.makeText(mContext, "Signup Succesful. Sending verification email.", Toast.LENGTH_SHORT).show();

                mAuth.signOut();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * setup the firebase auth object
     */

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting firebase auth");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef=mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!= null){
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged: signed in"+user.getUid());

                    //We use this listener instead of another because we only want to take
                    //like an instant photo of the database in that moment.
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: Entering onDataChange");
                            checkIfUsernameExists(username);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    finish();
                }
                else {
                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged: signed in");
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
