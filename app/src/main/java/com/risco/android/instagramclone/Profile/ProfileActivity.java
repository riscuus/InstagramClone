package com.risco.android.instagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.risco.android.instagramclone.Login.LoginActivity;
import com.risco.android.instagramclone.R;
import com.risco.android.instagramclone.models.User;
import com.risco.android.instagramclone.models.UserAccountSettings;
import com.risco.android.instagramclone.models.UserSettings;
import com.risco.android.instagramclone.utils.BottomNavigationViewHelper;
import com.risco.android.instagramclone.utils.FirebaseMethods;
import com.risco.android.instagramclone.utils.GridImageAdapter;
import com.risco.android.instagramclone.utils.UniversalImageLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Albert Risco on 23/11/2017.
 */

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int ACTIVITY_NUM=4;
    private static final int NUM_GRID_PHOTOS=3;

    private Context mContext = ProfileActivity.this;
    private ProgressBar mProgressbar;
    private ImageView mProfilePhoto;
    private TextView mProfileName;
    private TextView mPosts;
    private TextView mFollowing;
    private TextView mFollowers;
    private TextView mDisplayname;
    private TextView mDescription;
    private TextView mWebsite;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private FirebaseMethods mFirebaseMethods;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started");
        setupBottomNavigationView();
        setupToolbar();
        mProgressbar = (ProgressBar) findViewById(R.id.profile_progressBar);
        mProgressbar.setVisibility(View.GONE);
        mProfilePhoto = (ImageView) findViewById(R.id.profile_photo);
        mProfileName = (TextView) findViewById(R.id.profileName);
        mPosts = (TextView) findViewById(R.id.tvPosts);
        mFollowing = (TextView) findViewById(R.id.tvFollowing);
        mFollowers = (TextView) findViewById(R.id.tvfollowers);
        mDisplayname = (TextView) findViewById(R.id.display_name);
        mDescription = (TextView) findViewById(R.id.display_info);
        mWebsite = (TextView) findViewById(R.id.display_website);

        mFirebaseMethods = new FirebaseMethods(mContext);

        TextView mEditProfile = (TextView) findViewById(R.id.textEditProfile);
        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
                overridePendingTransition(0,0);
            }
        });

        tempGridSetup();
        setupFirebaseAuth();
    }

    private void setProfileWidgets(UserSettings userSettings){
        //Log.d(TAG, "setUserSettings: Setting widgets with data retrieved from firebase database");

        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getUserAccountSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");

        mProfileName.setText(settings.getUsername());
        mPosts.setText(String.valueOf(settings.getPosts()));
        mFollowers.setText(String.valueOf(settings.getFollowers()));
        mFollowing.setText(String.valueOf(settings.getFollowing()));
        mDisplayname.setText(settings.getDisplay_name());
        mDescription.setText(settings.getDescription());
        mWebsite.setText(settings.getWebsite());

        setProfileImage(settings);

    }
    //*********************************************Setup the grid images*********************************************
    private void tempGridSetup(){
        ArrayList<String> imgURLs = new ArrayList<>();
        imgURLs.add("https://i.pinimg.com/736x/60/cf/db/60cfdb96744a9cc380f96b23b1d7550d--cute-instagram-photo-ideas-cute-pictures-for-instagram.jpg");
        imgURLs.add("https://assets.hongkiat.com/uploads/yummy-instagram-accounts/2-food-instagram-accounts.jpg");
        imgURLs.add("https://i.pinimg.com/736x/df/7a/97/df7a97858a3db730b10a7c86172c782d--instagram-photo-poses-instagram-ideas-couples.jpg");
        imgURLs.add("https://i.pinimg.com/736x/6a/16/08/6a1608241cabdd943846c841b124f5f5--tumblr-lights-photography-instagram-photography-ideas.jpg");
        imgURLs.add("https://i.pinimg.com/736x/a3/78/27/a37827a46c9efe7b9362861f182a2e70--grunge-photography-girl-photography.jpg");
        imgURLs.add("https://images.fastcompany.net/image/upload/w_596,c_limit,q_auto:best,f_auto,fl_lossy/fc/3068655-inline-i-1-first-instagram-photo-ever.jpg");
        imgURLs.add("http://campusghanta.com/wp-content/uploads/2016/08/instagram_1920x1080_37413.jpg");
        imgURLs.add("https://www.istockphoto.com/resources/images/PhotoFTLP/img_63351521.jpg");
        imgURLs.add("https://static.pexels.com/photos/129441/pexels-photo-129441.jpeg");
        imgURLs.add("http://wallpaper.pickywallpapers.com/2560x1600/emma-watson-black-and-white-profile.jpg");

        setupImageGrid(imgURLs);

    }

    private void setupImageGrid(ArrayList<String> imgURLs){
        GridView gridView = (GridView) findViewById(R.id.profile_gridView);

        int gridWith = getResources().getDisplayMetrics().widthPixels;

        int imageWith = gridWith/NUM_GRID_PHOTOS;

        gridView.setColumnWidth(imageWith);

        GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, "", imgURLs);
        gridView.setAdapter(adapter);
    }

    //***********************************************************************************************************************



    private void setupToolbar(){
        Log.d(TAG, "setupToolbar: setting up profile toolbar");
        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);

        ImageView profileMenu =(ImageView) findViewById(R.id.profileMenu);

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigating to account Settings");

                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
                overridePendingTransition(0,0);
            }
        });
    }

    private void setProfileImage(UserAccountSettings settings){
        String imgUrl = settings.getProfile_photo();
        Log.d(TAG, "setProfileImage: setting profile Photo:" +imgUrl);
        UniversalImageLoader.setImage(imgUrl, mProfilePhoto, null, "");
    }



    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);


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
