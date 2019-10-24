package com.wdysolutions.www.rf_scanner.Feeding;

public class Feeding_model_data {

    private String id;
    private String num_of_heads;
    private String pen_name;
    private String building_name;
    private String quantity;
    private String product_name;
    private String date_of_feeding;
    private String isCheck;

    public Feeding_model_data(String id, String num_of_heads, String pen_name, String building_name, String quantity, String product_name,
                              String date_of_feeding, String isCheck){
        this.id = id;
        this.num_of_heads = num_of_heads;
        this.pen_name = pen_name;
        this.building_name = building_name;
        this.quantity = quantity;
        this.product_name = product_name;
        this.date_of_feeding = date_of_feeding;
        this.isCheck = isCheck;
    }

    public String getIsCheck() {
        return isCheck;
    }

    public String getNum_of_heads() {
        return num_of_heads;
    }

    public String getId() {
        return id;
    }

    public String getBuilding_name() {
        return building_name;
    }

    public String getDate_of_feeding() {
        return date_of_feeding;
    }

    public String getPen_name() {
        return pen_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getQuantity() {
        return quantity;
    }
}
