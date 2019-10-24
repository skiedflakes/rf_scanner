package com.wdysolutions.www.rf_scanner.WritterTag;

public class WritterTag_model_pig {

    private int swine_id;
    private String swine_code;
    private int check_status;
    private int tag_counter;

    public WritterTag_model_pig(int swine_id, String swine_code,int check_status,int tag_counter){
        this.swine_id = swine_id;
        this.swine_code = swine_code;
        this.check_status =check_status;
        this.tag_counter=tag_counter;

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

    public int getTag_counter() {
        return tag_counter;
    }
}
