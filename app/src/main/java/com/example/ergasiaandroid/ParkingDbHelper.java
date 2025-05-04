package com.example.ergasiaandroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ParkingDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ParkingApp.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ParkingContract.ParkingEntry.TABLE_NAME + " (" +
                    ParkingContract.ParkingEntry._ID + " INTEGER PRIMARY KEY," +
                    ParkingContract.ParkingEntry.COLUMN_USER_ID + " TEXT," +
                    ParkingContract.ParkingEntry.COLUMN_LOCATION + " TEXT," +
                    ParkingContract.ParkingEntry.COLUMN_START_TIME + " TEXT," +
                    ParkingContract.ParkingEntry.COLUMN_END_TIME + " TEXT," +
                    ParkingContract.ParkingEntry.COLUMN_AMOUNT_PAID + " REAL)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ParkingContract.ParkingEntry.TABLE_NAME;

    public ParkingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
