package com.example.kidstracker;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

public class MapsFragment extends Fragment {

    private MapFragment fragment;
    private GoogleMap map;
    private static View view;
    private static Marker positionMarker;
    private LatLng clickedPoint;
    MyReceiver myReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
	        } catch (InflateException e) {
	            /* map is already there, just return view as it is */
	        }
        
	      try { 
		      if (map == null) {
		         map = ((MapFragment) getFragmentManager().
		         findFragmentById(R.id.map)).getMap();

		         if (getArguments().getBoolean("regions", false)) {
			         map.setOnMapClickListener(new OnMapClickListener(){
			        	    @Override
			        	    public void onMapClick(LatLng point) {
			        	    	clickedPoint = point;
			        	    	Dialog d = createDialog(false, 0);
			        	    	d.show();

			        	        final NumberPicker np1 = (NumberPicker) d.findViewById(R.id.numberPicker1);
			        	         np1.setMaxValue(24);
			        	         np1.setMinValue(1);
			        	        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.numberPicker2);
			        	         np2.setMaxValue(59);
			        	         np2.setMinValue(0);
				        	    final NumberPicker np3 = (NumberPicker) d.findViewById(R.id.numberPicker3);
				        	     np3.setMaxValue(24);
				        	     np3.setMinValue(1);
				        	     final NumberPicker np4 = (NumberPicker) d.findViewById(R.id.numberPicker4);
				        	     np4.setMaxValue(59);
				        	     np4.setMinValue(0);
				        	     final NumberPicker np5 = (NumberPicker) d.findViewById(R.id.numberPicker5);
				        	     np5.setMaxValue(500);
				        	     np5.setMinValue(1);
				        	     
			        	    }
			         });
			         
			         map.setOnMarkerClickListener(new OnMarkerClickListener() {
						
						@Override
						public boolean onMarkerClick(final Marker marker) {
							String name = marker.getTitle();
							final DBHelper db = new DBHelper(getActivity());
							final Region region = db.getRegion("r_name", name);
							
							AlertDialog  alertDialog = new AlertDialog.Builder(getActivity()).create();

						    alertDialog.setTitle("Dialog Options");

						    alertDialog.setMessage("Choose one of the options:");

						    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Edit Region", new DialogInterface.OnClickListener() {

						      public void onClick(DialogInterface dialog, int id) {

						        
						        LayoutInflater inflater = getActivity().getLayoutInflater();
						        final View dd = inflater.inflate(R.layout.dialog, null);
						        
		
					        	   createDialog(true, region.id).show();
						    } }); 

						    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Delete Region", new DialogInterface.OnClickListener() {

						      public void onClick(DialogInterface dialog, int id) {

						    	  db.deleteRegion(region.id);
						    	  marker.remove();
						          Toast.makeText(getActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
						        

						    }}); 

						    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

						      public void onClick(DialogInterface dialog, int id) {

						        //...

						    }});
						    
						    alertDialog.show();

							
							return false;
						}
					});
		         } else {
		        	 map.setOnMapClickListener(null);
		         }
		      }
			  map.setMapType(GoogleMap.MAP_TYPE_HYBRID);  // set maptype to hybrid
	      } catch (Exception e) {
			   e.printStackTrace();
	      }
        return view;    
    }
    
    
    public Dialog createDialog(Boolean edit, int id) {
    	final ArrayList mSelectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title    LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dd = inflater.inflate(R.layout.dialog, null);
        if (edit) {
        	final DBHelper db = new DBHelper(getActivity());
			final Region region = db.getRegion("id", String.valueOf(id));
     	   EditText mEdit   = (EditText) dd.findViewById(R.id.rName);
    	   
    	   mEdit.setText(region.name);
	       NumberPicker np1 = (NumberPicker) dd.findViewById(R.id.numberPicker1);
	       NumberPicker np2 = (NumberPicker) dd.findViewById(R.id.numberPicker2);
	       NumberPicker np3 = (NumberPicker) dd.findViewById(R.id.numberPicker3);
	       NumberPicker np4 = (NumberPicker) dd.findViewById(R.id.numberPicker4);
	       NumberPicker np5 = (NumberPicker) dd.findViewById(R.id.numberPicker5);
	         np1.setMaxValue(24);
	         np1.setMinValue(1);
	         np2.setMaxValue(59);
	         np2.setMinValue(0);
    	     np3.setMaxValue(24);
    	     np3.setMinValue(1);
    	     np4.setMaxValue(59);
    	     np4.setMinValue(0);
    	     np5.setMaxValue(500);
    	     np5.setMinValue(1);
	       np1.setValue(Integer.parseInt(region.from.split(":")[0]));
	       np2.setValue(Integer.parseInt(region.from.split(":")[1]));
	       np3.setValue(Integer.parseInt(region.to.split(":")[0]));
	       np4.setValue(Integer.parseInt(region.to.split(":")[1]));
	       np5.setValue(region.radius);
    	   for (int i = 1; i <= 7; i++) {
    		   int resID = dd.getResources().getIdentifier("checkBox"+i,
    				    "id", getActivity().getPackageName());
    		   CheckBox check = (CheckBox) dd.findViewById(resID);
    		   if (region.days.charAt(i-1) == '1') {
    			   check.setChecked(true);
    		   }
    	   }
    	   
        }
        builder.setView(dd);

        builder.setTitle(edit ? "Create Region" : "Edit Region")
               .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                	   
    				   Region region = new Region();

                	   EditText mEdit   = (EditText) dd.findViewById(R.id.rName);
                	   
                	   region.name = mEdit.getText().toString();
	        	       NumberPicker np1 = (NumberPicker) dd.findViewById(R.id.numberPicker1);
	        	       NumberPicker np2 = (NumberPicker) dd.findViewById(R.id.numberPicker2);
	        	       region.from = np1.getValue() + ":" + np2.getValue();
		        	   NumberPicker np3 = (NumberPicker) dd.findViewById(R.id.numberPicker3);
		        	   NumberPicker np4 = (NumberPicker) dd.findViewById(R.id.numberPicker4);
		        	   region.to = np3.getValue() + ":" + np4.getValue();
		        	   NumberPicker np5 = (NumberPicker) dd.findViewById(R.id.numberPicker5);
		        	   region.radius = np5.getValue();
		        	   List<String> checkboxes =   new ArrayList<String>();
		        	   String daysBitmask = new String();
		        	   for (int i = 1; i <= 7; i++) {
		        		   int resID = dd.getResources().getIdentifier("checkBox"+i,
		        				    "id", getActivity().getPackageName());
		        		   CheckBox check = (CheckBox) dd.findViewById(resID);
		        		   if (check.isChecked()) {
		        			   checkboxes.add((String) check.getText());
		        			   daysBitmask += "1";
		        		   } else {
		        			   daysBitmask += "0";
		        		   }
		        	   }
		        	   region.days = daysBitmask;
		        	   region.lat = clickedPoint.latitude;
		        	   region.lng = clickedPoint.longitude;
		        	   DBHelper db = new DBHelper(getActivity());
		        	   db.addRegion(region);
		        	   Log.i("kt", db.getRegion("r_name", region.name).days);
		    		   map.addMarker(new MarkerOptions()
		    		   					  .position(clickedPoint)
		    		   					  .title(mEdit.getText().toString()));
    				   
    				   CircleOptions circleOptions = new CircleOptions()
    				    .center(clickedPoint)
    				    .radius(np3.getValue()); // In meters
    				   map.addCircle(circleOptions);
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                   }
               });

        return builder.create();
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
   		 
   		 
   	  double lat = arg1.getDoubleExtra("lat", 0);
   	  double lng = arg1.getDoubleExtra("lng", 0);
   	  if (positionMarker != null) {
   		  positionMarker.setPosition(new LatLng(lat,lng));
   	  } else {
   		   LatLng positionLatLng = new LatLng(lat, lng);
		   map.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, 15));
		   map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
		   positionMarker = map.addMarker(new MarkerOptions().
		   position(positionLatLng).title("Kid").icon(BitmapDescriptorFactory.fromResource(R.drawable.kma)));
		   
		   CircleOptions circleOptions = new CircleOptions()
		    .center(positionLatLng)
		    .radius(10); // In meters
		   map.addCircle(circleOptions);
   	  }
   	 }
   	 
   	}
}


