package edu.cvtc.itCapstone.sus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import edu.cvtc.itCapstone.sus.DatabaseContract.SubscriptionInfoEntry;

public class AddSub extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    public static final String SUBSCRIPTION_ID = "edu.cvtc.itCapstone.sus.SUBSCRIPTION_ID";
    public static final String ORIGINAL_SUBSCRIPTION_NAME = "edu.cvtc.itCapstone.sus.ORIGINAL_SUBSCRIPTION_NAME";
    public static final String  ORIGINAL_SUBSCRIPTION_DESCRIPTION = "edu.cvtc.itCapstone.sus.ORIGINAL_SUBSCRIPTION_DESCRIPTION";
    public static final String  ORIGINAL_SUBSCRIPTION_COST = "edu.cvtc.itCapstone.sus.ORIGINAL_SUBSCRIPTION_COST";
    public static final String  ORIGINAL_SUBSCRIPTION_DATE = "edu.cvtc.itCapstone.sus.ORIGINAL_SUBSCRIPTION_DATE";

    public static final int ID_NOT_SET = -1;
    public static final int LOADER_SUB = 0;
    private int mSubId;
    private SubscriptionOpenHelper mDbOpenHelper;

    private SubscriptionInfo mSub = new SubscriptionInfo(0, "", "", 0.0, "");
    private int mSubNamePosition;
    private int mSubDescriptionPosition;
    private int mSubCostPosition;
    private int mSubDatePosition;

    private String mOriginalSubName;
    private String mOriginalSubDesc;
    private double mOriginalSubCost;
    private String mOriginalSubDate;

    private EditText mName;
    private EditText mDescription;
    private EditText mCost;
    private EditText mDate;

    private Button mButton;
    private Cursor mCursor;
    private boolean mNewSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub);

        mDbOpenHelper = new SubscriptionOpenHelper(this);
        mButton = findViewById(R.id.button_save);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readSateValues();
            }
        });



        if (savedInstanceState == null){

            saveOriginalSub();
        } else {
            restoreOriginalSub(savedInstanceState);
        }

        mName = findViewById(R.id.text_name);
        mDescription = findViewById(R.id.text_description);
        mCost = findViewById(R.id.text_cost);
        mDate = findViewById(R.id.text_date);

        if(!mNewSub) {
            LoaderManager.getInstance(this).initLoader(LOADER_SUB, null, this);
        }
    }


    private void saveSub(){
        // getting the input from the edit texts
        String subName = mName.getText().toString();
        String subDescription = mDescription.getText().toString();
        //TODO might need to parse to double
        double subCost = Double.parseDouble(mCost.getText().toString());
        String subDate = mDate.getText().toString();
        // method to save to the db.
        saveSubscription(subName, subDescription, subCost, subDate);
    }
    private void saveSubscription(String name, String description, double cost, String date) {
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
                db.update(SubscriptionInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                return null;
            }
        };
        task.loadInBackground();
    }

    private void showSub(){

        // gets the values and inserts them into the layout based on sub that is clicked
        String subName = mCursor.getString(mSubNamePosition);
        String subDescription = mCursor.getString(mSubDescriptionPosition);
        String subCost = mCursor.getString(mSubCostPosition);
        String subDate = mCursor.getString(mSubDatePosition);


        // fills the edit texts in the layout
        mName.setText(subName);
        mDescription.setText(subDescription);
        mCost.setText(subCost);
        mDate.setText(subDate);



    }

    private void restoreOriginalSub(Bundle savedInstanceState) {

        // gets the original values
        mOriginalSubName = savedInstanceState.getString(ORIGINAL_SUBSCRIPTION_NAME);
        mOriginalSubDesc = savedInstanceState.getString(ORIGINAL_SUBSCRIPTION_DESCRIPTION);

        // possibly parsing to double
        mOriginalSubCost = Double.parseDouble(savedInstanceState.getString(ORIGINAL_SUBSCRIPTION_COST));
        mOriginalSubDate = savedInstanceState.getString(ORIGINAL_SUBSCRIPTION_DATE);

    }

    private void saveOriginalSub() {

        if(!mNewSub) {
            mOriginalSubName = mSub.getName();
            mOriginalSubDesc = mSub.getDescription();
            mOriginalSubCost = mSub.getCost();
            mOriginalSubDate = mSub.getDate();

        }
    }

    private void readSateValues() {
        Intent intent = getIntent();

        mSubId = intent.getIntExtra(SUBSCRIPTION_ID, ID_NOT_SET);

        mNewSub = mSubId == ID_NOT_SET;

        if(mNewSub) {

            createNewSub();
        }
    }

    private void createNewSub() {
        mName = findViewById(R.id.text_name);
        mDescription = findViewById(R.id.text_description);
        mCost = findViewById(R.id.text_cost);
        mDate = findViewById(R.id.text_date);
        ContentValues contentValues = new ContentValues();

        contentValues.put(SubscriptionInfoEntry.COLUMN_NAME, mName.getText().toString());
        contentValues.put(SubscriptionInfoEntry.COLUMN_DESCRIPTION, mDescription.getText().toString());
        contentValues.put(SubscriptionInfoEntry.COLUMN_COST, Double.parseDouble(mCost.getText().toString()));
        contentValues.put(SubscriptionInfoEntry.COLUMN_DATE, mDate.getText().toString());

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        mSubId = (int) db.insert(SubscriptionInfoEntry.TABLE_NAME, null, contentValues);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        CursorLoader loader = null;

        if (id == LOADER_SUB){
            loader = createLoaderSub();
        }
        return loader;
    }

    private CursorLoader createLoaderSub() {
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

                String selection = SubscriptionInfoEntry._ID + " =? ";
                String[] selectionArgs = {Integer.toString(mSubId)};

                String[] subColumns = {
                        SubscriptionInfoEntry.COLUMN_NAME,
                        SubscriptionInfoEntry.COLUMN_DESCRIPTION,
                        SubscriptionInfoEntry.COLUMN_COST,
                        SubscriptionInfoEntry.COLUMN_DATE

                };
                return db.query(SubscriptionInfoEntry.TABLE_NAME, subColumns, selection, selectionArgs, null, null, null);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}