package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.AI_Natural_Breeding;

/**
 * Created by aronandrada on 2/8/19.
 */

public class Technician_model {

    private String tech_id;
    private String tech_name;

    public Technician_model(String tech_id, String tech_name){
        this.tech_id = tech_id;
        this.tech_name = tech_name;
    }

    public String getTech_id() {
        return tech_id;
    }

    public String getTech_name() {
        return tech_name;
    }
}
