package edu.cvtc.itCapstone.sus;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class DatabaseContract {

    private DatabaseContract(){}

    public static final class SubscriptionInfoEntry implements BaseColumns {

        public static final String TABLE_NAME = "subscriptions";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_COST = "cost";
        public static final String COLUMN_DATE = "date";

        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String SQL_CREATE_INDEX1 = "CREATE INDEX " + INDEX1 + " ON " +
                                                        TABLE_NAME + "(" + COLUMN_NAME + ")";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY, "
                                                        + COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_DESCRIPTION + " TEXT"
                                                        + COLUMN_COST + " INTEGER, " + COLUMN_DATE + " DATE";
    }
}
