package com.wdysolutions.www.rf_scanner.Medication_Vaccination;

/**
 * Created by aronandrada on 3/4/19.
 */

public class Med_Vac_model_pen {

    private int pen_assignment_id;
    private String pen_name;
    private String red_status;

    public Med_Vac_model_pen(int pen_assignment_id, String pen_name, String red_status){
        this.pen_assignment_id = pen_assignment_id;
        this.pen_name = pen_name;
        this.red_status = red_status;
    }

    public String getRed_status() {
        return red_status;
    }

    public int getPen_assignment_id() {
        return pen_assignment_id;
    }

    public String getPen_name() {
        return pen_name;
    }
}
