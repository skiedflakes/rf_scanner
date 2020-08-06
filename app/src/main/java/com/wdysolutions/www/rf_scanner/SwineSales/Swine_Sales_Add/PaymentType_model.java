package com.wdysolutions.www.rf_scanner.SwineSales.Swine_Sales_Add;

public class PaymentType_model {

    String payment_id;
    String payment;

    public void PaymentType_model(String payment_id,String payment){
        this.payment=payment;
        this.payment_id=payment_id;
    }

    public String getPayment() {
        return payment;
    }

    public String getPayment_id() {
        return payment_id;
    }
}
