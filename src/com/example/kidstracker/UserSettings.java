package com.example.kidstracker;

import com.google.android.maps.MapActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
 
public class UserSettings extends PreferenceFragment {
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.layout.settings);
        setupPreferences();

    }
    
    
	private void setupPreferences() {

		CheckBoxPreference deactivate = (CheckBoxPreference) (CheckBoxPreference) findPreference("deactivate");

		deactivate
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						if ((Boolean) newValue) {
							stopService();
						} else {
							startService();
						}
						return true;
					}
				});

		final EditTextPreference edit = (EditTextPreference) (EditTextPreference) findPreference("kidname");
		String kidName = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("kidname", "Kid");
		edit.setSummary(kidName);
		edit.setText(kidName);
		edit.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						edit.setSummary((String) newValue);
						return true;
					}
				});

	}
	
	private Boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager) getActivity()
				.getSystemService(getActivity().ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (MapService.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	
	private void startService() {
		Intent serviceIntent = new Intent(getActivity(), MapService.class);
		if (isServiceRunning()) {
			getActivity().startService(serviceIntent);
		}
		getActivity().startService(serviceIntent);
	}

	private void stopService() {
		Intent serviceIntent = new Intent(getActivity(), MapService.class);
		getActivity().stopService(serviceIntent);
	}

}