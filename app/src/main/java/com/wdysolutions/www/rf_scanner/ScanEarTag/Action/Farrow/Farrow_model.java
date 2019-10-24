package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Farrow;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aronandrada on 2/4/19.
 */

public class Farrow_model {

    private String status;
    private String swine_code;
    private String gender;
    private String weight;

    public Farrow_model(String status, String swine_code, String gender, String weight){
        this.status = status;
        this.swine_code = swine_code;
        this.gender = gender;
        this.weight = weight;
    }

    public String getSwine_code() {
        return swine_code;
    }

    public String getGender() {
        return gender;
    }

    public String getStatus() {
        return status;
    }

    public String getWeight() {
        return weight;
    }

}
