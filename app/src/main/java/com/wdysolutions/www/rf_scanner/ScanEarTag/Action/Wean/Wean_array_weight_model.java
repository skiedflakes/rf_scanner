package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Wean;

/**
 * Created by aronandrada on 2/1/19.
 */

public class Wean_array_weight_model {

    private String swine_code;
    private String wean_weight;
    private String swine_id;

    public Wean_array_weight_model(String swine_code, String swine_id, String wean_weight){
        this.swine_code = swine_code;
        this.wean_weight = wean_weight;
        this.swine_id = swine_id;
    }

    public String getSwine_code() {
        return swine_code;
    }

    public String getSwine_id() {
        return swine_id;
    }

    public String getWean_weight() {
        return wean_weight;
    }
}
