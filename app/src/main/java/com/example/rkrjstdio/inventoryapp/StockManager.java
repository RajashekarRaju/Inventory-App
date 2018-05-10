package com.example.rkrjstdio.inventoryapp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

class StockManager {

    /**
     * PreferenceManager for user displayed settings at start of the activity
     * To prevent activity showing same activity at start
     */
    private SharedPreferences pref;

    // Preventing activity for first time launch
    private SharedPreferences.Editor editor;

    // Shared preferences file name
    private static final String PREF_NAME = "welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    @SuppressLint("CommitPrefEdits")
    StockManager(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    // setting activity to launch at first time
    void setFirstTimeLaunch() {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, false);
        editor.commit();
    }

    // checking activity for first time launch.
    boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}
