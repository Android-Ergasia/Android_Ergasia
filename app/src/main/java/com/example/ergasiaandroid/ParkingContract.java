package com.example.ergasiaandroid;

import android.provider.BaseColumns;

public class ParkingContract {
    private ParkingContract() {}

    public static class ParkingEntry implements BaseColumns {
        public static final String TABLE_NAME = "ParkingSessions";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_END_TIME = "end_time";
        public static final String COLUMN_AMOUNT_PAID = "amount_paid";
    }
}
