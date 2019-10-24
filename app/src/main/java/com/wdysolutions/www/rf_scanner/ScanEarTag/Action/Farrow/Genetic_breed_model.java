package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Farrow;

/**
 * Created by aronandrada on 2/2/19.
 */

public class Genetic_breed_model {

    private String genetic_id;
    private String genetic;

    public Genetic_breed_model(String genetic_id, String genetic){
        this.genetic_id = genetic_id;
        this.genetic = genetic;
    }

    public String getGenetic() {
        return genetic;
    }

    public String getGenetic_id() {
        return genetic_id;
    }
}
