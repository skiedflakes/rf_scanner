package com.wdysolutions.www.rf_scanner.MonthPicker;

public class monthPicker_model {

    private String text;
    private String value;

    public monthPicker_model(String text, String value){
        this.text = text;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
