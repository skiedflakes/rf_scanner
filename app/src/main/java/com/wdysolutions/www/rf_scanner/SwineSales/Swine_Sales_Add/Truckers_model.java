package com.wdysolutions.www.rf_scanner.SwineSales.Swine_Sales_Add;

public class Truckers_model {

    String trucker_id;
    String trucker;

    public Truckers_model(String trucker_id, String trucker) {
        this.trucker_id = trucker_id;
        this.trucker = trucker;
    }

    public String getTrucker_id() {
        return trucker_id;
    }

    public String getTrucker() {
        return trucker;
    }
}
