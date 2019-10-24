package com.wdysolutions.www.rf_scanner.ScanEarTag;

/**
 * Created by aronandrada on 12/10/18.
 */

public class RFscanner_buttonActions_model {

    private int id;
    private String name;
    private String disableStatus;

    public RFscanner_buttonActions_model(int id, String name, String disableStatus){
        this.id = id;
        this.name = name;
        this.disableStatus = disableStatus;
    }

    public String getDisableStatus() {
        return disableStatus;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
