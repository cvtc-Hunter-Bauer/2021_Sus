package edu.cvtc.itCapstone.sus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

import edu.cvtc.itCapstone.sus.DatabaseContract.SubscriptionInfoEntry;

public class AddSub extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    // Member Constraints
    public static final String SUBSCRIPTION_ID = "edu.cvtc.itCapstone.sus.SUBSCRIPTION_ID";
    public static final String ORIGINAL_SUBSCRIPTION_NAME = "edu.cvtc.itCapstone.sus.ORIGINAL_SUBSCRIPTION_NAME";
    public static final String ORIGINAL_SUBSCRIPTION_DESCRIPTION = "edu.cvtc.itCapstone.sus.ORIGINAL_SUBSCRIPTION_DESCRIPTION";
    public static final String ORIGINAL_SUBSCRIPTION_COST = "edu.cvtc.itCapstone.sus.ORIGINAL_SUBSCRIPTION_COST";
    public static final String ORIGINAL_SUBSCRIPTION_DATE = "edu.cvtc.itCapstone.sus.ORIGINAL_SUBSCRIPTION_DATE";
    public static final String NOTIFICATION_TITLE = "edu.cvtc.itCapstone.sus_NOTIFICATION_TITLE";
    public static final String NOTIFICATION_TEXT = "edu.cvtc.itCapstone.sus_NOTIFICATION_TEXT";
    public static final String NOTIFICATION_ID = "edu.cvtc.itCapstone.sus_NOTIFICATION_ID";

    public static final int ID_NOT_SET = -1;
    public static final int LOADER_SUB = 0;
    private int mSubId;
    private SubscriptionOpenHelper mDbOpenHelper;
    private SharedPreferences sharedPref;
    private SharedPreferences sharedPref2;
    private String prefName = "spinner_value";
    int id=0;
    int p;

    private final SubscriptionInfo mSub = new SubscriptionInfo(0, "", "", 0.0, "");
    private int mSubNamePosition;
    private int mSubDescriptionPosition;
    private int mSubCostPosition;
    private int mSubDatePosition;

    private String mOriginalSubName;
    private String mOriginalSubDesc;
    private double mOriginalSubCost;
    private String mOriginalSubDate;

    private DatePickerDialog mDatepicker;
    private EditText mName;
    private EditText mDescription;
    private EditText mCost;
    private EditText mDate;
    private CheckBox mCheckbox;

    private Button mButton;
    private Button mDeleteButton;
    private Button mPayButton;
    private Cursor mCursor;
    private boolean mNewSub;
    private boolean mIsCancelling;
    String chosenOption;
    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub);

        sharedPref = this.getSharedPreferences("edu.cvtc.itCapstone", Context.MODE_PRIVATE);

        mDbOpenHelper = new SubscriptionOpenHelper(this);
        mButton = findViewById(R.id.button_save);
        mDeleteButton = findViewById(R.id.button_delete);
        mPayButton = findViewById(R.id.button_pay);

        // Calling before button press so The values can be populated
        readSateValues();
        mName = findViewById(R.id.text_name);
        mDescription = findViewById(R.id.text_description);
        mCost = findViewById(R.id.text_cost);
        // mDatepicker = findViewById(R.id.sub_DatePicker);
        mDate = findViewById(R.id.text_date);
        mCheckbox = findViewById(R.id.cbx_notification);




        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getting the current date using calendar class
                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                mDatepicker = new DatePickerDialog(AddSub.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        // Before we can set the text for the date we need to change it to be compatible with SQL
                        // It must be set into the format YYYY-MM-DD
                        // this includes requiring adding leading zeros to single digit numbers
                        // ie: 2021/09/01

                        month = month + 1;
                        // Convert int to strings
                        String monthString;
                        String dayString;
                        String yearString = String.valueOf(year);

                        // Month
                        if (month < 10) {
                            monthString = "0" + String.valueOf(month);
                        } else {
                            monthString = String.valueOf(month);
                        }

                        // Day
                        if (dayOfMonth < 10) {
                            dayString = "0" + String.valueOf(dayOfMonth);
                        } else {
                            dayString = String.valueOf(dayOfMonth);
                        }

                        mDate.setText(yearString + "-" + monthString + "-" + dayString);
                    }
                }, mYear, mMonth, mDay);
                mDatepicker.show();
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mName.getText().toString().trim().isEmpty() || mDescription.getText().toString().trim().isEmpty() || mCost.getText().toString().trim().isEmpty() || mDate.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddSub.this, "Please make sure the fields are filled", Toast.LENGTH_SHORT).show();
                } else {

                    if (savedInstanceState == null) {

                        saveOriginalSub();
                    } else {
                        restoreOriginalSub(savedInstanceState);
                    }

                    if (mCheckbox.isChecked()) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(String.valueOf(mSubId), 1);
                        editor.apply();

                        scheduleNotification();
                    } else {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(String.valueOf(mSubId), 0);
                        editor.apply();
                    }

                    startActivity(new Intent(AddSub.this, MainActivity.class));
                }
            }

        });


        if (!mNewSub) {
            LoaderManager.getInstance(this).initLoader(LOADER_SUB, null, this);
            mDeleteButton.setVisibility(View.VISIBLE);
            mPayButton.setVisibility(View.VISIBLE);
        }
    }


    // upper right back button will work as back and cancel
    @Override
    public void onBackPressed() {
        // do nothing.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel) {
            mIsCancelling = true;
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Did the user cancel the process?
        if (mIsCancelling) {
            // Is this a new course?
            if (mNewSub) {
                // Delete the new course.
                deleteSubscriptionFromDatabase();
            } else {
                // Put the original values on the screen.
                storePreviousSubValues();
            }
        } else {
            // Save the data when leaving the activity.
            saveSub();
        }
    }

    private void deleteSubscriptionFromDatabase() {
        // Create selection criteria
        final String selection = SubscriptionInfoEntry._ID + " = ?";
        final String[] selectionArgs = {Integer.toString(mSubId)};
        AsyncTaskLoader<String> task = new AsyncTaskLoader<String>(this) {
            @Nullable
            @Override
            public String loadInBackground() {
                // Get connection to the database. Use the writable
                // method since we are changing the data.
                SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
                // Call the delete method


                db.delete(SubscriptionInfoEntry.TABLE_NAME, selection, selectionArgs);
                return null;
            }
        };
        task.loadInBackground();
        finish();
    }

    private void saveSub() {
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


    private void showSub() {

        // gets the values and inserts them into the layout based on sub that is clicked
        if (mSubId != 0) {
            String subName = mCursor.getString(mSubNamePosition);
            String subDescription = mCursor.getString(mSubDescriptionPosition);
            String subCost = mCursor.getString(mSubCostPosition);
            String subDate = mCursor.getString(mSubDatePosition);

            int subNotificationString = sharedPref.getInt(String.valueOf(mSubId), 0);
            boolean subNotification = subNotificationString > 0;

            // fills the edit texts in the layout
            mName.setText(subName);
            mDescription.setText(subDescription);
            mCost.setText(subCost);
            mDate.setText(subDate);
            mCheckbox.setChecked(subNotification);
        }


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

        if (!mNewSub) {
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

        if (mNewSub) createNewSub();
    }

    private void createNewSub() {
        //  mName = findViewById(R.id.text_name);
        //  mDescription = findViewById(R.id.text_description);
        //  mCost = findViewById(R.id.text_cost);
        //  mDate = findViewById(R.id.text_date);
        ContentValues contentValues = new ContentValues();

        contentValues.put(SubscriptionInfoEntry.COLUMN_NAME, "");
        contentValues.put(SubscriptionInfoEntry.COLUMN_DESCRIPTION, "");
        contentValues.put(SubscriptionInfoEntry.COLUMN_COST, 0.0);
        contentValues.put(SubscriptionInfoEntry.COLUMN_DATE, "");

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        mSubId = (int) db.insert(SubscriptionInfoEntry.TABLE_NAME, null, contentValues);
    }

    private void storePreviousSubValues() {
        mSub.setName(mOriginalSubName);
        mSub.setDescription(mOriginalSubDesc);
        mSub.setCost(mOriginalSubCost);
        mSub.setDate(mOriginalSubDate);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        CursorLoader loader = null;

        if (id == LOADER_SUB) {
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
        if (loader.getId() == LOADER_SUB) {
            loadFinishedCourses(data);
        }
    }

    private void loadFinishedCourses(Cursor data) {
        // Populate our member cursor with the data
        mCursor = data;
        // Get the positions of the fields in the cursor so that
        // we are able to retrieve them into our layout.
        mSubNamePosition = mCursor.getColumnIndex(SubscriptionInfoEntry.COLUMN_NAME);
        mSubDescriptionPosition = mCursor.getColumnIndex(SubscriptionInfoEntry.COLUMN_DESCRIPTION);
        mSubCostPosition = mCursor.getColumnIndex(SubscriptionInfoEntry.COLUMN_COST);
        mSubDatePosition = mCursor.getColumnIndex(SubscriptionInfoEntry.COLUMN_DATE);

        // Make sure that we have moved to the correct record.
        // The cursor will not have populated any of the
        // fields until we move it.
        mCursor.moveToNext();
        // Call the method to display the course.
        showSub();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (loader.getId() == LOADER_SUB) {
            // If the sub is not null, close it
            if (mCursor != null) {
                mCursor.close();
            }
        }
    }

    private void scheduleNotification() {

        // Create our intent and load it with all the Strings it will need
        Intent intent = new Intent(this, NotificationHandler.class);
        intent.putExtra(NOTIFICATION_TITLE, "Upcoming Payment to " + mName.getText());
        intent.putExtra(NOTIFICATION_TEXT, "Your Payment to " + mName.getText() + " will be payed on " + mDate.getText() + ".");
        intent.putExtra(NOTIFICATION_ID, mSubId);

        // Set the intent to trigger when the alarm goes off
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 42069,intent,0);

        // Convert the date String into a Date object
        // First set a ParsePosition
        ParsePosition pos = new ParsePosition(0);

        // Second set the DateFormat
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Finally Parse the date
        Date date = simpleDateFormat.parse(mDate.getText().toString(), pos);
        // Convert the date into milliseconds and minus 259200000 milliseconds (3 Days)
        long displayDate = date.getTime() - 259200000;

        // Set the alarm to go off three days before the subscription is due
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC,displayDate ,pendingIntent);
    }
    // On Delete button click
    public void deleteSubscription(View view) {
        deleteSubscriptionFromDatabase();
    }

    public void playSubscription(View view) throws ParseException {
        int month;
        String strMonth;
        int day;
        String strDay;

        // Convert the date String into a Calender object
        // First Create our calender
        Calendar calendar = Calendar.getInstance();

        // Second set the DateFormat
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Finally Parse the date
        calendar.setTime(simpleDateFormat.parse(mDate.getText().toString()));

        calendar.set(Calendar.MONTH,  calendar.get(Calendar.MONTH) + 1);

        month = calendar.get(Calendar.MONTH) + 1;
        if (month < 10) {
            strMonth = "0" + month;
        } else {
            strMonth = String.valueOf(month);
        }

        day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day < 10) {
            strDay = "0" + day;
        } else {
            strDay = String.valueOf(day);
        }

        mDate.setText(calendar.get(Calendar.YEAR) + "-" + strMonth + "-" + strDay);

        mButton.performClick();
    }
}