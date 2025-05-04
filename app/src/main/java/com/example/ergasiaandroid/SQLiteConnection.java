package com.example.ergasiaandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteConnection {
    private ParkingDbHelper dbHelper;
    private SQLiteDatabase db;

    public SQLiteConnection(Context context) {
        dbHelper = new ParkingDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insertParkingSession(String userId, String location, String startTime, String endTime, double amountPaid) {
        ContentValues values = new ContentValues();
        values.put(ParkingContract.ParkingEntry.COLUMN_USER_ID, userId);
        values.put(ParkingContract.ParkingEntry.COLUMN_LOCATION, location);
        values.put(ParkingContract.ParkingEntry.COLUMN_START_TIME, startTime);
        values.put(ParkingContract.ParkingEntry.COLUMN_END_TIME, endTime);
        values.put(ParkingContract.ParkingEntry.COLUMN_AMOUNT_PAID, amountPaid);

        return db.insert(ParkingContract.ParkingEntry.TABLE_NAME, null, values);
    }

    public void close() {
        db.close();
    }
    public Cursor getParkingSessionsByUser(String userId) {
        if (userId == null) {
            return db.query(
                    ParkingContract.ParkingEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    ParkingContract.ParkingEntry.COLUMN_START_TIME + " DESC"
            );
        } else {
            return db.query(
                    ParkingContract.ParkingEntry.TABLE_NAME,
                    null,
                    ParkingContract.ParkingEntry.COLUMN_USER_ID + " = ?",
                    new String[]{userId},
                    null,
                    null,
                    ParkingContract.ParkingEntry.COLUMN_START_TIME + " DESC"
            );
        }
    }

    public void updateLatestParkingSession(String userId, String endTime, double amountPaid) {
        ContentValues values = new ContentValues();
        values.put(ParkingContract.ParkingEntry.COLUMN_END_TIME, endTime);
        values.put(ParkingContract.ParkingEntry.COLUMN_AMOUNT_PAID, amountPaid);

        db.update(
                ParkingContract.ParkingEntry.TABLE_NAME,
                values,
                ParkingContract.ParkingEntry.COLUMN_USER_ID + " = ? AND " +
                        ParkingContract.ParkingEntry.COLUMN_END_TIME + " IS NULL",
                new String[]{userId}
        );
    }


}
