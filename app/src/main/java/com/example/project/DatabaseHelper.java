package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;

public class DatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase database;

    // Table Name
    public static final String TABLE_NAME = "RDV";
    // Table columns
    public static final String _ID = "_id";
    public static final String DESCRIPTION = "description";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String CONTACT = "contact";
    public static final String ADDRESS = "address";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String DONE = "isdone";
    // Database Information
    static final String DB_NAME = "RDV.DB";
    // database version
    static final int DB_VERSION = 1;
    // Creating table query
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DESCRIPTION + " TEXT NOT NULL, " + DATE +
            " TEXT NOT NULL, " + TIME + " TEXT NOT NULL, " + CONTACT + " TEXT NOT NULL, " + ADDRESS + " TEXT NOT NULL, " +
            PHONE_NUMBER +" TEXT NOT NULL, " + DONE + " INTEGER);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void open() throws SQLException {
        database = this.getWritableDatabase();
    }
    public void close() {
        database.close();
    }

    public void add(RDV rdv){
        ContentValues contentValues= new ContentValues();
        contentValues.put(DESCRIPTION,rdv.getDescription());
        contentValues.put(DATE,rdv.getDate());
        contentValues.put(TIME, rdv.getTime());
        contentValues.put(CONTACT,rdv.getContact());
        contentValues.put(ADDRESS, rdv.getAddress());
        contentValues.put(PHONE_NUMBER, rdv.getPhoneNumber());
        contentValues.put(DONE, rdv.getIsDone());
        database.insert(TABLE_NAME,null,contentValues);
    }

    public Cursor getAllRDV(){
        String[] projection = {_ID,DESCRIPTION,DATE,TIME,CONTACT,ADDRESS,PHONE_NUMBER,DONE};
        Cursor cursor = database.query(TABLE_NAME,projection,null,null,null,null,null,null);
        return cursor;
    }

    public Cursor getAllDate(){
        String[] projection = {DATE};
        Cursor cursor = database.query(TABLE_NAME,projection,null,null,null,null,null,null);
        return cursor;
    }

    public int update(RDV rdv) {
        int _id= rdv.getIdentifier();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DESCRIPTION, rdv.getDescription());
        contentValues.put(DATE, rdv.getDate());
        contentValues.put(TIME,rdv.getTime());
        contentValues.put(CONTACT, rdv.getContact());
        contentValues.put(ADDRESS, rdv.getAddress());
        contentValues.put(PHONE_NUMBER, rdv.getPhoneNumber());
        int count = database.update(TABLE_NAME, contentValues, this._ID + " = " + _id, null);
        return count;
    }

    public void delete(long _id)
    {
        database.delete(TABLE_NAME, _ID + "=" + _id, null);
    }

    public String getShareTextFromDataBase(long id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] { _ID,
                        DESCRIPTION, DATE, TIME, CONTACT, ADDRESS, PHONE_NUMBER, DONE }, _ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();


        String rv = "RDV " + cursor.getString(1)  + "\n"
                + "Date : " + cursor.getString(2) + "\n"
                + "Time : " + cursor.getString(3) + "\n"
                + "Address : " + cursor.getString(5) + "\n";
        return rv;
    }
}
