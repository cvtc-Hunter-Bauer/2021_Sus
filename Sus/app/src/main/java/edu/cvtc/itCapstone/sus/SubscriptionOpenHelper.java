package edu.cvtc.itCapstone.sus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import androidx.annotation.Nullable;

import static edu.cvtc.itCapstone.sus.DatabaseContract.*;

public class SubscriptionOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "itCapstone.db";
    public static final int DATABASE_VERSION = 1;
    public SubscriptionOpenHelper (@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SubscriptionInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(SubscriptionInfoEntry.SQL_CREATE_INDEX1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
