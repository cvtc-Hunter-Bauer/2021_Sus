package edu.cvtc.itCapstone.sus;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import edu.cvtc.itCapstone.sus.DatabaseContract.SubscriptionInfoEntry;
public class DataManager {
    private static DataManager ourInstance = null;
    private List<SubscriptionInfo> mSubscriptions = new ArrayList<>();
    public static DataManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new DataManager();
        }
        return ourInstance;
    }

    // Return a list of your courses
    public List<SubscriptionInfo> getSubscriptions() {
        return mSubscriptions;
    }


    private static void loadCoursesFromDatabase(Cursor cursor) {

        // Retrieve the field positions in your database.
        // The positions of fields may change over time as the database grows, so
        // you want to use your constants to reference where those positions are in
        // the table.
        int listTitlePosition = cursor.getColumnIndex(SubscriptionInfoEntry.COLUMN_NAME);
        int listDescriptionPosition = cursor.getColumnIndex(SubscriptionInfoEntry.COLUMN_DESCRIPTION);
        int listCostPosition = cursor.getColumnIndex(SubscriptionInfoEntry.COLUMN_COST);
        int listDatePosition = cursor.getColumnIndex(SubscriptionInfoEntry.COLUMN_DATE);
        int idPosition = cursor.getColumnIndex(SubscriptionInfoEntry._ID);

        // Create an instance of your DataManager and use the DataManager
        // to clear any information from the array list.
        DataManager dm = getInstance();
        dm.mSubscriptions.clear();

        // Loop through the cursor rows and add new course objects to
        // your array list.
        while (cursor.moveToNext()) {
            String listTitle = cursor.getString(listTitlePosition);
            String listDescription = cursor.getString(listDescriptionPosition);
            double listCost = cursor.getDouble(listCostPosition);
            String listDate = cursor.getString(listDatePosition);
            int id = cursor.getInt(idPosition);
            SubscriptionInfo list = new SubscriptionInfo(id, listTitle, listDescription, listCost, listDate);
            dm.mSubscriptions.add(list);
        }

        // Close the cursor (to prevent memory leaks).
        cursor.close();
    }

    // Load items from database
    public static void loadFromDatabase(SubscriptionOpenHelper dbHelper) {

        // Open your database in read mode.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Create a list of columns you want to return.
        String[] subColumns = {
                SubscriptionInfoEntry.COLUMN_NAME,
                SubscriptionInfoEntry.COLUMN_DESCRIPTION,
                SubscriptionInfoEntry.COLUMN_COST,
                SubscriptionInfoEntry.COLUMN_DATE,
                SubscriptionInfoEntry._ID};

        // Create an order by field for sorting purposes.
        String subOrderBy = SubscriptionInfoEntry.COLUMN_NAME;

        // Populate your cursor with the results of the query.
        final Cursor courseCursor = db.query(SubscriptionInfoEntry.TABLE_NAME, subColumns,
                null, null, null, null,
                subOrderBy);

        // Call the method to load your array list.
        loadCoursesFromDatabase(courseCursor);
    }

    // Create subscription
    public int createNewSub() {

        // Create an empty subscription object to use on your activity screen
        // when you want a "blank" record to show up. It will return the
        // size of the new course array list.
        SubscriptionInfo subscription = new SubscriptionInfo(null, null, 0.0, null);
        mSubscriptions.add(subscription);
        return mSubscriptions.size();
    }

    // Remove course at passed in index
    public void removeCourse(int index) {
        mSubscriptions.remove(index);
    }
}
