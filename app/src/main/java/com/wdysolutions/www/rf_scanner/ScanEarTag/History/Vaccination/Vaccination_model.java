package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Vaccination;

/**
 * Created by aronandrada on 12/6/18.
 */

public class Vaccination_model {

    private int id;
    private String dosage;
    private String product_id;
    private String total_cost;
    private String btn;
    private String date;
    private String count;
    private String swine_id;

    public Vaccination_model(int id, String dosage, String product_id, String total_cost, String btn, String date, String count, String swine_id) {
        this.id = id;
        this.dosage = dosage;
        this.product_id = product_id;
        this.total_cost = total_cost;
        this.btn = btn;
        this.date = date;
        this.count = count;
        this.swine_id = swine_id;
    }

    public String getSwine_id() {
        return swine_id;
    }

    public String getCount() {
        return count;
    }

    public String getBtn() {
        return btn;
    }

    public String getDate() {
        return date;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getTotal_cost() {
        return total_cost;
    }

    public String getDosage() {
        return dosage;
    }

    public int getId() {
        return id;
    }

}
