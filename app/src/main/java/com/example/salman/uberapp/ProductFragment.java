package com.example.salman.uberapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductFragment extends Fragment {
    private static final String TAG = "SALMAN";
    private JSONObject mProduct;
    private TextView mProductTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product, parent, false);
        Bundle bundle = this.getArguments();
        String product = bundle.getString(ProductsActivity.PRODUCT_INFO_KEY);
        try {
            mProduct = new JSONObject(product);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        mProductTextView = (TextView)v.findViewById(R.id.product);
        mProductTextView.setText(mProduct.toString());
        return v;
    }

}
