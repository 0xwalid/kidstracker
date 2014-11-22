package com.example.kidstracker;

import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.app.Activity;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.Build;
import android.preference.PreferenceManager;

public class MainActivity extends Activity {
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Fragment mapFragment = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.side_menu);
//		Boolean logged_in = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("logged_in", false);
//		if (logged_in == false) {
//			Intent login = new Intent(this, LoginActivity.class);
//			startActivity(login);
//			finish();
//		}
		
//		if (savedInstanceState == null) {
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
 
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                getActionBar().setTitle("Kids Tracker");
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }
 
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("Menu");
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            boolean value = extras.getBoolean("map");
            if (value) {
            	displayView(1);
            } else {
            	displayView(0);
            }
        } else {
        	displayView(0);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        
        Intent intent = new Intent(MainActivity.this,
                MapService.class);
        startService(intent);
        PreferenceManager.setDefaultValues(this, R.layout.settings, false);
	}
	
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
		        long id) {
		    // display view for selected nav drawer item
		    displayView(position);
		}
    }
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        boolean map = false;
        boolean reg = false;
        switch (position) {
        case 0:
            fragment = new HomeFragment();
            break;
        case 1:
        	fragment =  new MapsFragment();
        	reg = false;
        	map = true;
            break;
        case 2:
            fragment = new MapsFragment();
            reg = true;
            break;
        case 3:
            fragment = new HistoryFragment();
            break;
        case 4:
            fragment = new UserSettings();
            break;
        case 5:
			Intent login = new Intent(this, LoginActivity.class);
			startActivity(login);
			if (isServiceRunning()) {
				stopService();
			}
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("logged_in", false).commit();
			finish(); 
			return;
 
        default:
            break;
        }
        Bundle b = new Bundle();
        b.putBoolean("regions", reg);
        fragment.setArguments(b);
        
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();
 
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mPlanetTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("kt", "Error in creating fragment");
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
        case R.id.action_settings:
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
 
    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
  
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
 
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
	public Boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager) this
				.getSystemService(this.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (MapService.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	
	public void startService() {
		Intent serviceIntent = new Intent(this, MapService.class);
		if (isServiceRunning()) {
			this.startService(serviceIntent);
		}
		this.startService(serviceIntent);
	}

	public void stopService() {
		Intent serviceIntent = new Intent(this, MapService.class);
		this.stopService(serviceIntent);
	}

}
