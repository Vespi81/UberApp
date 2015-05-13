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
    private EditText mStartAddressInput;
    private EditText mEndAddressInput;
    private Button mOK;
    private String mStartAddressText;
    private String mEndAddressText;
    private JSONObject mGeocode;
    private ListView mProductsListView;
    private String mStartLatitude;
    private String mStartLongitude;
    private String mEndLatitude;
    private String mEndLongitude;
    private JSONObject mStartAddress;
    private JSONObject mEndAddress;
    private JSONArray mProducts;

    private static final String TAG = "SALMAN";
    private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String URL = "https://sandbox-api.uber.com/v1/products";
    public static final String PRODUCTS_KEY = "com.example.salman.uberapp.product";
    public static final String COORDS_KEY = "com.example.salman.uberapp.coords"

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView Begin");
        View v = inflater.inflate(R.layout.fragment_products, parent, false);
        mOK = (Button) v.findViewById(R.id.ok_button);
        mStartAddressInput = (EditText) v.findViewById(R.id.enter_start_text);
        mEndAddressInput = (EditText)v.findViewById(R.id.enter_end_text);
        mOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mStartAddressText = mStartAddressInput.getText().toString();
                mEndAddressText = mEndAddressInput.getText().toString();
                if ((mStartAddressText.isEmpty())||(mEndAddressText.isEmpty())) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Error")
                            .setMessage("Start or end address fields empty")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .create()
                            .show();
                } else {
                    new FetchStartCoordinates().execute();
                }
            }
        });
        Log.i(TAG, "onCreateView End");
        return v;
    }

    public void processStartLocation() throws JSONException {
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
                            .setTitle("Select start address")
                            .setItems(addresses, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        JSONObject address = results.getJSONObject(which);
                                        mStartLatitude = address.getJSONObject("geometry").getJSONObject("location").getString("lat");
                                        mStartLongitude = address.getJSONObject("geometry").getJSONObject("location").getString("lng");
                                        new FetchEndCoordinates().execute();
                                    } catch (JSONException e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                }
                            })
                            .create()
                            .show();
                } else {
                    JSONObject address = results.getJSONObject(0);
                    mStartLatitude = address.getJSONObject("geometry").getJSONObject("location").getString("lat");
                    mStartLongitude = address.getJSONObject("geometry").getJSONObject("location").getString("lng");
                    new FetchEndCoordinates().execute();
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
                return;
            default:
                return;
        }
    }

    public void processEndLocation() throws JSONException {
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
                            .setTitle("Select end address")
                            .setItems(addresses, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        JSONObject address = results.getJSONObject(which);
                                        mEndLatitude = address.getJSONObject("geometry").getJSONObject("location").getString("lat");
                                        mEndLongitude = address.getJSONObject("geometry").getJSONObject("location").getString("lng");
                                        new FetchProducts().execute();
                                    } catch (JSONException e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                }
                            })
                            .create()
                            .show();
                } else {
                    JSONObject address = results.getJSONObject(0);
                    mEndLatitude = address.getJSONObject("geometry").getJSONObject("location").getString("lat");
                    mEndLongitude = address.getJSONObject("geometry").getJSONObject("location").getString("lng");
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
                return;
            default:
                return;
        }
    }

    public void processProducts() throws JSONException {
        if (mProducts.length() == 0) {
            // TODO
        }
        Intent i = new Intent(getActivity(), ProductsActivity.class);
        i.putExtra(PRODUCTS_KEY, mProducts.toString());
        JSONObject coords = new JSONObject();
        coords.put("startLat", mStartLatitude);
        coords.put("startLng", mStartLongitude);
        coords.put("endLat", mEndLatitude);
        coords.put("endLng", mEndLongitude);
        i.putExtra(COORDS_KEY, coords.toString());
        startActivity(i);
    }

    private class FetchStartCoordinates extends AsyncTask<Void, Void, Void> {

        @Override
        public Void doInBackground(Void... arg) {
            String url = Uri.parse(GEOCODE_URL).buildUpon()
                    .appendQueryParameter("address", mStartAddressText)
                    .build().toString();
            mGeocode = HTTPModel.get(url);
            return null;
        }

        @Override
        public void onPostExecute(Void arg) {
            this.cancel(true);
            try {
                processStartLocation();
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private class FetchEndCoordinates extends AsyncTask<Void, Void, Void> {
        @Override
        public Void doInBackground(Void... arg) {
            String url = Uri.parse(GEOCODE_URL).buildUpon()
                    .appendQueryParameter("address", mEndAddressText)
                    .build().toString();
            mGeocode = HTTPModel.get(url);
            return null;
        }

        @Override
        public void onPostExecute(Void arg) {
            this.cancel(true);
            try {
                processEndLocation();
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private class FetchProducts extends AsyncTask<Void, Void, Void> {
        @Override
        public Void doInBackground(Void... arg) {
            String url = Uri.parse(URL).buildUpon()
                    .appendQueryParameter("latitude", String.valueOf(mStartLatitude))
                    .appendQueryParameter("longitude", String.valueOf(mStartLongitude))
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
