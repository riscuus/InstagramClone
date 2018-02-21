package com.risco.android.instagramclone.Home;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.risco.android.instagramclone.R;

/**
 * Created by Albert Risco on 23/11/2017.
 */

public class HomeFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        return view;

    }
}
