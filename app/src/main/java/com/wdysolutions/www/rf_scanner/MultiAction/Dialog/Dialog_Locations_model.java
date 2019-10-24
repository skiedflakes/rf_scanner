package com.wdysolutions.www.rf_scanner.MultiAction.Dialog;

/**
 * Created by aronandrada on 1/24/19.
 */

public class Dialog_Locations_model {

    private String branch_id;
    private String branch;

    public Dialog_Locations_model(String branch_id, String branch){
        this.branch_id = branch_id;
        this.branch = branch;
    }

    public String getBranch() {
        return branch;
    }

    public String getBranch_id() {
        return branch_id;
    }
}
