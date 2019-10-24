package com.wdysolutions.www.rf_scanner.Feeding.FeedingPerPen_Details.Update;

public class Feeding_Pen_Update_model {

    String product, product_id;

    public Feeding_Pen_Update_model(String product, String product_id){
        this.product = product;
        this.product_id = product_id;
    }

    public String getProduct() {
        return product;
    }

    public String getProduct_id() {
        return product_id;
    }
}
