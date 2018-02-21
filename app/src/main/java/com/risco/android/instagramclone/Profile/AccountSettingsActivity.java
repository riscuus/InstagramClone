package com.risco.android.instagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.risco.android.instagramclone.R;
import com.risco.android.instagramclone.utils.SectionPagerAdapter;
import com.risco.android.instagramclone.utils.SectionsStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Albert Risco on 26/11/2017.
 */

public class AccountSettingsActivity extends AppCompatActivity {
    private static final String TAG = "AccountSettingsActivity";

    private Context mContext = AccountSettingsActivity.this;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;
    private SectionsStatePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        Log.d(TAG, "onCreate: started");

        mViewPager = (ViewPager) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.accountSettingsRelLayout);

        setupSettingsList();

        setupFragments();

        //Setup back-arrow for navigating to profileAcitvity
        final ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(0,0);
                finish();
            }

        });
        getIncomingIntent();
    }

    private void setupFragments(){
        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile)); //fragment 0
        pagerAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out)); //fragment 1

    }

    private void setViewPager(int fragmentNumber){
        mRelativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: navigating to "+ fragmentNumber);

        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(fragmentNumber);

    }

    private void setupSettingsList(){
        Log.d(TAG, "setupSettingsList: initializating account settings list");
        ListView listView = (ListView) findViewById(R.id.list_accountSettings);


        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile)); //fragment 0
        options.add(getString(R.string.sign_out)); //fragment 1

        ArrayAdapter adapter = new ArrayAdapter(mContext, R.layout.layout_list_item_settings, options);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: navigating to fragment #"+ i);

                setViewPager(i);
            }
        });
    }

    private void getIncomingIntent(){
        Intent intent = getIntent();

        if(intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "getIncomingIntent: received incoming intent from: " + getString(R.string.profile_activity));

            setViewPager(pagerAdapter.getFragmentNumber(getString(R.string.edit_profile)));

        }
    }


}
