package com.wdysolutions.www.rf_scanner.WritterTag;

public class WritterTag_model_pen {

    private int pen_assignment_id;
    private String pen_name;


    public WritterTag_model_pen(int pen_assignment_id, String pen_name){
        this.pen_assignment_id = pen_assignment_id;
        this.pen_name = pen_name;

    }

    public int getPen_assignment_id() {
        return pen_assignment_id;
    }

    public String getPen_name() {
        return pen_name;
    }
}
