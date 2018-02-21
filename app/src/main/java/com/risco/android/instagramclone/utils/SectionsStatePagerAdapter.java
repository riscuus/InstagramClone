package com.risco.android.instagramclone.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Albert Risco on 26/11/2017.
 */

public class SectionsStatePagerAdapter extends FragmentStatePagerAdapter{

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final HashMap<Fragment, Integer> mFragments = new HashMap<>();
    private final HashMap<String, Integer> mFragmentsNumbers = new HashMap<>();
    private final HashMap<Integer, String> mFragmentsNames = new HashMap<>();

    public SectionsStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String fragmetName){
        mFragmentList.add(fragment);
        mFragments.put(fragment, mFragmentList.size()-1);
        mFragmentsNumbers.put(fragmetName, mFragmentList.size()-1);
        mFragmentsNames.put(mFragmentList.size()-1, fragmetName);
    }

    /**
     * return the fragment number corresponding to that gragment name
     * @param fragmentName
     * @return
     */
    public Integer getFragmentNumber(String fragmentName){
        if(mFragmentsNumbers.containsKey(fragmentName)){
            return mFragmentsNumbers.get(fragmentName);
        }
        else return null;
    }

    /**
     * return the fragment number corresponding to that gragment name
     * @param fragment
     * @return
     */
    public Integer getFragmentNumber(Fragment fragment){
        if(mFragments.containsKey(fragment)){
            return mFragments.get(fragment);
        }
        else return null;
    }

    /**
     * return the fragment number corresponding to that gragment name
     * @param fragmentNumber
     * @return
     */
    public String getFragmentName(Integer fragmentNumber){
        if(mFragmentsNames.containsKey(fragmentNumber)){
            return mFragmentsNames.get(fragmentNumber);
        }
        else return null;
    }




}
