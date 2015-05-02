package com.example.salman.uberapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class OptionsActivity extends FragmentActivity {
    private int mOption;
    private static String TAG = "SALMAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate OptionsActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        mOption = getIntent().getIntExtra(OptionsListFragment.OPTIONS_KEY, -1);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        try {
            switch (mOption) {
                case 0:
                    ft.add(R.id.options_container, new UserInfoFragment()).commit();
                case 1:
                    ft.add(R.id.options_container, new ProductsFragment()).commit();
                default:
                    Log.e(TAG, "OptionsActivity: " + mOption);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }
}
