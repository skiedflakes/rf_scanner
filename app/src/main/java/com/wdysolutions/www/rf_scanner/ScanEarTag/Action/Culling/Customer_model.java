package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Culling;

/**
 * Created by aronandrada on 1/26/19.
 */

public class Customer_model {

    private String customer_id;
    private String customer;

    public Customer_model(String customer_id, String customer){
        this.customer = customer;
        this.customer_id = customer_id;
    }

    public String getCustomer() {
        return customer;
    }

    public String getCustomer_id() {
        return customer_id;
    }
}
