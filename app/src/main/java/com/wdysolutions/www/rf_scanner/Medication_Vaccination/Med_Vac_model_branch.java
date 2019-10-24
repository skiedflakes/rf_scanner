package com.wdysolutions.www.rf_scanner.Medication_Vaccination;

/**
 * Created by aronandrada on 2/28/19.
 */

public class Med_Vac_model_branch {

    private int branch_id;
    private String branch;

    public Med_Vac_model_branch(int branch_id, String branch){
        this.branch_id = branch_id;
        this.branch = branch;
    }

    public int getBranch_id() {
        return branch_id;
    }

    public String getBranch() {
        return branch;
    }
}
