package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Remarks;

/**
 * Created by aronandrada on 1/10/19.
 */

public class Remarks_model {

    private int id;
    private String count;
    private String remarks;
    private String date;

    public Remarks_model(int id, String count, String remarks, String date){
        this.id = id;
        this.count = count;
        this.remarks = remarks;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getCount() {
        return count;
    }

    public String getDate() {
        return date;
    }

    public String getRemarks() {
        return remarks;
    }
}
