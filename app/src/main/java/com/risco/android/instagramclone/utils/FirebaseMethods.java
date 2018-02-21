package com.risco.android.instagramclone.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.risco.android.instagramclone.R;
import com.risco.android.instagramclone.models.User;
import com.risco.android.instagramclone.models.UserAccountSettings;
import com.risco.android.instagramclone.models.UserSettings;

/**
 * Created by Albert Risco on 02/12/2017.
 */

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";

    private Context mContext;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userID;


    public FirebaseMethods(Context context){
        mContext=context;
        mAuth= FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef=mFirebaseDatabase.getReference();

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }

    }

    /**
     * Updates all fields except username
     * @param displayName
     * @param website
     * @param description
     * @param phonenumber
     */

    public void updateUserAccountSettings(String displayName, String website, String description, Long phonenumber){
        Log.d(TAG, "updateUserAccountSettings: updating user account settings");

        if(description!=null){
            myRef.child(mContext.getString(R.string.dbname_users_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_descrption))
                    .setValue(description);
        }

        if(website!=null){
            myRef.child(mContext.getString(R.string.dbname_users_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_website))
                    .setValue(website);
        }

        if(displayName!=null){
            myRef.child(mContext.getString(R.string.dbname_users_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_displayname))
                    .setValue(displayName);
        }

        if(phonenumber!=null){
            myRef.child(mContext.getString(R.string.dbname_users))
                    .child(userID)
                    .child(mContext.getString(R.string.field_phonenumber))
                    .setValue(phonenumber);
        }



    }

    public void updateUsername(String username){
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_users_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }
/*
        public boolean checkIfUsernameExists(String username, DataSnapshot dataSnapshot){
            Log.d(TAG, "checkIfUsernameExists: checking if "+username+" already exists");

            User user = new User();

            for(DataSnapshot ds: dataSnapshot.child(mContext.getString(R.string.dbname_users)).getChildren()){
                Log.d(TAG, "checkIfUsernameExists: datasnapshot: "+ds);

                user.setUsername(ds.getValue(User.class).getUsername());
                Log.d(TAG, "checkIfUsernameExists: username: "+user.getUsername());

                if(StringManipulation.expandUsername(user.getUsername()).equals(username)){
                    Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH "+user.getUsername());

                    return true;
                }

            }
            Log.d(TAG, "checkIfUsernameExists: It didn't already exist.");
            return false;
        }
*/


    /**
     * Register a new email into firebase
     * @param email
     * @param password
     * @param username
     */
    public void registerNewEmail(final String email, String password, final String username){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            userID = mAuth.getCurrentUser().getUid();
                            Toast.makeText(mContext, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                            sendVerificationEmail();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(mContext, "Email sent", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(mContext, "We couldn't send an email verification", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void addNewUser(String email, String username, String description, String website, String profile_photo){
        Log.d(TAG, "addNewUser: Adding the new user to the database.");
        User user = new User(userID, StringManipulation.condenseUsername(username), email, 1);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);

        UserAccountSettings userAccountSettings = new UserAccountSettings(
                description,
                username,
                profile_photo,
                StringManipulation.condenseUsername(username),
                website,
                0,
                0,
                0);


        myRef.child(mContext.getString(R.string.dbname_users_account_settings))
                .child(userID)
                .setValue(userAccountSettings);

    }

    /**
     * Retrieves the account settings for the currently logged in database user_account_settings node
     * @param dataSnapshot
     * @return
     */
    public UserSettings getUserSettings(DataSnapshot dataSnapshot){
        Log.d(TAG, "getUserAccountSettings: retrieving user account settings from firebase");

        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        for(DataSnapshot ds: dataSnapshot.getChildren()){

            //User accounts settings node
            if(ds.getKey().equals(mContext.getString(R.string.dbname_users_account_settings))){
                Log.d(TAG, "getUserAccountSettings: datasnapshot: " + ds);

                try{

                    settings.setDisplay_name(
                            ds.child(userID).getValue(UserAccountSettings.class).getDisplay_name()
                    );

                    settings.setUsername(
                            ds.child(userID).getValue(UserAccountSettings.class).getUsername()
                    );

                    settings.setDescription(
                            ds.child(userID).getValue(UserAccountSettings.class).getDescription()
                    );

                    settings.setFollowers(
                            ds.child(userID).getValue(UserAccountSettings.class).getFollowers()
                    );

                    settings.setFollowing(
                            ds.child(userID).getValue(UserAccountSettings.class).getFollowing()
                    );

                    settings.setPosts(
                            ds.child(userID).getValue(UserAccountSettings.class).getPosts()
                    );

                    settings.setProfile_photo(
                            ds.child(userID).getValue(UserAccountSettings.class).getProfile_photo()
                    );

                    settings.setWebsite(
                            ds.child(userID).getValue(UserAccountSettings.class).getWebsite()
                    );

                    Log.d(TAG, "getUserAccountSettings: retrieved UserAccountSettings information:" +
                            settings.toString());

                }catch (NullPointerException ex){
                    Log.e(TAG, "getUserAccountSettings: NullPointerException" +ex.getMessage());
                }

            }
            //Users node
            if(ds.getKey().equals(mContext.getString(R.string.dbname_users))){
                Log.d(TAG, "getUserAccountSettings: datasnapshot: " + ds);

                try{

                    user.setPhone_number(
                            ds.child(userID).getValue(User.class).getPhone_number()
                    );

                    user.setUser_id(
                            ds.child(userID).getValue(User.class).getUser_id()
                    );

                    user.setUsername(
                            ds.child(userID).getValue(User.class).getUsername()
                    );

                    user.setEmail(
                            ds.child(userID).getValue(User.class).getEmail()
                    );

                    Log.d(TAG, "getUserAccountSettings: retrieved user information:" +
                            user.toString());

                }catch (NullPointerException ex){
                    Log.e(TAG, "getUserAccountSettings: NullPointerException" +ex.getMessage());
                }

            }
        }
        return new UserSettings(user, settings);

    }
}























