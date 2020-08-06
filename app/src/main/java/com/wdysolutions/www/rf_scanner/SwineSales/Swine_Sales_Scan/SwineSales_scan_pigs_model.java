package com.wdysolutions.www.rf_scanner.SwineSales.Swine_Sales_Scan;

public class SwineSales_scan_pigs_model {

    private int swine_id;
    private String swine_code;
    private int check_status;
    private int scanned_counter;
    private String age;

    public SwineSales_scan_pigs_model(int swine_id, String swine_code,int check_status,int scanned_counter,String age){
        this.swine_id = swine_id;
        this.swine_code = swine_code;
        this.check_status =check_status;
        this.scanned_counter = scanned_counter;
        this.age = age;
    }

    public int getScanned_counter() {
        return scanned_counter;
    }

    public String getAge() {
        return age;
    }

    public int getSwine_id() {
        return swine_id;
    }

    public String getSwine_code() {
        return swine_code;
    }

    public int getCheck_status() {
        return check_status;
    }
}
