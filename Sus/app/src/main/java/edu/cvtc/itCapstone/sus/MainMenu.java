package edu.cvtc.itCapstone.sus;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;

import static edu.cvtc.itCapstone.sus.MainActivity.LOADER_SUBS;

public class MainMenu extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    // Member variables
    private SubscriptionOpenHelper mDbOpenHelper;
    private RecyclerView mRecyclerSubs;
    private LinearLayoutManager mSubLayoutManger;
    private SubscriptionRecyclerAdapter mSubRecyclerAdapter;

    // Boolean to check if the 'onCreateLoader' method has run
    private boolean mIsCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mDbOpenHelper = new SubscriptionOpenHelper(this);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_upcoming_payments);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_upcoming_payments:

                    break;
                case R.id.action_subscriptions:
                    Intent intent = new Intent(MainMenu.this, MainActivity.class);
                    overridePendingTransition(0, 0);
                    MainMenu.this.startActivity(intent);
                    overridePendingTransition(0, 0);
                    break;
                case R.id.action_graph:
                    Intent intent2 = new Intent(MainMenu.this, Graph.class);
                    overridePendingTransition(0, 0);
                    MainMenu.this.startActivity(intent2);
                    overridePendingTransition(0, 0);
                    break;
            }
            return true;
        });

        initializeDisplayContent();
        displaySpending();
    }

    private void initializeDisplayContent() {

        // Retrieve the information from your database
        DataManager.loadFromDatabase(mDbOpenHelper);

        // Set a reference to your list of items layout
        mRecyclerSubs = (RecyclerView) findViewById(R.id.list_subs);
        mSubLayoutManger = new LinearLayoutManager(this);

        // You don't have a cursor yet, so pass null.
        mSubRecyclerAdapter = new SubscriptionRecyclerAdapter(this, null);

        // Display the courses
        displaySubscriptions();
    }

    private void displaySubscriptions() {
        mRecyclerSubs.setLayoutManager(mSubLayoutManger);
        mRecyclerSubs.setAdapter(mSubRecyclerAdapter);
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();

        // Use restartLoader instead of initLoader to make sure
        // we re-query the database each time the activity is
        // loaded in the app.
        LoaderManager.getInstance(this).restartLoader(LOADER_SUBS, null, this);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Create new cursor loader
        CursorLoader loader = null;

        if (id == LOADER_SUBS) {
            loader = new CursorLoader(this) {

                @Override
                public Cursor loadInBackground() {
                    mIsCreated = true;

                    // Create our selection
                    final String selection ="DATE(" + DatabaseContract.SubscriptionInfoEntry.COLUMN_DATE + ") <= DATE(?)";

                    // Create a Calendar object on today's date, add 4 days to the calendar.
                    // This will be the upper bounds that table will pull from
                    // Any subscription past this will not be shown in upcoming payments
                    Calendar calendarMaxDate = Calendar.getInstance();
                    calendarMaxDate.add(Calendar.DATE, 4);

                    // Next we need to turn the date from our calendar into something
                    // that SQL can read; The format is YYYY-MM-DD.
                    String dayString;
                    String monthString;
                    String yearString = String.valueOf(calendarMaxDate.get(Calendar.YEAR));

                    // Convert day to a string
                    // add a leading zero if less then 10
                    int day = calendarMaxDate.get(Calendar.DAY_OF_MONTH);
                    if (day < 10) {
                        dayString = "0" + day;
                    } else {
                        dayString = String.valueOf(day);
                    }

                    // Convert month to a string
                    // add a leading zero if less then 10
                    int month = calendarMaxDate.get(Calendar.MONTH) + 1;
                    if (month < 10) {
                        monthString = "0" + month;
                    } else {
                        monthString = String.valueOf(month);
                    }

                    // Add all the parts together in a date String
                    String dateString = yearString + "-" + monthString + "-" + dayString;

                    // Finally create the argument for our selection
                    final String[] selectionArgs = {dateString};

                    // Open our database in read mode.
                    SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

                    // Create a list of columns we want to return.
                    String[] subColumns = {
                            DatabaseContract.SubscriptionInfoEntry.COLUMN_NAME,
                            DatabaseContract.SubscriptionInfoEntry.COLUMN_DESCRIPTION,
                            DatabaseContract.SubscriptionInfoEntry.COLUMN_COST,
                            DatabaseContract.SubscriptionInfoEntry.COLUMN_DATE,
                            DatabaseContract.SubscriptionInfoEntry._ID};

                    // Create an order by field for sorting purposes.
                    String subscriptionsOrderBy = DatabaseContract.SubscriptionInfoEntry.COLUMN_NAME;

                    // Populate our cursor with the results of the query.
                    return db.query(DatabaseContract.SubscriptionInfoEntry.TABLE_NAME, subColumns,
                            selection, selectionArgs, null, null,
                            subscriptionsOrderBy);
                }
            };
        }
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        if (loader.getId() == LOADER_SUBS && mIsCreated) {

            // Associate the cursor with our RecyclerAdapter
            mSubRecyclerAdapter.changeCursor(data);
            mIsCreated = false;
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (loader.getId() == LOADER_SUBS) {

            // Associate the cursor with our RecyclerAdapter
            mSubRecyclerAdapter.changeCursor(null);
        }
    }

    public void displaySpending() {
        int spending = 0;

        // Save a call to each one of our TextViews
        TextView monthlyText = findViewById(R.id.monthly_dollar_text);
        TextView sixMonthlyText = findViewById(R.id.half_year_dollar_text);
        TextView yearlyText = findViewById(R.id.yearly_dollar_text);

        // Retrieve the information from your database
        DataManager.loadFromDatabase(mDbOpenHelper);

        // Open our database in read mode.
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        // Create a list of columns we want to return.
        String[] spendingColumn = {
                DatabaseContract.SubscriptionInfoEntry.COLUMN_COST};

        // Populate our cursor with the results of the query.
        Cursor cursor = db.query(DatabaseContract.SubscriptionInfoEntry.TABLE_NAME, spendingColumn,
                null, null, null, null,
                null);

        if(cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                spending += cursor.getInt(0);
                cursor.moveToNext();
            }
        }
        cursor.close();

        // update each text view with the proper amount of spending
        monthlyText.setText("$ " + spending);
        sixMonthlyText.setText("$ " + (spending * 6));
        yearlyText.setText("$ " + (spending * 12));
    }
}