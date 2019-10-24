package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Farrowing_Stats.addBreedingFailed;

/**
 * Created by aronandrada on 1/15/19.
 */

public class Farrowing_addBreedFailed_model {

    private int swine_id;
    private String swine_code;

    public Farrowing_addBreedFailed_model(int swine_id, String swine_code){
        this.swine_id = swine_id;
        this.swine_code = swine_code;
    }

    public String getSwine_code() {
        return swine_code;
    }

    public int getSwine_id() {
        return swine_id;
    }
}
