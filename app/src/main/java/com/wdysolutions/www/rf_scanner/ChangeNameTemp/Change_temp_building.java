package com.wdysolutions.www.rf_scanner.ChangeNameTemp;

public class Change_temp_building {

    String building_id, building;

    public Change_temp_building(String building, String building_id){
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
