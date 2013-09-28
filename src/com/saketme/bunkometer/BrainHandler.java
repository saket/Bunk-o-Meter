package com.saketme.bunkometer;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@SuppressLint({"NewApi"})
class BrainHandler extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "brain_for_betaDB";
    private final static int DATABASE_VERSION = 2;
    private final static String TABLE_NAME = "brain_table";

    private final static String KEY_ID = "Id";
    private final static String KEY_CONDITION_ID = "Condition_ID";
    private final static String KEY_STATE = "State";
    private final static String KEY_VISIBILITY = "Visibility";

    public BrainHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE " + TABLE_NAME + "("
                        + KEY_ID + " INTEGER PRIMARY KEY, "
                        + KEY_CONDITION_ID + " TEXT, "
                        + KEY_VISIBILITY + " TEXT, "
                        + KEY_STATE + " TEXT); "

        );

		/* KEEP IN MIND
		 * 
		 * Visibility = NULL means that the card hasn't been either closed or displayed yet.
		 * If it's HIDDEN, then it means that the user has manually closed it.
		 * VISIBLE = currently visible to the user. 
		 */

        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (1, 'tipNoBunkForAWeek', 'NULL', 'NULL');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (2, 'tipCreativeIdeas', 'NULL', 'NULL');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (3, 'messageSemOver', 'NULL', 'NULL');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (4, 'warning2Bunks', 'NULL', 'NULL');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (5, 'warning50Limit', 'NULL', 'NULL');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (6, 'warning90Limit', 'NULL', 'NULL');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (7, 'alert100Limit', 'NULL', 'NULL');");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //drop table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Now re-create it
        onCreate(db);

    }

    public void setState(String condition_ID, String state){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STATE, state);

        db.update(TABLE_NAME, values, KEY_CONDITION_ID + "=?", new String[]{condition_ID});

        db.close();

    }

    public Boolean getVisibility(String condition_ID){

        String visibility = "UNCLEAR";
        Boolean visibility_code = true;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_VISIBILITY}, KEY_CONDITION_ID + "=?", new String[]{condition_ID}, null, null, null);

        try {
            if(cursor.moveToFirst()){
                visibility = cursor.getString(0);
            }
        }catch (IllegalStateException iSE){
            Log.i("BH","Database error");
        }finally {
            cursor.close();
        }

        if(visibility.equals("HIDDEN")){
            //Log.i("BrainHandler", "TRUE");
            visibility_code = false;
        }
        else if(visibility.equals("VISIBLE")){
            //Log.i("BrainHandler", "FALSE");
            visibility_code = true;
        }

        //Log.i("BrainHandler", "Visibility of " + condition_ID + ": " + visibility + ". So Code: " + visibility_code);
        db.close();

        return visibility_code;
    }

    public void setVisibility(String condition_ID, String visibility){

        //Log.e("BH", "Setting: " + condition_ID + " as " + visibility);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VISIBILITY, visibility);

        db.update(TABLE_NAME, values, KEY_CONDITION_ID + "=?", new String[]{condition_ID});

        db.close();
        //Log.i("BrainHandler", "Updated " + condition_ID + " to " + visibility);

    }

    public String getState(String condition_ID){

        String state = "UNCLEAR";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_STATE}, KEY_CONDITION_ID + "=?", new String[]{condition_ID}, null, null, null);

        try {
            if(cursor.moveToFirst()){
                state = cursor.getString(0);
            }
        } finally {
            cursor.close();
        }
        db.close();

        return state;
    }

    public void resetAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Log.i("BrainHandler", "UPDATE " + TABLE_NAME + " SET " + KEY_VISIBILITY + " = NULL");
        ContentValues values = new ContentValues();
        values.put(KEY_VISIBILITY, "NULL");
        db.update(TABLE_NAME, values, null, null);
        db.close();

    }

    // Deleting whole DB
    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

}
