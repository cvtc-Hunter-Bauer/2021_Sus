package edu.cvtc.itCapstone.sus;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.cvtc.itCapstone.sus.DatabaseContract.SubscriptionInfoEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Member variables
    private SubscriptionOpenHelper mDbOpenHelper;
    private RecyclerView mRecyclerSubs;
    private LinearLayoutManager mSubLayoutManger;
    private SubscriptionRecyclerAdapter mSubRecyclerAdapter;
    private String prefName = "spinner_value";
    private Boolean nameDescending = true;
    private Boolean  priceDescending = false;
    private Boolean dateDescending = false;

    // Member constants
    public static final String MY_PREFS = "MyPrefs";
    public static final int LOADER_SUBS = 0;
    public static final String CHANNEL_ID = "channel_payments";


    // Boolean to check if the 'onCreateLoader' method has run
    private boolean mIsCreated = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //   Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        mDbOpenHelper = new SubscriptionOpenHelper(this);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddSub.class));
                //Toast.makeText(MainActivity.this, "Fab", Toast.LENGTH_SHORT).show();
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_subscriptions);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_upcoming_payments:
                    Intent intent = new Intent(MainActivity.this, MainMenu.class);
                    overridePendingTransition(0, 0);
                    MainActivity.this.startActivity(intent);
                    overridePendingTransition(0, 0);
                    break;
                case R.id.action_subscriptions:

                    break;
                case R.id.action_graph:
                    Intent intent2 = new Intent(MainActivity.this, Graph.class);
                    overridePendingTransition(0, 0);
                    MainActivity.this.startActivity(intent2);
                    overridePendingTransition(0, 0);
                    break;
            }
            return true;
        });

        SharedPreferences sp = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if (sp.getBoolean("firstStart", true)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("firstStart", false);
            editor.apply();

            View overlayPageOne = findViewById(R.id.OverlayPageOne);
            overlayPageOne.setVisibility(View.VISIBLE);
        }

        createNotificationChannel();
        initializeDisplayContent();
    }

    private void createNotificationChannel() {
        // Create NotificationChannel, but only on devices with API 26+ because
        // the NotificationChannel class is new and wont work on older devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Upcoming payments";
            String description = "Displays upcoming subscription payments";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system;
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Order items
        switch (item.getItemId()) {
            case R.id.action_name:

                // Check to see if list needs to be descending
                if (nameDescending) {
                    orderSubscriptions(SubscriptionInfoEntry.COLUMN_NAME + " DESC");
                    nameDescending = false;
                } else {
                    orderSubscriptions(SubscriptionInfoEntry.COLUMN_NAME);
                    nameDescending = true;
                }

                priceDescending = false;
                dateDescending = false;

                return true;
            case R.id.action_price:

                // Check to see if list needs to be descending
                if (priceDescending) {
                    orderSubscriptions(SubscriptionInfoEntry.COLUMN_COST + " DESC");
                    priceDescending = false;
                } else {
                    orderSubscriptions(SubscriptionInfoEntry.COLUMN_COST);
                    priceDescending = true;
                }

                nameDescending = false;
                dateDescending = false;

                return true;
            case R.id.action_date:

                // Check to see if list needs to be descending
                if (dateDescending) {
                    orderSubscriptions(SubscriptionInfoEntry.COLUMN_DATE + " DESC");
                    dateDescending = false;
                } else {
                    orderSubscriptions(SubscriptionInfoEntry.COLUMN_DATE);
                    dateDescending = true;
                }

                nameDescending = false;
                priceDescending = false;

                orderSubscriptions(SubscriptionInfoEntry.COLUMN_DATE);
                return true;
            default:
                // Nothing

        }

        return super.onOptionsItemSelected(item);
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


    public void closeTutorial(View view) {
        View overlayPageOne = findViewById(R.id.OverlayPageOne);
        overlayPageOne.setVisibility(View.INVISIBLE);
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
                    String subscriptionsOrderBy = SubscriptionInfoEntry.COLUMN_NAME;

                    // Populate our cursor with the results of the query.
                    return db.query(SubscriptionInfoEntry.TABLE_NAME, subColumns,
                            null, null, null, null,
                            subscriptionsOrderBy);
                }
            };
        }
        return loader;
    }

    // Orders
    public void orderSubscriptions(String subscriptionsOrderBy) {
        // Create new cursor loader
        CursorLoader loader = null;

        Cursor data;

        data = null;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();


        // Create a list of columns we want to return.
        String[] subColumns = {
                DatabaseContract.SubscriptionInfoEntry.COLUMN_NAME,
                DatabaseContract.SubscriptionInfoEntry.COLUMN_DESCRIPTION,
                DatabaseContract.SubscriptionInfoEntry.COLUMN_COST,
                DatabaseContract.SubscriptionInfoEntry.COLUMN_DATE,
                DatabaseContract.SubscriptionInfoEntry._ID};

        // Populate cursor with the results of the query
        data = db.query(SubscriptionInfoEntry.TABLE_NAME, subColumns,
                null, null, null, null,
                subscriptionsOrderBy);

        // Associate the cursor with out RecyclerAdapter
        mSubRecyclerAdapter.changeCursor(data);
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

}
