package com.example.salman.uberapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductsFragment extends Fragment {
    private EditText mAddressInput;
    private Button mOK;
    private String mUserInput;
    private JSONObject mGeocode;
    private ListView mProductsListView;
    private String mLatitude;
    private String mLongitude;
    private JSONObject mAddress;
    private JSONArray mProducts;

    private static final String TAG = "SALMAN";
    private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String URL = "https://sandbox-api.uber.com/v1/products";
    public static final String PRODUCT_KEY = "com.example.salman.uberapp.product";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView Begin");
        View v = inflater.inflate(R.layout.fragment_products, parent, false);
        mOK = (Button) v.findViewById(R.id.ok_button);
        mAddressInput = (EditText) v.findViewById(R.id.enter_text);
        mOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mUserInput = mAddressInput.getText().toString();
                Log.i(TAG, "onClick: " + mUserInput);
                if (mUserInput.length() == 0) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Error")
                            .setMessage("No input")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .create()
                            .show();
                } else {
                    new FetchCoordinates().execute();
                }
            }
        });
        Log.i(TAG, "onCreateView End");
        return v;
    }

    public void processLocation() throws JSONException {
        String status = mGeocode.getString("status");
        Log.i(TAG, "STATUS: " + status);
        switch (status) {
            case "OK":
                final JSONArray results = mGeocode.getJSONArray("results");
                Log.i(TAG, "" + results.length());
                if (results.length() > 1) {
                    CharSequence[] addresses = new String[results.length()];
                    for (int i = 0; i < results.length(); i++) {
                        addresses[i] = results.getJSONObject(i).getString("formatted_address");
                        Log.i(TAG, addresses[i].toString());
                    }
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Select address")
                            .setItems(addresses, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        mAddress = results.getJSONObject(which);
                                        mLatitude = mAddress.getJSONObject("geometry").getJSONObject("location").getString("lat");
                                        mLongitude = mAddress.getJSONObject("geometry").getJSONObject("location").getString("lng");
                                        Log.i(TAG, "LAT: " + mLatitude + " LNG: " + mLongitude);
                                        new FetchProducts().execute();
                                    } catch (JSONException e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                }
                            })
                            .create()
                            .show();
                } else {
                    mAddress = results.getJSONObject(0);
                    mLatitude = mAddress.getJSONObject("geometry").getJSONObject("location").getString("lat");
                    mLongitude = mAddress.getJSONObject("geometry").getJSONObject("location").getString("lng");
                    Log.i(TAG, "LAT: " + mLatitude + " LNG: " + mLongitude);
                    new FetchProducts().execute();
                }
            case "ZERO_RESULTS":
                new AlertDialog.Builder(getActivity())
                        .setTitle("Error")
                        .setMessage("No results found")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();
            default:
                return;
        }
    }

    public void processProducts() throws JSONException {
        if (mProducts.length() == 0) {
            // TODO
        }
        Intent i = new Intent(getActivity(), ProductsActivity.class);
        i.putExtra(PRODUCT_KEY, mProducts.toString());
        startActivity(i);
    }

//        String[] productNames = new String[mProducts.length()];
//        String[] productDescriptions = new String[mProducts.length()];
//        for(int i = 0;i < mProducts.length(); i++){
//            productNames[i] = mProducts.getJSONObject(i).getString("display_name");
//            productDescriptions[i] = mProducts.getJSONObject(i).getString("description");
//        }
//        
//        ProductsAdapter productsAdapter = new ProductsAdapter(productNames, productDescriptions);
//        mProductsListView = (ListView)getView().findViewById(R.id.products_list);
//        mProductsListView.setAdapter(productsAdapter);
//        mProductsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                Intent i = new Intent(getActivity(), ProductInfoActivity.class);
//                try {
//                    i.putExtra(PRODUCT_KEY, mProducts.getJSONObject(arg2).toString());
//                } catch (JSONException e) {
//                    Log.e(TAG, e.getMessage());
//                }
//                getActivity().startActivity(i);
//            }
//        });
//    }

//    private class ProductsAdapter extends ArrayAdapter<String> {
//        private String[] mNames;
//        private String[] mDescriptions;
//        
//        public ProductsAdapter(String[] names, String[] descriptions){
//            super(getActivity(), 0, names);
//            mNames = names;
//            mDescriptions = descriptions;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_products_list, null);
//            }
//            LinearLayout linearLayout = (LinearLayout)convertView.findViewById(R.id.product);
//            TextView productName = (TextView)linearLayout.findViewById(R.id.product_name);
//            TextView productDescription = (TextView)linearLayout.findViewById(R.id.product_description);
//            productName.setText(mNames[position]);
//            productDescription.setText(mDescriptions[position]);
//            return convertView;
//        }
//    }

    private class FetchCoordinates extends AsyncTask<Void, Void, Void> {
        @Override
        public Void doInBackground(Void... arg) {
            String url = Uri.parse(GEOCODE_URL).buildUpon()
                    .appendQueryParameter("address", mUserInput)
                    .build().toString();
            mGeocode = HTTPModel.get(url);
            return null;
        }

        @Override
        public void onPostExecute(Void arg) {
            this.cancel(true);
            try {
                processLocation();
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private class FetchProducts extends AsyncTask<Void, Void, Void> {
        @Override
        public Void doInBackground(Void... arg) {
            String url = Uri.parse(URL).buildUpon()
                    .appendQueryParameter("latitude", String.valueOf(mLatitude))
                    .appendQueryParameter("longitude", String.valueOf(mLongitude))
                    .build().toString();
            try {
                mProducts = HTTPModel.get(url).getJSONArray("products");
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        public void onPostExecute(Void arg) {
            this.cancel(true);
            try {
                processProducts();
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
