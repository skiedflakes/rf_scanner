package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Vaccination.Add_Vaccine;

/**
 * Created by aronandrada on 12/6/18.
 */

public class Vaccine_model {

    private int id;
    private String name;

    public Vaccine_model(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
