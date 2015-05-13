package com.example.salman.uberapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductFragment extends Fragment {
    private static final String TAG = "SALMAN";
    private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    private JSONObject mProduct;
    private JSONObject mCoords;
    private TextView mProductTextView;
    private String mProductID;
    private Button mRequestButton;
    private String mDestination;
    private String mUrl;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product, parent, false);
        Bundle bundle = this.getArguments();
        String product = bundle.getString(ProductsActivity.PRODUCT_INFO_KEY);
        String coords = bundle.getString(ProductsActivity.COORDS_KEY);
        try {
            mCoords = new JSONObject(coords);
            mProduct = new JSONObject(product);
        }catch(JSONException e){
            Log.e(TAG, e.getMessage());
        }
        mProductTextView = (TextView)v.findViewById(R.id.product);
        mProductTextView.setText(mProduct.toString());
        mRequestButton = (Button)v.findViewById(R.id.product_request_button);
        mRequestButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {


          }
        });

        return v;
    }

}
