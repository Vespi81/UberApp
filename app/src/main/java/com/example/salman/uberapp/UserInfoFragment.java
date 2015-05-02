package com.example.salman.uberapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class UserInfoFragment extends Fragment {
    private TextView mFirstName;
    private TextView mLastName;
    private TextView mEmail;
    private TextView mPromoCode;
    private ImageView mUserImage;
    private Bitmap mUserBitmap;
    private JSONObject mUserInfo;

    private static final String URL = "https://sandbox-api.uber.com/v1/me";
    private static final String TAG = "SALMAN";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        super.onCreateView(inflater, parent, savedInstanceState);
        Log.i(TAG, "onCreateView UserInfoFragment");
        View v = inflater.inflate(R.layout.fragment_user_info, parent, false);
        mUserImage = (ImageView)v.findViewById(R.id.user_image);
        mFirstName = (TextView)v.findViewById(R.id.first_name);
        mLastName = (TextView)v.findViewById(R.id.last_name);
        mEmail = (TextView)v.findViewById(R.id.email);
        mPromoCode = (TextView)v.findViewById(R.id.promo_code);
        new FetchUserInfo().execute();
        return v;
    }

    private void setTextViews(){
        try{
            Log.i(TAG, "setTextViews()");
            mUserImage.setImageBitmap(mUserBitmap);
            mFirstName.setText(mUserInfo.getString("first_name"));
            mLastName.setText(mUserInfo.getString("last_name"));
            mEmail.setText(mUserInfo.getString("email"));
            mPromoCode.setText(mUserInfo.getString("promo_code"));
        }catch(Exception e){
            Log.e(TAG, "UserInfoFragment: setTextViews: " + e.getMessage());
        }
    }

    private class FetchUserInfo extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Log.i(TAG, "FetchUserInfo doInBackground");
                mUserInfo = HTTPModel.get(URL);
                Log.i(TAG, "FetchUserInfo doInBackground");
            } catch (Exception e) {
                Log.e(TAG, "UserInfoFragment: FetchUserInfo: " + e.getMessage());
            }
            Log.i(TAG, "FetchUserInfo doInBackground");
            return null;
        }

        @Override
        protected void onPostExecute(Void arg){
            Log.i(TAG, "FetchUserInfo onPostExecute");
            this.cancel(true);
            try {
                Log.i(TAG, "FetchUserInfo onPostExecute");
                new FetchUserImage(mUserInfo.getString("picture")).execute();
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private class FetchUserImage extends AsyncTask<Void, Void, Void>{
        private String mUrl;
        public FetchUserImage(String url){
            mUrl = url;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Log.i(TAG, "FetchUserImage doInBack");
                mUserBitmap = HTTPModel.getImage(mUrl);
            } catch (Exception e) {
                Log.e(TAG, "UserInfoFragment: FetchUserImage: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void arg){
            Log.i(TAG, "FetchUserImage onPostExecute");
            this.cancel(true);
            setTextViews();
        }
    }
}
