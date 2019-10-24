package com.wdysolutions.www.rf_scanner.ScanEarTag;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aronandrada on 1/19/19.
 */

public class RFscanner_piglets_add_model{

    private int id;
    private String status;

    public RFscanner_piglets_add_model(int id, String status){
        this.id = id;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }


}
