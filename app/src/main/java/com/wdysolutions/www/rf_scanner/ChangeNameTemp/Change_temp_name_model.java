package com.wdysolutions.www.rf_scanner.ChangeNameTemp;

public class Change_temp_name_model {

    String id, swine_code;

    public Change_temp_name_model(String id, String swine_code){
        this.id = id;
        this.swine_code = swine_code;
    }

    public String getId() {
        return id;
    }

    public String getSwine_code() {
        return swine_code;
    }
}
