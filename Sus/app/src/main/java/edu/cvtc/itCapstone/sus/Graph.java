package edu.cvtc.itCapstone.sus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static edu.cvtc.itCapstone.sus.MainActivity.LOADER_SUBS;

public class Graph extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{





    private static final String TAG = "GraphActivity";
    public boolean test;

    private SubscriptionOpenHelper mDbOpenHelper;
    private RecyclerView mRecyclerSubs;
    private LinearLayoutManager mSubLayoutManger;
    private SubscriptionRecyclerAdapter mSubRecyclerAdapter;

    // Boolean to check if the 'onCreateLoader' method has run
    public static final int LOADER_SUBS = 0;
    private boolean mIsCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SubscriptionOpenHelper subscriptionOpenHelper = new SubscriptionOpenHelper(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        DataManager.loadFromDatabase(subscriptionOpenHelper);
        mDbOpenHelper = new SubscriptionOpenHelper(this);
        // creating pie chart object
        PieChart pieChart = findViewById(R.id.chart);

        // settings for pie chart and description
        pieChart.setUsePercentValues(false);

        pieChart.setCenterText("This your monthly spending");
        pieChart.animate();

        // using method that steps through database inserting into a PieEntry object
        PieDataSet pieDate = new PieDataSet(mDbOpenHelper.getValues(), "Subscriptions");
        PieData pie = new PieData(pieDate);
        pie.setValueTextSize(11f);

        // setting format for the graph of 2 decimal places
        pie.setValueFormatter(new DefaultValueFormatter(2));
        pieChart.setData(pie);
        // setting piechart color
        pieDate.setColors(ColorTemplate.COLORFUL_COLORS);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_graph);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
           switch (item.getItemId()) {
                case R.id.action_upcoming_payments:
                    Intent intent = new Intent(Graph.this, MainMenu.class);
                    overridePendingTransition(0, 0);
                    Graph.this.startActivity(intent);
                    overridePendingTransition(0, 0);
                    break;

                case R.id.action_subscriptions:
                    Intent intent2 = new Intent(Graph.this, MainActivity.class);
                    overridePendingTransition(0, 0);
                    Graph.this.startActivity(intent2);
                    overridePendingTransition(0, 0);
                    break;
                case R.id.action_graph:

                    break;                }
            return true;

        });


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
                    String subscriptionsOrderBy = DatabaseContract.SubscriptionInfoEntry.COLUMN_NAME;

                    // Populate our cursor with the results of the query.
                    return db.query(DatabaseContract.SubscriptionInfoEntry.TABLE_NAME, subColumns,
                            null, null, null, null,
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



}
