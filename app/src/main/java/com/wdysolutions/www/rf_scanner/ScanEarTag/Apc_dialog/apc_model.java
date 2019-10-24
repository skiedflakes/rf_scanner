package com.wdysolutions.www.rf_scanner.ScanEarTag.Apc_dialog;

/**
 * Created by aronandrada on 3/25/19.
 */

public class apc_model {

    private String product;
    private String amount;
    private String date;

    public apc_model(String product, String amount, String date){
        this.product = product;
        this.amount = amount;
        this.date = date;
    }

    public String getProduct() {
        return product;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }
}
