package com.example.kidstracker;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
	
	private static final String[] COLUMNS = {"id","r_name","r_from", "r_to", "r_lat", "r_lng", "r_days", "r_radius"};
  private static final int DATABASE_VERSION = 1;
  private static final String DATABASE_NAME = "kidstracker";
  private static final String DICTIONARY_TABLE_NAME = "regions";
  private static final String DICTIONARY_TABLE_CREATE =
              "CREATE TABLE " + DICTIONARY_TABLE_NAME + " (" +
              "id INTEGER PRIMARY KEY, r_name TEXT, r_from TEXT, r_to TEXT, r_lat REAL, r_lng REAL, r_days TEXT, r_radius INTEGER);";

  public DBHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
      db.execSQL(DICTIONARY_TABLE_CREATE);
  }

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	public Region getRegion(String key, String value){
    SQLiteDatabase db = this.getReadableDatabase();

    Cursor cursor =
            db.query(DICTIONARY_TABLE_NAME, // a. table
            COLUMNS, // b. column names
            " " + key +" = ?", // c. selections
            new String[] { String.valueOf(value) }, // d. selections args
            null, // e. group by
            null, // f. having
            null, // g. order by
            null); // h. limit
    if (cursor != null && cursor.moveToFirst()) {
        Region region = new Region(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getDouble(5), cursor.getString(6), Integer.parseInt(cursor.getString(7)));
      cursor.close();
      db.close();
      return region;
    } 
    return null;
	}
	
	public List<Region> getAllRegions() {
		List<Region> regions = new ArrayList<Region>();
		SQLiteDatabase db = this.getReadableDatabase();
		
	    Cursor cursor = db.query(DICTIONARY_TABLE_NAME,
	    		COLUMNS, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
          Region region = new Region(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getDouble(5), cursor.getString(6), Integer.parseInt(cursor.getString(7)));
          regions.add(region);
          cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
	
        
        return regions;
	}
	
	public void addRegion(Region region) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
    values.put("r_name", region.name);
    values.put("r_from", region.from);
    values.put("r_to", region.to);
    values.put("r_days", region.days);
    values.put("r_lat", region.lat);
    values.put("r_lng", region.lng);
    values.put("r_radius", region.radius);
    db.insert(DICTIONARY_TABLE_NAME, // table
        null, //nullColumnHack
        values); // key/value -> keys = column names/ values = column values

    db.close(); 
	}
	
	public void reCreate(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS white_listed");
		this.onCreate(db);
	}
	
}
