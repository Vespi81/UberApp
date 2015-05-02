package com.example.salman.uberapp;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class OptionsListFragment extends ListFragment {
    private int mOption;
    private static String TAG = "SALMAN";

    private String[] mOptionsList = new String[]{"View user info", "View products"};
    public static final String OPTIONS_KEY = "com.example.salman.uberapp.Options";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        OptionsAdapter optionsAdapter = new OptionsAdapter();
        setListAdapter(optionsAdapter);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        ActionBar aBar = getActivity().getActionBar();
        aBar.setTitle("Select option");
        //inflater.inflate(R.menu.select_option, menu);
    }

    @Override
    public void onListItemClick (ListView l, View v, int position, long id){
        Log.i(TAG, "onListItemClick: " + position);
        super.onListItemClick(l, v, position, id);
        try{
            Intent i = new Intent(getActivity(), OptionsActivity.class);
            i.putExtra(OPTIONS_KEY, position);
            getActivity().startActivity(i);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    private class OptionsAdapter extends ArrayAdapter<String> {
        public OptionsAdapter() {
            super(getActivity(), 0, mOptionsList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_options, null);
            }
            String opt = mOptionsList[position];
            Button optButton = (Button) convertView.findViewById(R.id.option_button);
            optButton.setText(opt);
            return convertView;
        }
    }
}
