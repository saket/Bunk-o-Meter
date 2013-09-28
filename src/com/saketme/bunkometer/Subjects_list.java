package com.saketme.bunkometer;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class Subjects_list extends ListActivity {
	
	private ArrayList<String> classes = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		openAndQueryDatabase();
		displayResults();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_subjects_list, menu);
		return true;
	}
	
	void openAndQueryDatabase(){
			DatabaseHandler dh = new DatabaseHandler(getApplicationContext());
			SQLiteDatabase db = dh.getReadableDatabase();
			
			Cursor cursor = db.query("bunk_table", null, null, null, null, null, null);
			
			Toast.makeText(this, "Extracting info...", Toast.LENGTH_SHORT).show();
			
			if(cursor!=null){
				
				if(cursor.moveToFirst()){
					do{
						String subject_name = cursor.getString(cursor.getColumnIndex("Subject"));
						int bunked_classes = (Integer.parseInt(cursor.getString(cursor.getColumnIndex("Bunked_Classes"))));
						int bunk_limit = (Integer.parseInt(cursor.getString(cursor.getColumnIndex("Bunk_Limit"))));
						String current_date = cursor.getString(cursor.getColumnIndex("Bunk_Date")); 
						classes.add(subject_name + ", " + bunked_classes + ", " + bunk_limit + ", " + current_date);
					}while(cursor.moveToNext());
				}
			}
	}
	
	void displayResults(){
		
		setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, classes));
		getListView().setTextFilterEnabled(true);
		
	}
	
	void emptyDatabase(){
		
		DatabaseHandler dh = new DatabaseHandler(this);
		dh.dropTable();
		dh.close();
		
	}
	
	public void resetBrain(){
		
		BrainHandler bh = new BrainHandler (this);
		bh.resetAll();
		
		Toast.makeText(this, "RESET", Toast.LENGTH_SHORT).show();
		
		bh.close();
		
	}

	void fillDatabase(){
		//Get the current date
		String string_current_date = Brain.getCurrentDate();

        //Connect to a databse
		DatabaseHandler dh = new DatabaseHandler(this);

        dh.addSubject("Microprocessor",2, 15, string_current_date);
        dh.addSubject("D.A.A", 2, 10, string_current_date);
        dh.addSubject("C.G.", 2, 13, string_current_date);
        dh.addSubject("DCSC", 2, 13, string_current_date);
        dh.addSubject("Data comm", 2, 13, string_current_date);

		dh.close();
	}

	public boolean onOptionsItemSelected(MenuItem m){
		switch(m.getItemId()){
		case R.id.empty_database:
			emptyDatabase();
			break;
			
		case R.id.fill_database:
			fillDatabase();
			break;
			
		case R.id.reset_share_counter:
            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putInt("runCount", 10);
            editor.putBoolean("showSpreadWord", true);
            editor.putInt("optimalRunCount", 10);
            editor.commit();
			break;


        case android.R.id.home:
            onBackPressed();
            break;
		}

		return true;
		
	}

}
