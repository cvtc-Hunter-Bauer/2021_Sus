package edu.cvtc.itCapstone.sus;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;

import static edu.cvtc.itCapstone.sus.DatabaseContract.*;

public class DataWorker {

    private final SQLiteDatabase  mDb;

    public DataWorker(SQLiteDatabase db){
        mDb = db;
    }

    // for inserting into list of subscriptions.
    private void insertSub(String name, String description, double cost, String date) {
        ContentValues contentValues = new ContentValues();

        // need this format for date values
        // no longer used
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
        contentValues.put(SubscriptionInfoEntry.COLUMN_NAME, name);
        contentValues.put(SubscriptionInfoEntry.COLUMN_DESCRIPTION, description);
        contentValues.put(SubscriptionInfoEntry.COLUMN_COST, cost);
        contentValues.put(SubscriptionInfoEntry.COLUMN_DATE, date);

        long newRow = mDb.insert(SubscriptionInfoEntry.TABLE_NAME, null, contentValues);

    }

    public void insertSubs() {
        // Sample values for list
        insertSub("test", "subscription test for the list", 15.00, "wed, Mar 3, '21");
    }
}
