package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Wean;

/**
 * Created by aronandrada on 1/31/19.
 */

public class Wean_array_check_model {

    private String id;
    private String eartag;
    private String gender;
    private String dateofbirth;

    public Wean_array_check_model(String id, String eartag, String gender, String dateofbirth){
        this.id = id;
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

    public String getId() {
        return id;
    }

}
