package com.wdysolutions.www.rf_scanner.ChangeNameTemp;

public class Change_temp_pen {

    String pen_id, pen_name;

    public Change_temp_pen(String pen_id, String pen_name){
        this.pen_id = pen_id;
        this.pen_name = pen_name;
    }

    public String getPen_id() {
        return pen_id;
    }

    public String getPen_name() {
        return pen_name;
    }
}
