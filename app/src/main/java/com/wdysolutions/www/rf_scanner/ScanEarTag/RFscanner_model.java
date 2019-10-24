package com.wdysolutions.www.rf_scanner.ScanEarTag;

/**
 * Created by aronandrada on 12/4/18.
 */

public class RFscanner_model {

    String name;
    String details;
    String colorType;

    public RFscanner_model(String name, String details, String colorType){
        this.name = name;
        this.details = details;
        this.colorType = colorType;
    }

    public String getColorType() {
        return colorType;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }
}
