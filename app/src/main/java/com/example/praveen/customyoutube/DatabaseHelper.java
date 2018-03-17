package com.example.praveen.customyoutube;

/**
 * Created by Praveen on 02/04/2018.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//Creating database and table with all columns
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Package1.db";
    public static final String TABLE_NAME = "Custom_Youtube";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "VIDEO_ID";
    public static final String COL_3 = "DESCRIPTION";
    public static final String COL_4 = "PLAYLIST_NAME";
//    public static final String COL_5 = "CURR_LOCATION";
//    public static final String COL_6 = "CURR_LOC_TIME";
//    public static final String COL_7 = "WEIGHT";
//    public static final String COL_8 = "TOT_PACKETS";

//    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    Date date = null;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,VIDEO_ID TEXT,DESCRIPTION TEXT,PLAYLIST_NAME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //onCreate(db);
    }

    //inserting data from the Send packet page
    public boolean insertData(String videoID,String description,String playlistName) {
//        date = new Date();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,videoID);
        contentValues.put(COL_3,description);
        contentValues.put(COL_4,playlistName);
//        contentValues.put(COL_5,source);
//        contentValues.put(COL_7,weight);
//        contentValues.put(COL_8,totPackets);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    //gets table data for use in Track package page
    public Cursor getAllData(String playlistName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" where PLAYLIST_NAME=\""+playlistName+"\"",null);
        Log.d("res", "res is: "+res.getCount());
        return res;
    }

    //gets ID from the recently inserted data
    public Cursor getVideoID(String playlistName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select VIDEO_ID from "+TABLE_NAME+" where PLAYLIST_NAME=\""+playlistName+"\"",null);
        return res;
    }

    public void deleteLinkfromList(String videoID)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NAME+" WHERE VIDEO_ID='"+videoID+"'");
       // db.close();
    }

    //updating the database from Send package page and runs in the background
//    public boolean updateData(String ID,String curr_loc,String curr_loc_time) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_5,curr_loc);
//        contentValues.put(COL_6,curr_loc_time);
//        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { ID });
//        return true;
//    }

}