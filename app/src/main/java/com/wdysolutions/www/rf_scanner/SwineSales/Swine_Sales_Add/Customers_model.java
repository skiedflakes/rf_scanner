package com.wdysolutions.www.rf_scanner.SwineSales.Swine_Sales_Add;

public class Customers_model {

    String customer_id;
    String customer;

    public Customers_model(String customer,String customer_id){
        this.customer=customer;
        this.customer_id=customer_id;
    }

    public String getCustomer() {
        return customer;
    }

    public String getCustomer_id() {
        return customer_id;
    }
}
