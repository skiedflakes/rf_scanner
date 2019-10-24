package com.wdysolutions.www.rf_scanner.Feeding.Add;

public class Feeding_model_product {

    private String product_id;
    private String product;

    public Feeding_model_product(String product, String product_id){
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
