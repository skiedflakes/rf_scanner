package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Medication;

/**
 * Created by aronandrada on 12/22/18.
 */

public class Medication_model {

    private int id;
    private String product_id;
    private String diagnosis;
    private String btn;
    private String total_cost;
    private String date;
    private String amount;
    private String swine_id;

    public Medication_model(int id, String product_id, String diagnosis, String btn, String total_cost, String date, String amount, String swine_id){
        this.id = id;
        this.product_id = product_id;
        this.diagnosis = diagnosis;
        this.btn = btn;
        this.total_cost = total_cost;
        this.date = date;
        this.amount = amount;
        this.swine_id = swine_id;
    }

    public String getSwine_id() {
        return swine_id;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getDate() {
        return date;
    }

    public String getTotal_cost() {
        return total_cost;
    }

    public int getId() {
        return id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getBtn() {
        return btn;
    }

    public String getAmount() {
        return amount;
    }
}
