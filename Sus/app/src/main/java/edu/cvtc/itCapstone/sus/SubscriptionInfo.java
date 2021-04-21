package edu.cvtc.itCapstone.sus;

import android.os.Parcel;
import android.os.Parcelable;

public class SubscriptionInfo implements Parcelable {

    private String mName;
    private String mDescription;
    private int mId;
    private int mCost;
    private String mDate;

    public SubscriptionInfo(String name, String description, Integer cost, String date) {
        mName = name;
        mDescription = description;
        mCost = cost;
        mDate = date;
    }

    public SubscriptionInfo(int id, String name, String description, Integer cost, String date) {
        mId = id;
        mName = name;
        mDescription = description;
        mCost = cost;
        mDate = date;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public int getId() {
        return mId;
    }

    public int getCost() {
        return mCost;
    }

    public void setCost(int cost) {
        mCost = cost;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
       mDate = date;
    }

    public String getCompareKey() {
        return mName + "|" + mDescription + "|" + mCost + "|" + mDate;
    }

    protected SubscriptionInfo(Parcel in) {
        mName = in.readString();
        mDescription = in.readString();
        mId = in.readInt();
        mCost = in.readInt();
        mDate = in.readString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        SubscriptionInfo that = (SubscriptionInfo) o;
        return getCompareKey().equals(that.getCompareKey());
    }

    @Override
    public int hashCode() {
        return getCompareKey().hashCode();
    }

    @Override
    public String toString(){
        return getCompareKey();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mDescription);
        dest.writeInt(mId);
        dest.writeInt(mCost);
        dest.writeString(mDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SubscriptionInfo> CREATOR = new Creator<SubscriptionInfo>() {
        @Override
        public SubscriptionInfo createFromParcel(Parcel in) {
            return new SubscriptionInfo(in);
        }

        @Override
        public SubscriptionInfo[] newArray(int size) {
            return new SubscriptionInfo[size];
        }
    };
}
