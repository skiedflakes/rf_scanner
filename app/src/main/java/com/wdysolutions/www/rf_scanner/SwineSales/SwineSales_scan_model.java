package com.wdysolutions.www.rf_scanner.SwineSales;

public class SwineSales_scan_model {
    private int swine_id;
    private String swine_code;
    private String age;
    private String feeds_cons;
    private Double weight;
    private Double price;
    private Double subtotal;
    private String delivery_number;

    public SwineSales_scan_model(int swine_id, String swine_code,String age, String feeds_cons,
                                 Double weight,Double price,Double subtotal,String delivery_number){
        this.swine_id = swine_id;
        this.swine_code = swine_code;
        this.age = age;
        this.feeds_cons = feeds_cons;
        this.weight = weight;
        this.price = price;
        this.subtotal = subtotal;
        this.delivery_number = delivery_number;

    }

    public String getDelivery_number() {
        return delivery_number;
    }

    public Double getPrice() {
        return price;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public Double getWeight() {
        return weight;
    }

    public String getAge() {
        return age;
    }

    public String getFeeds_cons() {
        return feeds_cons;
    }

    public int getSwine_id() {
        return swine_id;
    }

    public String getSwine_code() {
        return swine_code;
    }



    public void setSwine_code(String swine_code) {
        this.swine_code = swine_code;
    }

    public void setSwine_id(int swine_id) {
        this.swine_id = swine_id;
    }


}
