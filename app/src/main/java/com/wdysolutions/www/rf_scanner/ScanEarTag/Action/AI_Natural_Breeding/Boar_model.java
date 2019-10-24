package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.AI_Natural_Breeding;

/**
 * Created by aronandrada on 2/8/19.
 */

public class Boar_model {

    private String boar_id;
    private String disable_info;
    private String boar_name;

    public Boar_model(String boar_id, String disable_info, String boar_name){
        this.boar_id = boar_id;
        this.disable_info = disable_info;
        this.boar_name = boar_name;
    }

    public String getBoar_id() {
        return boar_id;
    }

    public String getBoar_name() {
        return boar_name;
    }

    public String getDisable_info() {
        return disable_info;
    }
}
