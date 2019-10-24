package com.wdysolutions.www.rf_scanner.MultiAction;

public class Transfer_model_building {

    String building_id, building;

    public Transfer_model_building(String building, String building_id){
        this.building = building;
        this.building_id = building_id;
    }

    public String getBuilding() {
        return building;
    }

    public String getBuilding_id() {
        return building_id;
    }
}
