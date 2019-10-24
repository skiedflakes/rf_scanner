package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Farrowing_Stats.Born_Alive;

/**
 * Created by aronandrada on 1/17/19.
 */

public class Farrowing_Born_Alive_addArray_model {

    private int id;
    private String value;
    private String status;

    public Farrowing_Born_Alive_addArray_model(int id, String value, String status){
        this.id = id;
        this.value = value;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
