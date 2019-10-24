package com.wdysolutions.www.rf_scanner.Feeding.FeedingPerPen_Details;

public class Feeding_Pen_model {

    String feeding_id, ear_tag, quantity, btn_status, swine_id, check_status;

    public Feeding_Pen_model(String feeding_id, String ear_tag, String quantity, String btn_status, String swine_id, String check_status){
        this.feeding_id = feeding_id;
        this.ear_tag = ear_tag;
        this.quantity = quantity;
        this.btn_status = btn_status;
        this.swine_id = swine_id;
        this.check_status = check_status;
    }

    public String getCheck_status() {
        return check_status;
    }

    public String getSwine_id() {
        return swine_id;
    }

    public String getFeeding_id() {
        return feeding_id;
    }

    public String getBtn_status() {
        return btn_status;
    }

    public String getEar_tag() {
        return ear_tag;
    }

    public String getQuantity() {
        return quantity;
    }
}
