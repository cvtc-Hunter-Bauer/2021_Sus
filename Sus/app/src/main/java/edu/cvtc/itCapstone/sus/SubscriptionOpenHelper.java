package edu.cvtc.itCapstone.sus;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static edu.cvtc.itCapstone.sus.DatabaseContract.*;

public class SubscriptionOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "itCapstone.db";
    public static final int DATABASE_VERSION = 1;

    public SubscriptionOpenHelper(@Nullable Context context) {
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

    public List<Float> getSubs() {
        List<SubscriptionInfo> subscriptionInfo = new ArrayList<>();
        List<Float> test = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor crs = db.rawQuery("SELECT cost FROM subscriptions",null);
        while(crs.moveToNext()) {

                   test.add(crs.getFloat(0));



        }
        crs.close();

        return test;
    }

    public List<String> getSubsNames() {
        List<SubscriptionInfo> subscriptionInfo = new ArrayList<>();
        List<String> test = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor crs = db.rawQuery("SELECT name FROM subscriptions",null);
        while(crs.moveToNext()) {

            test.add(crs.getString(0));



        }
        crs.close();

        return test;
    }
}

