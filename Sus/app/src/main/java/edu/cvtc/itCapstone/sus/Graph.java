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
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

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
        Description description = new Description();
        description.setText("This your monthly spending");
        pieChart.setDescription(description);


        List<PieEntry> value = new ArrayList<>();
        //List<SubscriptionInfo> list = new ArrayList<>();
        List<Float> list =  mDbOpenHelper.getSubs();

        for(int i = 0; i < list.size(); i++) {

                 test = value.add(new PieEntry(Float.parseFloat(String.valueOf(mDbOpenHelper.getSubs().get(i))), mDbOpenHelper.getSubsNames().get(i) ));


        }
        PieDataSet pieDate = new PieDataSet(value, "test");
        PieData pie = new PieData(pieDate);
        pie.setValueTextSize(11f);
        pieChart.setData(pie);
        // setting piechart color
        pieDate.setColors(ColorTemplate.COLORFUL_COLORS);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_graph);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_upcoming_payments:
                    Intent intent = new Intent(Graph.this, MainMenu.class);
                    Graph.this.startActivity(intent);
                    break;

                case R.id.action_subscriptions:
                    Intent intent2 = new Intent(Graph.this, MainActivity.class);
                    Graph.this.startActivity(intent2);
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
