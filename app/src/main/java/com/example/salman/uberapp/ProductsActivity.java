package com.example.salman.uberapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

public class ProductsActivity extends FragmentActivity {
    private static final String TAG = "SALMAN";
    public static final String PRODUCT_INFO_KEY = "com.example.salman.uberapp.product_info";

    private JSONArray mProducts;

    @Override
    public void onCreate(Bundle onSavedInstance){
        super.onCreate(onSavedInstance);
        setContentView(R.layout.activity_products);
        String products = getIntent().getStringExtra(ProductsFragment.PRODUCT_KEY);
        try {
            mProducts = new JSONArray(products);
        }catch(JSONException e){
            Log.e(TAG, e.getMessage());
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.products_pager);
        Adapter pagerAdapter = new Adapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
    }

    private class Adapter extends FragmentPagerAdapter {
        public Adapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Bundle bundle = new Bundle();
            try {
                bundle.putString(PRODUCT_INFO_KEY, mProducts.getJSONObject(i).toString());
            }catch(JSONException e){
                    Log.e(TAG, e.getMessage());
            }
            ProductFragment product = new ProductFragment();
            product.setArguments(bundle);
            return product;
        }

        @Override
        public int getCount() {
            return mProducts.length();
        }
    }
}
