package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Feeding;

/**
 * Created by aronandrada on 1/9/19.
 */

public class Feeding_model {

    private int id;
    private String count;
    private String product_id;
    private String pen_id;
    private String date;
    private String amount;
    private String total_cost;
    private String swine_id;


    public Feeding_model(int id, String count, String product_id, String pen_id, String date, String amount, String total_cost, String swine_id){
        this.id = id;
        this.count = count;
        this.product_id = product_id;
        this.pen_id = pen_id;
        this.date = date;
        this.amount = amount;
        this.total_cost = total_cost;
        this.swine_id = swine_id;
    }

    public String getSwine_id() {
        return swine_id;
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

    public String getAmount() {
        return amount;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getTotal_cost() {
        return total_cost;
    }

    public String getPen_id() {
        return pen_id;
    }
}
