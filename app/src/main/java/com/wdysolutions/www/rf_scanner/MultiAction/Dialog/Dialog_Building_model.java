package com.wdysolutions.www.rf_scanner.MultiAction.Dialog;

/**
 * Created by aronandrada on 1/24/19.
 */

public class Dialog_Building_model {

    private String building_id;
    private String building_name;

    public Dialog_Building_model(String building_id, String building_name){
        this.building_id = building_id;
        this.building_name = building_name;
    }

    public String getBuilding_id() {
        return building_id;
    }

    public String getBuilding_name() {
        return building_name;
    }
}
