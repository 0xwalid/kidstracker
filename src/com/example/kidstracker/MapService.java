package com.example.kidstracker;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MapService extends Service {
	final static String LOC_INFO = "LOC_INFO";
	@Override
	public IBinder onBind(Intent arg0) {
	 // TODO Auto-generated method stub
	 return null;
	}
	 
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	 // TODO Auto-generated method stub
//	     meMap.put("green",);
	 MyThread myThread = new MyThread();
	 myThread.start();
	 
	 return super.onStartCommand(intent, flags, startId);
	}
	
	public void notifyIfNeeded(JSONObject res) {
		try {
			if (res.getString("type").equals("red")) {
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class MyThread extends Thread {
	 
		 @Override
		 public void run() {
		  // TODO Auto-generated method stub
			 
		  while(true) {
		   try {
			   Intent intent = new Intent();
			   intent.setAction(LOC_INFO);
//			   Log.i("kt", "loggging");
			   JSONParser caller = new JSONParser();
			   JSONObject res = caller.getJSONFromUrl("http://10.0.2.2:1337/user/getLocation", new ArrayList<NameValuePair>());
			   try {
				   if(res != null) {
					   intent.putExtra("lat", res.getDouble("lat"));
					   intent.putExtra("lng", res.getDouble("lng"));
					   intent.putExtra("region", res.getString("region"));
					   intent.putExtra("type", res.getString("type"));
					   notifyIfNeeded(res);
				   } else {
					   
				   }
				   
			   } catch (JSONException e) {
				   
			   }
		       sendBroadcast(intent);
			   Thread.sleep(5000);
		   } catch (InterruptedException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		   }
		  }
		 }
	 
	}
	 
}