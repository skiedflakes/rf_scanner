package com.wdysolutions.www.rf_scanner.SwineSales.dialog_viewDetails;

/**
 * Created by aronandrada on 3/27/19.
 */

public class viewDetails_model {

    private String id;
    private String packaging_id;
    private String stock_id;
    private String age;
    private String total_feed_comsumption;
    private String weight;
    private String price_kg;
    private String sub_total;
    private String price;

    public viewDetails_model(String id, String packaging_id, String stock_id, String age, String total_feed_comsumption, String weight,
                             String price_kg, String sub_total, String price){
        this.id = id;
        this.packaging_id = packaging_id;
        this.stock_id = stock_id;
        this.age = age;
        this.total_feed_comsumption = total_feed_comsumption;
        this.weight = weight;
        this.price_kg = price_kg;
        this.sub_total = sub_total;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getWeight() {
        return weight;
    }

    public String getAge() {
        return age;
    }

    public String getPackaging_id() {
        return packaging_id;
    }

    public String getPrice() {
        return price;
    }

    public String getPrice_kg() {
        return price_kg;
    }

    public String getStock_id() {
        return stock_id;
    }

    public String getSub_total() {
        return sub_total;
    }

    public String getTotal_feed_comsumption() {
        return total_feed_comsumption;
    }
}
