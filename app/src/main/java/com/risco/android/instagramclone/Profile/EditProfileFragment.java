package com.risco.android.instagramclone.Profile;

import android.content.Intent;
import android.icu.lang.UCharacterEnums;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.risco.android.instagramclone.R;
import com.risco.android.instagramclone.models.User;
import com.risco.android.instagramclone.models.UserAccountSettings;
import com.risco.android.instagramclone.models.UserSettings;
import com.risco.android.instagramclone.utils.FirebaseMethods;
import com.risco.android.instagramclone.utils.UniversalImageLoader;

/**
 * Created by Albert Risco on 26/11/2017.
 */

public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";

    //editProfile widgets
    private ImageView mProfilePhoto;
    private EditText mDisplayname, mUserName, mWebsite, mDescription, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userID;

    private FirebaseMethods mFirebaseMethods;


    //variables
    private UserSettings mUserSettings;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        mProfilePhoto = (ImageView) view.findViewById(R.id.profile_photo);
        mDisplayname = (EditText) view.findViewById(R.id.name);
        mUserName = (EditText) view.findViewById(R.id.username);
        mWebsite = (EditText) view.findViewById(R.id.website);
        mDescription = (EditText) view.findViewById(R.id.description);
        mEmail = (EditText) view.findViewById(R.id.email);
        mPhoneNumber = (EditText) view.findViewById(R.id.phoneNumber);
        mChangeProfilePhoto = (TextView) view.findViewById(R.id.changeProfilePhoto);

        mFirebaseMethods = new FirebaseMethods(getActivity());

        setProfileImage();

        //Setup back-arrow for navigating to profileAcitvity
        final ImageView backArrow = (ImageView) view.findViewById(R.id.cancel_button);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Navigating back to Profile Acitivity");
                getActivity().overridePendingTransition(0,0);
                getActivity().finish();
            }

        });

        //setup check button
        ImageView checkmark = (ImageView) view.findViewById(R.id.checkmark);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: attempting to save changes");
                saveProfileSettings();
                getActivity().finish();

            }
        });

        setupFirebaseAuth();
        return view;
    }

    private void setProfileImage(){
        Log.d(TAG, "setProfileImage: setting profile image");

        String imageUrl = "https://organicthemes.com/demo/profile/files/2012/12/profile_img.png";

        UniversalImageLoader.setImage(imageUrl, mProfilePhoto, null, "");
    }


    private void setProfileWidgets(UserSettings userSettings){
        //Log.d(TAG, "setUserSettings: Setting widgets with data retrieved from firebase database");
        mUserSettings = userSettings;

        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getUserAccountSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");

        mDisplayname.setText(settings.getDisplay_name());
        mUserName.setText(String.valueOf(settings.getUsername()));
        mEmail.setText(String.valueOf(user.getEmail()));
        mPhoneNumber.setText((String.valueOf(user.getPhone_number())));
        mDescription.setText(settings.getDescription());
        mWebsite.setText(settings.getWebsite());


    }

    /**
     * Retrieve the data frim the widgets and submits it to the database
     */
    private void saveProfileSettings(){
        try {
            final String displayName = mDisplayname.getText().toString();
            final String userName = mUserName.getText().toString();
            final String email = mEmail.getText().toString();
            final long phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());
            final String description = mDescription.getText().toString();
            final String website = mWebsite.getText().toString();


            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = new User();

                    //case1: the user change his username (we have to ensure it is unique):
                    if (!mUserSettings.getUser().getUsername().equals(userName)) {
                        checkIfUsernameExists(userName);

                        //case2: the user didn't change his username:
                    }
                    if (!mUserSettings.getUserAccountSettings().getDisplay_name().equals(displayName)) {
                        //update displayname
                        mFirebaseMethods.updateUserAccountSettings(displayName, null, null, null);

                    }
                    if (!mUserSettings.getUserAccountSettings().getWebsite().equals(website)) {
                        //update website
                        mFirebaseMethods.updateUserAccountSettings(null, website, null, null);


                    }
                    if (!mUserSettings.getUserAccountSettings().getDescription().equals(description)) {
                        //update description
                        mFirebaseMethods.updateUserAccountSettings(null, null, description, null);


                    }
                    if (mUserSettings.getUser().getPhone_number() != phoneNumber) {
                        //update phonenumber
                        mFirebaseMethods.updateUserAccountSettings(null, null, null, phoneNumber);

                    }
                    Toast.makeText(getActivity(), "saved!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception ex){
        Toast.makeText(getActivity(), "The fields can't be blank", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks if the @param userName already exists in the database
     * @param userName
     */

    private void checkIfUsernameExists(final String userName) {
        Log.d(TAG, "checkIfUsernameExists: Checking if the username already exists");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(userName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    //add the username
                    mFirebaseMethods.updateUsername(userName);

                }
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if(singleSnapshot.exists()){
                        Log.d(TAG, "checkIfUserNameExists: FOUND A MATCH:" + singleSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(getActivity(), "That username already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /*
    ----------------------------Firebase------------------------------------
    */


    /**
     * setup the firebase auth object
     */

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting firebase auth");
        mAuth = FirebaseAuth.getInstance();

        mFirebaseDatabase= FirebaseDatabase.getInstance();
        myRef=mFirebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();

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
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //retrieve user information from the database
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));
                //retrieve images for the user in question
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
