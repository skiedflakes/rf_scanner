package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Wean;

/**
 * Created by aronandrada on 1/31/19.
 */

public class Wean_array_model {

    private String id;
    private String status;
    private String eartag;
    private String gender;
    private String dateofbirth;

    public Wean_array_model(String id, String status, String eartag, String gender, String dateofbirth){
        this.id = id;
        this.status = status;
        this.eartag = eartag;
        this.gender = gender;
        this.dateofbirth = dateofbirth;
    }

    public String getGender() {
        return gender;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public String getEartag() {
        return eartag;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

}
