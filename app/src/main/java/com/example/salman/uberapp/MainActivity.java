package com.example.salman.uberapp;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private WebView mWebView;
    public static JSONObject mAccessTokenObj;

    private static final String TAG = "SALMAN";
    private static final String OAUTH_URL = "https://login.uber.com/oauth/authorize";
    private static final String OAUTH_ACCESS_TOKEN_URL = "https://login.uber.com/oauth/token";
    private static final String CLIENT_ID = "IGYZCY26yNmP_jT5TSC4LRAassVZoHsM";
    private static final String CLIENT_SECRET = "wJ3K7mNBHiQyIuY-zUUyedvsEWj0O-6RHIivsCet";
    private static final String CALLBACK_URL = "https://sandbox-api.uber.com/v1/";
    private static final String RESP_TYPE = "code";
    private static final String SCOPE = "profile history_lite request";
    private static final String GRANT_TYPE = "authorization_code";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String url = Uri.parse(OAUTH_URL).buildUpon()
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("response_type", RESP_TYPE)
                .appendQueryParameter("scope", SCOPE)
                .build().toString();

        mWebView = (WebView)findViewById(R.id.auth_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            //public void onPageStarted(WebView view, String url, Bitmap favicon){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                try {
                    Log.i(TAG, "MainActivity: URL: " + url);
                    int codeIndex = url.indexOf("code=");
                    if (codeIndex == -1) {
                        return false;
                    }
                    String accessCode = url.substring(codeIndex + 5);
                    Log.i(TAG, "ACCESS CODE: " + accessCode);
                    List<NameValuePair> accessTokenPair = new ArrayList<NameValuePair>();
                    accessTokenPair.add(new BasicNameValuePair("client_id", CLIENT_ID));
                    accessTokenPair.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
                    accessTokenPair.add(new BasicNameValuePair("code", accessCode));
                    accessTokenPair.add(new BasicNameValuePair("grant_type", GRANT_TYPE));
                    accessTokenPair.add(new BasicNameValuePair("redirect_uri", CALLBACK_URL));
                    final FetchInfo fetchInfo = new FetchInfo(OAUTH_ACCESS_TOKEN_URL, accessTokenPair);
                    fetchInfo.execute();
                } catch (Exception e) {
                    Log.e(TAG, "MainActivity: " + e.getMessage());
                }
                return true;
            }
        });
        mWebView.loadUrl(url);
    }

    private class FetchInfo extends AsyncTask<Void, Void, Void>{
        private String mUrl;
        private List<NameValuePair> mAccessTokenPair;

        FetchInfo(String url, List<NameValuePair> accessTokenPair){
            mUrl = url;
            mAccessTokenPair = accessTokenPair;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                mAccessTokenObj = HTTPModel.post(mUrl, mAccessTokenPair);
            } catch (Exception e) {
                Log.e(TAG, "MainActivity: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void arg){
            if(mWebView != null){
                ViewGroup v = (ViewGroup)mWebView.getParent();
                v.removeView(mWebView);
                mWebView.clearHistory();
                mWebView.clearCache(true);
                mWebView.loadUrl("about:blank");
                mWebView.pauseTimers();
                mWebView.destroy();
            }
            this.cancel(true);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new OptionsListFragment())
                    .commit();

        }
    }
}
