package edu.cvtc.itCapstone.sus;

import android.os.Bundle;

public class SubViewModel {

    public static final String ORIGINAL_SUB_NAME="edu.cvtc.itCapstone.sus.ORIGINAL_SUB_NAME";
    public static final String ORIGINAL_SUB_DESCRIPTION = "edu.cvtc.itCapstone.sus.ORIGINAL_SUB_DESCRIPTION";
    public static final String ORIGINAL_SUB_COST = "edu.cvtc.itCapstone.sus.ORIGINAL_SUB_COST";
    public static final String ORIGINAL_SUB_DATE = "edu.cvtc.itCapstone.sus.ORIGINAL_SUB_DATE";

    public String mOriginalSubName;
    public String mOriginalSubDescription;
    public String mOriginalSubDate;
    public String mOriginalSubCost;
    public boolean mIsNewlyCreated = true;

    public void saveState(Bundle bundle) {
        bundle.putString(ORIGINAL_SUB_NAME, mOriginalSubName);
        bundle.putString(ORIGINAL_SUB_DESCRIPTION, mOriginalSubDescription);
        bundle.putString(ORIGINAL_SUB_COST, mOriginalSubCost);
        bundle.putString(ORIGINAL_SUB_DATE, mOriginalSubDate);
    }


    public void restoreState(Bundle bundle) {

        mOriginalSubName = bundle.getString(ORIGINAL_SUB_NAME);
        mOriginalSubDescription = bundle.getString(ORIGINAL_SUB_DESCRIPTION);
        mOriginalSubCost = bundle.getString(ORIGINAL_SUB_COST);
        mOriginalSubDate = bundle.getString(ORIGINAL_SUB_DATE);
    }
}
