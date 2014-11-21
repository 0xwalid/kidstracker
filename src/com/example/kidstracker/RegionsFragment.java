package com.example.kidstracker;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RegionsFragment extends Fragment {

    private MapFragment fragment;
    private GoogleMap map;
    private static View view;
    private static Marker positionMarker;

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
		      }
			   map.setMapType(GoogleMap.MAP_TYPE_HYBRID);  // set maptype to hybrid
	   		   LatLng positionLatLng = new LatLng(31.95, 35.95);
			   map.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, 15));
			   map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
			   positionMarker = map.addMarker(new MarkerOptions().
			   position(positionLatLng).title("Kid"));
	      } catch (Exception e) {
			   e.printStackTrace();
	      }
        return view;    
    }
}


