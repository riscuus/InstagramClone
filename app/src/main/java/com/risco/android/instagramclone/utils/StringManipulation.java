package com.risco.android.instagramclone.utils;

/**
 * Created by Albert Risco on 02/12/2017.
 */

public class StringManipulation {

    public static String expandUsername(String username){
        return username.replace(".", " ");
    }

    public static String condenseUsername(String username){
        return username.replace(" ", ".");
    }
}
