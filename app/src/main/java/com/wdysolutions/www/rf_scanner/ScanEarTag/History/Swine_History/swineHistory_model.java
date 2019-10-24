package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Swine_History;

/**
 * Created by aronandrada on 1/5/19.
 */

public class swineHistory_model {

    private int id;
    private String user_id;
    private String remarks;
    private String date;
    private String branch;
    private String detail;
    private String count;

    public swineHistory_model(int id, String user_id, String remarks, String date, String branch, String detail, String count){
        this.id = id;
        this.user_id = user_id;
        this.remarks = remarks;
        this.date = date;
        this.branch = branch;
        this.detail = detail;
        this.count = count;
    }


    public String getRemarks() {
        return remarks;
    }

    public String getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public String getCount() {
        return count;
    }

    public String getBranch() {
        return branch;
    }

    public String getDetail() {
        return detail;
    }

    public String getUser_id() {
        return user_id;
    }
}
