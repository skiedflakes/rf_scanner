package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Mated_History;

/**
 * Created by aronandrada on 1/5/19.
 */

public class matedHistory_model {

    private int id;
    private String count;
    private String breeding_date;
    private String dry_days;
    private String boar_1;
    private String boar_2;
    private String boar_3;
    private String status;
    private String remarks;
    private String allow_delete;
    private String breeding_status;

    public matedHistory_model(int id, String count, String breeding_date, String dry_days, String boar_1, String boar_2,
                              String boar_3, String status, String remarks, String allow_delete, String breeding_status){
        this.id = id;
        this.count = count;
        this.breeding_date = breeding_date;
        this.dry_days = dry_days;
        this.boar_1 = boar_1;
        this.boar_2 = boar_2;
        this.boar_3 = boar_3;
        this.status = status;
        this.remarks = remarks;
        this.allow_delete = allow_delete;
        this.breeding_status = breeding_status;
    }

    public String getStatus() {
        return status;
    }

    public String getCount() {
        return count;
    }

    public int getId() {
        return id;
    }

    public String getBoar_1() {
        return boar_1;
    }

    public String getBoar_2() {
        return boar_2;
    }

    public String getBoar_3() {
        return boar_3;
    }

    public String getBreeding_date() {
        return breeding_date;
    }

    public String getDry_days() {
        return dry_days;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getAllow_delete() {
        return allow_delete;
    }

    public String getBreeding_status() {
        return breeding_status;
    }
}
