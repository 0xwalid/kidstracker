package com.example.kidstracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class HistoryFragment extends Fragment {
    
	TableLayout tl;
	char days;
    public HistoryFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.history, container, false);
        tl = (TableLayout) rootView.findViewById(R.id.table);
        
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                getActivity());
//        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select the period:");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("1 Day");
        arrayAdapter.add("2 Days");
        arrayAdapter.add("3 Days");
        arrayAdapter.add("4 Days");
        arrayAdapter.add("5 Days");
        arrayAdapter.add("6 Days");
        arrayAdapter.add("7 Days");
        builderSingle.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        days = strName.charAt(0);
                    	
                    	new serverConnection().execute();
                    }
                });
        builderSingle.show();

        return rootView;
    }
    
    
    public void addRow(String Date, String Region, int color) {
    	/* Create a new row to be added. */
    	TableRow tr = new TableRow(getActivity());
    	tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    	/* Create a Button to be the row-content. */
    	TextView b = new TextView(getActivity());
    	b.setText(Date);
    	b.setPadding(75, 7, 0, 7);
    	
    	b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    	/* Add Button to row. */
    	tr.addView(b);
    	TextView c = new TextView(getActivity());
    	c.setText(Region);
    	c.setPadding(325, 7, 0, 7);
    	c.setTextColor(color);
    	c.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    	/* Add Button to row. */
    	tr.addView(c);
    	/* Add row to TableLayout. */
    	//tr.setBackgroundResource(R.drawable.sf_gradient_03);
    	tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }
    
    
    
    private class serverConnection extends AsyncTask<String, String, JSONArray> {


        private ProgressDialog pDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Contacting Server");

            pDialog.setMessage("Retrieving History...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
        	String serverUrl = getResources().getString(R.string.host);
            JSONArray json = null;
            String user = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("user", "");
            json  = new JSONParser().getReqArr(serverUrl+ "/getHistory/" + user + "/"+days);
            return json;
        }

        @Override
        protected void onPostExecute(JSONArray json) {
//            try {
               if (json != null) {

                    Log.i("kt", json.toString());
                    for(int i = 0; i < json.length(); i++){
                    	 try {
 							String captureTime = json.getJSONObject(i).getString("capture_date");
 							captureTime = captureTime.substring(0, captureTime.length() - 10);
 							Log.i("kt", captureTime);
						    
							String region = json.getJSONObject(i).getString("region_name");
							String zone = json.getJSONObject(i).getString("zone_type");
							int color = 0;
							if (zone.equals("green")) {
								color = Color.GREEN;
							} else if (zone.equals("gray")) {
								color = Color.GRAY;
							} else if (zone.equals("red")) {
								color = Color.RED;
								region = "OUT";
							}
							addRow(captureTime, region, color);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }
                    
                    pDialog.dismiss();
                    
                    
                    
                } else {
                	pDialog.dismiss();
                }
       }
    }     
}