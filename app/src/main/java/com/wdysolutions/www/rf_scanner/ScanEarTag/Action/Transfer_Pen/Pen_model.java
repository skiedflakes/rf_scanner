package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Transfer_Pen;

/**
 * Created by aronandrada on 1/24/19.
 */

public class Pen_model {

    private String pen_assignment_id;
    private String pen_name;
    private String pen_type;

    public Pen_model(String pen_assignment_id, String pen_name, String pen_type){
        this.pen_assignment_id = pen_assignment_id;
        this.pen_name = pen_name;
        this.pen_type = pen_type;
    }

    public String getPen_assignment_id() {
        return pen_assignment_id;
    }

    public String getPen_name() {
        return pen_name;
    }

    public String getPen_type() {
        return pen_type;
    }
}
