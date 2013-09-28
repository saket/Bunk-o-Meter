package com.saketme.bunkometer;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressLint({"NewApi"})
class DatabaseHandler extends SQLiteOpenHelper {

    //DB and Table details
    private final static String DATABASE_NAME = "betaDB";
    private final static int DATABASE_VERSION = 3;
    private final static String TABLE_NAME = "bunk_table";

    //Attribute details
    private final static String KEY_ID = "id";
    private final static String KEY_SUBJECT = "Subject";
    private final static String KEY_BUNKED_CLASSES = "Bunked_Classes";
    private final static String KEY_LIMIT = "Bunk_Limit";
    private final static String KEY_DATE = "Bunk_Date";
    private final static String KEY_PERCENT = "Bunk_Percentage";

    private final Context context;

    /*
    SQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    Create a helper object to create, open, and/or manage a database.
    */
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Creating a table
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME +
                        " ("
                        + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + KEY_SUBJECT + " TEXT, "

                        + KEY_BUNKED_CLASSES + " TEXT,"
                        + KEY_LIMIT + " INTEGER,"
                        + KEY_DATE + " TEXT,"
                        + KEY_PERCENT + " REAL);"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //drop table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Now re-create it
        onCreate(db);
    }

    //Adding a new subject
    public void addSubject(String subject_name, int bunk_count, int bunk_limit, String string_current_date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        double percentage = (double)bunk_count/(double)bunk_limit*100;

        values.put(KEY_SUBJECT, subject_name); //Subject Name
        values.put(KEY_BUNKED_CLASSES, bunk_count); //Bunked Classes
        values.put(KEY_LIMIT, bunk_limit); //Limit
        values.put(KEY_DATE, string_current_date); //Date of bunking
        values.put(KEY_PERCENT, percentage); //Bunked Percentage

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    //Update a subject
    public void updateSubject(String subject_name, int bunk_count, int bunk_limit, String old_subject_name, String bunk_date){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        double percentage = (double)bunk_count/(double)bunk_limit*100;

        values.put(KEY_SUBJECT, subject_name); //Subject Name
        values.put(KEY_BUNKED_CLASSES, bunk_count); //Bunked Classes
        values.put(KEY_LIMIT, bunk_limit); //Limit
        values.put(KEY_PERCENT, percentage); //Bunked Percentage

        if(bunk_date != null)
            values.put(KEY_DATE, bunk_date); //Last Bunked Date

        db.update(TABLE_NAME, values, KEY_SUBJECT + "=?", new String[]{old_subject_name});

        db.close();

    }

    //Delete a subject
    public void deleteSubject(String subjectToDelete){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, KEY_SUBJECT + "=?", new String[]{subjectToDelete});
        db.close();

    }

    // Deleting whole DB
    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    //Get the Bunk Limit
    public int getLimit(String subject_name){
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_SUBJECT + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        int limit=0;

        Cursor cursor = db.rawQuery(selectQuery, new String[]{subject_name});

        try {
            if(cursor.moveToFirst()){
                limit = Integer.parseInt(cursor.getString(3));
            }
        } finally {
            cursor.close();
        }

        db.close();
        return limit;

    }

    //Find if a (bunked percentage) condition is true or not
    public List<String> checkBunkPercentageOfAllSubs(int percent_cond){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_SUBJECT}, KEY_PERCENT + ">=?", new String[]{String.valueOf(percent_cond)}, null, null, null);

        List<String> found_subjects = new ArrayList<String>();

        try{
            if(cursor.moveToFirst()){
                do{
                    found_subjects.add(cursor.getString(0));
                }while(cursor.moveToNext());
            }
        }finally{
            cursor.close();
        }
        db.close();
        return found_subjects;

    }

    //Get subjects count
    public int countSubjects(){

        int count;

        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor2 = db.rawQuery(countQuery, null);

        count = cursor2.getCount();

        cursor2.close();

        db.close();
        // return count
        return count;
    }

    //Get the count of subjects having the same date
    public Boolean countSubjectsSameDate(String week_old_date) throws ParseException{

        //Log.i("DatabaseHandler", "Searching for date in the DB: " + date);

        String query = "SELECT " + KEY_DATE + " FROM " + TABLE_NAME + " GROUP BY " + KEY_DATE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            if(cursor.getCount()==1){
                if(isOldDate(week_old_date, cursor.getString(0))){
                    //Log.i("DatabaseHandler", "Equal: " + cursor.getString(0) + " and " + date);
                    //TODO: Remove this If anything goes wrong:
                    db.close();
                    return true;
                }
            }
        }
        //TODO: Remove this If anything goes wrong:
        db.close();
        return false;
    }

    private static Boolean isOldDate(String week_old_date, String queried_date) throws ParseException {
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date weekOldDate = outputFormat.parse(week_old_date);
        Date queriedDate = outputFormat.parse(queried_date);

        return queriedDate.getTime() <= weekOldDate.getTime();
    }

    public String lastDateBeforeAddingNewSubject(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_DATE + " FROM " + TABLE_NAME + " GROUP BY " + KEY_DATE, null);

        //Log.i("DBH", "Cursor count is: " + cursor.getCount());

        String date = null;

        if(cursor.moveToFirst()){
            do{
                date = cursor.getString(0);
            }while(cursor.moveToNext());
        }

        //TODO: Remove this If anything goes wrong:
        db.close();
        return date;
    }

    //Get all the contacts
    public List<Classes> getAllContacts(){

        List<Classes> contactList = new ArrayList<Classes>();

        String select_query = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(select_query, null);

        // looping through all rows and adding to list
        try {
            if(cursor.moveToFirst()){
                do{
                    Classes subject = new Classes();
                    subject.setId(Integer.parseInt(cursor.getString(0)));
                    subject.setSubject(cursor.getString(1));
                    subject.setBunkedClasses(Integer.parseInt(cursor.getString(2)));
                    subject.setLimit(Integer.parseInt(cursor.getString(3)));
                    subject.setLastBunkDate(cursor.getString(4));

                    //Adding contact to list
                    contactList.add(subject);
                }while(cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        db.close();
        return contactList;

    }

}
