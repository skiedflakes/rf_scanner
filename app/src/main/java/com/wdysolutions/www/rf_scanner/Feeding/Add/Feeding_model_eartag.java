package com.wdysolutions.www.rf_scanner.Feeding.Add;

public class Feeding_model_eartag {

    private String swine_id;
    private String swine_name;
    private String checkStatus;
    private boolean isCheckAll;

    public Feeding_model_eartag(String swine_id, String swine_name, String checkStatus, boolean isCheckAll){
        this.swine_id = swine_id;
        this.swine_name = swine_name;
        this.checkStatus = checkStatus;
        this.isCheckAll = isCheckAll;
    }

    public boolean getisCheckAll(){
        return isCheckAll;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public String getSwine_id() {
        return swine_id;
    }

    public String getSwine_name() {
        return swine_name;
    }
}
