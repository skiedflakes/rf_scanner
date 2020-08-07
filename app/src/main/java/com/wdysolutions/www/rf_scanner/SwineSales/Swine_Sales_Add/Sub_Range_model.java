package com.wdysolutions.www.rf_scanner.SwineSales.Swine_Sales_Add;

public class Sub_Range_model {

    String invoice_id, sub_range;

    public Sub_Range_model(String invoice_id, String sub_range){
        this.invoice_id = invoice_id;
        this.sub_range = sub_range;
    }

    public String getInvoice_id() {
        return invoice_id;
    }

    public String getSub_range() {
        return sub_range;
    }
}
