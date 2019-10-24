package com.wdysolutions.www.rf_scanner.AuditPen;

public class AuditPen_model_branch {

    private int branch_id;
    private String branch;


    public AuditPen_model_branch(int branch_id, String branch){
        this.branch_id = branch_id;
        this.branch = branch;

    }

    public int getBranch_id() {
        return branch_id;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setBranch_id(int branch_id) {
        this.branch_id = branch_id;
    }
}
