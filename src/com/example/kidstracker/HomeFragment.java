package com.example.kidstracker;

import java.util.Calendar;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
 
public class HomeFragment extends Fragment {
     
    public HomeFragment(){}
    TextView regionText;
    TextView checkedText;
    MyReceiver myReceiver;
    HomeFragment that = this;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        regionText   = (TextView) rootView.findViewById(R.id.textView2);
  	  	checkedText   = (TextView) rootView.findViewById(R.id.textView4);
        return rootView;
    }
    
    private void registerReciever() {
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MapService.LOC_INFO);
        getActivity().registerReceiver(myReceiver, intentFilter);
    }

    @Override
	public void onResume() {
    	if (myReceiver == null) {
    		registerReciever();
    	}
    	super.onResume();
    }
    @Override
	public void onPause() {
     // TODO Auto-generated method stub
    	if (myReceiver != null) {
    		getActivity().unregisterReceiver(myReceiver);
    		myReceiver = null;
    	}
     
     super.onPause();
    } 
    
    private class MyReceiver extends BroadcastReceiver{
      	 
      	 @Override
      	 public void onReceive(Context arg0, Intent arg1) {
			String region = arg1.getStringExtra("region");
			String color = arg1.getStringExtra("type");
			long last_checked = PreferenceManager.getDefaultSharedPreferences(getActivity()).getLong("last_checked", 0);
			Calendar cal = Calendar.getInstance(Locale.ENGLISH);
			long time = System.currentTimeMillis()/1000;
			//					String region = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("region", "retreiving..");
			cal.setTimeInMillis(last_checked*1000);
			String date = DateFormat.format("dd/MM hh:mm:ss", cal).toString();
			if (time - last_checked < 10) {
				regionText.setText(region);
				if (color.equals("green"))
					regionText.setTextColor(Color.GREEN);
				else if (color.equals("red")) {
					regionText.setTextColor(Color.RED);
					regionText.setText("OUT");
				} else if (color.equals("gray"))
					regionText.setTextColor(Color.GRAY);
				checkedText.setText(date);
			}
		   
      	
      	 }
      	 
      	}
}