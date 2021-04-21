package edu.cvtc.itCapstone.sus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import edu.cvtc.itCapstone.sus.DatabaseContract.SubscriptionInfoEntry;

public class AddSub extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private int mSubId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub);
    }



    private void saveSubscription(String name, String description, int cost, String date) {
        // Create selection criteria
        final String selection = SubscriptionInfoEntry._ID + " = ?";
        final String[] selectionArgs = {Integer.toString(mSubId)};
        // Use a ContentValues object to put our information into.
        final ContentValues values = new ContentValues();
        values.put(SubscriptionInfoEntry.COLUMN_NAME, name);
        values.put(SubscriptionInfoEntry.COLUMN_DESCRIPTION, description);
        values.put(SubscriptionInfoEntry.COLUMN_COST, cost);
        values.put(SubscriptionInfoEntry.COLUMN_DATE, date);
        AsyncTaskLoader<String> task = new AsyncTaskLoader<String>(this) {
            @Nullable
            @Override
            public String loadInBackground() {
                // Get connection to the database. Use the writable
                // method since we are changing the data.
                SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
                // Call the update method
                db.update(CoursesInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                return null;
            }
        };
        task.loadInBackground();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}