package com.wdysolutions.www.rf_scanner.Feeding;

public class Feeding_model_delete {

    private String id;
    private int index;

    public Feeding_model_delete(String id, int index){
        this.id = id;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getId() {
        return id;
    }
}
