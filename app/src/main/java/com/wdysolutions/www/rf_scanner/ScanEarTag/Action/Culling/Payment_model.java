package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Culling;

/**
 * Created by aronandrada on 1/26/19.
 */

public class Payment_model {

    private String id;
    private String name;

    public Payment_model(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
