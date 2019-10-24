package com.wdysolutions.www.rf_scanner.MultiAction;

public class Transfer_model_pig {
    private int swine_id;
    private String swine_code;
    private int check_status;
    private int branch_id;
    private int building_id;
    private int pen_id;

    public Transfer_model_pig(int swine_id, String swine_code,int branch_id,int building_id,int pen_id, int check_status){
        this.swine_id = swine_id;
        this.swine_code = swine_code;
        this.check_status =check_status;
        this.building_id = building_id;
        this.branch_id = branch_id;
        this.pen_id = pen_id;
    }

    public int getPen_id() {
        return pen_id;
    }
    public int getSwine_id() {
        return swine_id;
    }

    public String getSwine_code() {
        return swine_code;
    }

    public int getCheck_status() {
        return check_status;
    }

    public int getBuilding_id() {
        return building_id;
    }
    public int getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(int branch_id) {
        this.branch_id = branch_id;
    }

    public void setBuilding_id(int building_id) {
        this.building_id = building_id;
    }

    public void setCheck_status(int check_status) {
        this.check_status = check_status;
    }

    public void setPen_id(int pen_id) {
        this.pen_id = pen_id;
    }

    public void setSwine_code(String swine_code) {
        this.swine_code = swine_code;
    }

    public void setSwine_id(int swine_id) {
        this.swine_id = swine_id;
    }
}
