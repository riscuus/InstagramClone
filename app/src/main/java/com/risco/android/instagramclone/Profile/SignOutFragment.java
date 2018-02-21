package com.risco.android.instagramclone.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.risco.android.instagramclone.Login.LoginActivity;
import com.risco.android.instagramclone.R;

/**
 * Created by Albert Risco on 26/11/2017.
 */

public class SignOutFragment extends Fragment {

    private static final String TAG = "SignOutFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressBar mProgressbar;
    private Button btnSignOut;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_out, container, false);

        mProgressbar=(ProgressBar) view.findViewById(R.id.logout_pg);
        btnSignOut=(Button) view.findViewById(R.id.button_confirm_signout);

        mProgressbar.setVisibility(View.GONE);

        setupFirebaseAuth();

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to sign out");

                mProgressbar.setVisibility(View.VISIBLE);
                mAuth.signOut();
                mProgressbar.setVisibility(View.GONE);
                getActivity().finish();
            }
        });

        return view;
    }

    /*
    ----------------------------Firebase-------------------------------------
     */

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
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
