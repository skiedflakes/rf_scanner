package com.wdysolutions.www.rf_scanner.Medication_Vaccination;

/**
 * Created by aronandrada on 2/28/19.
 */

public class Med_Vac_model_pig {

    private String swine_id;
    private String swine_code;
    private String status;

    public Med_Vac_model_pig(String swine_id, String swine_code, String status){
        this.swine_code = swine_code;
        this.swine_id = swine_id;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getSwine_id() {
        return swine_id;
    }

    public String getSwine_code() {
        return swine_code;
    }
}
