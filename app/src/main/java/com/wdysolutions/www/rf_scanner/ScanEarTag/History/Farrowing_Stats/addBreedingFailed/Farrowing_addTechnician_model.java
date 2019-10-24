package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Farrowing_Stats.addBreedingFailed;

/**
 * Created by aronandrada on 1/15/19.
 */

public class Farrowing_addTechnician_model {

    private int technician_id;
    private String technician;

    public Farrowing_addTechnician_model(int technician_id, String technician){
        this.technician_id = technician_id;
        this.technician = technician;
    }

    public int getTechnician_id() {
        return technician_id;
    }

    public String getTechnician() {
        return technician;
    }
}
