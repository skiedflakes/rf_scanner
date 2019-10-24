package com.wdysolutions.www.rf_scanner.ScanEarTag.History.AI_Materials_Semen;

/**
 * Created by aronandrada on 1/14/19.
 */

public class AI_Materials_model {

    private int id;
    private String count;
    private String product_id;
    private String source_boar;
    private String date;
    private String quantity;
    private String total_cost;
    private String btn;

    public AI_Materials_model(int id, String count, String product_id, String source_boar, String date, String quantity, String total_cost, String btn){
        this.id = id;
        this.count = count;
        this.product_id = product_id;
        this.source_boar = source_boar;
        this.date = date;
        this.quantity = quantity;
        this.total_cost = total_cost;
        this.btn = btn;
    }

    public int getId() {
        return id;
    }

    public String getCount() {
        return count;
    }

    public String getDate() {
        return date;
    }

    public String getTotal_cost() {
        return total_cost;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getBtn() {
        return btn;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getSource_boar() {
        return source_boar;
    }
}
