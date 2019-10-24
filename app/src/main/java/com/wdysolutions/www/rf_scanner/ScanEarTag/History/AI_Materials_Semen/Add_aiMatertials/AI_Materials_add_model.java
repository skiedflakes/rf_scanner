package com.wdysolutions.www.rf_scanner.ScanEarTag.History.AI_Materials_Semen.Add_aiMatertials;

/**
 * Created by aronandrada on 1/14/19.
 */

public class AI_Materials_add_model {

    private int product_id;
    private String product;

    public AI_Materials_add_model(int product_id, String product){
        this.product = product;
        this.product_id = product_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getProduct() {
        return product;
    }

}
