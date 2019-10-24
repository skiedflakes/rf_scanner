package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Farrow;

/**
 * Created by aronandrada on 2/2/19.
 */

public class Progeny_model {

    private String classification_id;
    private String classification;

    public Progeny_model(String classification_id, String classification){
        this.classification_id = classification_id;
        this.classification = classification;
    }

    public String getClassification() {
        return classification;
    }

    public String getClassification_id() {
        return classification_id;
    }
}
