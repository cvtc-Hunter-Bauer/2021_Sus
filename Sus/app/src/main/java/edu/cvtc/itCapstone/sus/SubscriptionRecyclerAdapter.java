package edu.cvtc.itCapstone.sus;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.cvtc.itCapstone.sus.DatabaseContract.SubscriptionInfoEntry;

public class SubscriptionRecyclerAdapter extends RecyclerView.Adapter<SubscriptionRecyclerAdapter.ViewHolder> {

    // Member variables
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private Cursor mCursor;
    private int mSubTitlePosition;
    private int mSubDescriptionPosition;
    private int mSubCostPosition;
    private int mDatePosition;
    private int mIdPosition;

    public SubscriptionRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        mLayoutInflater = LayoutInflater.from(context);

        // Used to get the positions of the columns we
        // are interested in.
        populateColumnPositions();

    }

    private void populateColumnPositions() {

        if (mCursor != null) {
            // Get column indexes from mCursor
            mSubTitlePosition = mCursor.getColumnIndex(SubscriptionInfoEntry.COLUMN_NAME);
            mSubDescriptionPosition = mCursor.getColumnIndex(SubscriptionInfoEntry.COLUMN_DESCRIPTION);
            mSubCostPosition = mCursor.getColumnIndex(SubscriptionInfoEntry.COLUMN_COST);
            mDatePosition = mCursor.getColumnIndex(SubscriptionInfoEntry.COLUMN_DATE);
            mIdPosition = mCursor.getColumnIndex(SubscriptionInfoEntry._ID);
        }

    }

    public void changeCursor(Cursor cursor) {

        // If the cursor is open, close it
        if (mCursor != null) {
            mCursor.close();
        }

        // Create a new cursor based upon
        // the object passed in.
        mCursor = cursor;

        // Get the positions of the columns in
        // your cursor.
        populateColumnPositions();

        // Tell the activity that the data set
        // has changed.
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.sub_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Move the cursor to the correct row
        mCursor.moveToPosition(position);

        // Get the actual values
        String subTitle = mCursor.getString(mSubTitlePosition);
        String subDescription = mCursor.getString(mSubDescriptionPosition);
        String subCost = mCursor.getString(mSubCostPosition);
        String subDate = mCursor.getString(mDatePosition);
        int id = mCursor.getInt(mIdPosition);

        // Pass the information into the holder
        holder.mSubTitle.setText(subTitle);
        holder.mSubDescription.setText(subDescription);
        holder.mSubCost.setText(subCost);
        holder.mSubDate.setText(subDate);
        holder.mId = id;
    }

    @Override
    public int getItemCount() {

        // If the cursor is null, return 0. Otherwise
        // return the count of records in it.
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Member variables for inner class
        public final TextView mSubTitle;
        public final TextView mSubDescription;
        public final TextView mSubCost;
        public final TextView mSubDate;
        public int mId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mSubTitle = (TextView)itemView.findViewById(R.id.sub_title);
            mSubDescription = (TextView)itemView.findViewById(R.id.sub_description);
            mSubCost = (TextView)itemView.findViewById(R.id.sub_cost);
            mSubDate = (TextView)itemView.findViewById(R.id.sub_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
              public void onClick(View view) {
                    Intent intent = new Intent(mContext, AddSub.class);
                intent.putExtra(AddSub.SUBSCRIPTION_ID, mId);
                    mContext.startActivity(intent);
               }
            });

        }
    }
}
