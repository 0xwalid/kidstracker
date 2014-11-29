package com.example.kidstracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
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
            long lastnotified = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("last_notified", 0);
            String lastcolor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("last_color", "green");
            long time = System.currentTimeMillis()/1000;
//            Log.i("kt", String.valueOf(time));
            if (lastnotified == 0) {
            	lastnotified = time;
            	PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putLong("last_notified", time).commit();
            } else if (res.getString("type").equals("red")) {
				if (time - lastnotified >= 180 || !lastcolor.equals("red")) {
					PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putLong("last_notified", time).commit();
					PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("last_color", "red").commit();
					sendNotif("Alert Notification", "The kid is out of the allowed zones!");
				}
			} else if(res.getString("type").equals("gray")) {
				if (time - lastnotified >= 420  || !lastcolor.equals("gray")) {
					PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putLong("last_notified", time).commit();
					PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("last_color", "gray").commit();
					sendNotif("Alert Notification", "The kid is in the zone: " + res.getString("region"));
				}
			} else {
				if (time - lastnotified >= 3600 && res.getString("type").equals("green") || res.getString("type").equals("green") && !lastcolor.equals("green")) {
					PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putLong("last_notified", time).commit();
					PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("last_color", "green").commit();
					sendNotif("Periodic Notification", "The kid is in the expected zone: "+res.getString("region"));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendNotif(String title, String message) {
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_kids)
		        .setContentTitle(title)
		        .setAutoCancel(true)
		        .setContentText(message);
		Intent resultIntent = new Intent(this, MainActivity.class);
		resultIntent.putExtra("map", true);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());
	}
	public class MyThread extends Thread {
	 
		 @Override
		 public void run() {
		  // TODO Auto-generated method stub
			 boolean deactivated = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("deactivate", false);
			 boolean logged_in = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("logged_in", false);
		  while(true && !deactivated && logged_in) {
		   try {
			   Intent intent = new Intent();
			   intent.setAction(LOC_INFO);
			   JSONParser caller = new JSONParser();
				 String user = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("user", "");

			   JSONObject res = caller.getReq("http://10.0.2.2:8999/getLocation/" + user);
			   try {
				   if(res != null) {
					   res.put("type", res.getString("zone_type"));
					   res.put("region", res.getString("region_name"));
					   Log.i("kt", res.toString());
			           long time = System.currentTimeMillis()/1000;
					   PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putLong("last_checked", time).commit();
					   PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("current_zone", res.getString("region")).commit();
					   intent.putExtra("lat", res.getDouble("lat"));
					   intent.putExtra("lng", res.getDouble("lng"));
					   intent.putExtra("region", res.getString("region"));
					   intent.putExtra("type", res.getString("type"));
					   sendBroadcast(intent);
					   notifyIfNeeded(res);
					   PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("last_color", res.getString("type")).commit();
				   } else {
					   
				   }
				   
			   } catch (JSONException e) {
				   
			   }
		       
			   Thread.sleep(5000);
		   } catch (InterruptedException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		   }
		  }
		 }
	 
	}
	 
}