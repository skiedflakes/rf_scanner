package com.wdysolutions.www.rf_scanner.ScanEarTag;

/**
 * Created by aronandrada on 12/19/18.
 */

public class RFscanner_piglets_model {

    private int id;
    private String name;
    private String weight;

    public RFscanner_piglets_model(int id, String name, String weight){
        this.id = id;
        this.name = name;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWeight() {
        return weight;
    }
}
