package com.wdysolutions.www.rf_scanner.Feeding.Add;

public class Feeding_model_building {

    private int building_id;
    private String building;

    public Feeding_model_building(int building_id, String building){
        this.building_id = building_id;
        this.building = building;
    }

    public int getBuilding_id() {
        return building_id;
    }

    public String getBuilding() {
        return building;
    }
}
