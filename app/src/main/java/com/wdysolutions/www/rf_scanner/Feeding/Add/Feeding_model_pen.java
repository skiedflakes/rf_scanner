package com.wdysolutions.www.rf_scanner.Feeding.Add;

public class Feeding_model_pen {

    private int pen_assignment_id;
    private String pen_name;
    private String pen_type;
    private String max_heads;

    public Feeding_model_pen(int pen_assignment_id, String pen_name, String pen_type, String max_heads){
        this.pen_assignment_id = pen_assignment_id;
        this.pen_name = pen_name;
        this.pen_type = pen_type;
        this.max_heads = max_heads;
    }

    public String getMax_heads() {
        return max_heads;
    }

    public String getPen_type() {
        return pen_type;
    }

    public int getPen_assignment_id() {
        return pen_assignment_id;
    }

    public String getPen_name() {
        return pen_name;
    }
}
