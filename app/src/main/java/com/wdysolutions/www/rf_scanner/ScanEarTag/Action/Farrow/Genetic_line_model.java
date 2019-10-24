package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Farrow;

/**
 * Created by aronandrada on 2/2/19.
 */

public class Genetic_line_model {

    private String genetic_line_id;
    private String genetic_line;

    public Genetic_line_model(String genetic_line_id, String genetic_line){
        this.genetic_line_id = genetic_line_id;
        this.genetic_line = genetic_line;
    }

    public String getGenetic_line() {
        return genetic_line;
    }

    public String getGenetic_line_id() {
        return genetic_line_id;
    }
}
