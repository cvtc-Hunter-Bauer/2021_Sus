package edu.cvtc.itCapstone.sus;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import androidx.annotation.Nullable;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Arrays;
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

        DataWorker dataWorker = new DataWorker(db);
        dataWorker.insertSubs();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    //method to step through database and add into an array of pieEntries
    public ArrayList<PieEntry> getValues(){

        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<PieEntry> dataV = new ArrayList<>();
        String[] columns = {"cost", "name"};
        Cursor cursor = db.query("subscriptions", columns, null, null, null, null, null);

        for(int i=0; i<cursor.getCount(); i++){

            cursor.moveToNext();
            dataV.add(new PieEntry(cursor.getFloat(0), cursor.getString(1)));
        }

        return dataV;

    }

}

