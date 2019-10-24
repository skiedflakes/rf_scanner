package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Mortality;

/**
 * Created by aronandrada on 1/25/19.
 */

public class Disease_model {

    private String disease_id;
    private String cause;

    public Disease_model(String disease_id, String cause){
        this.disease_id = disease_id;
        this.cause = cause;
    }

    public String getCause() {
        return cause;
    }

    public String getDisease_id() {
        return disease_id;
    }
}
