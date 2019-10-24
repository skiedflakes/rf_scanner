package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Farrowing_Stats.Born_Alive;

/**
 * Created by aronandrada on 1/17/19.
 */

public class Farrowing_Born_Alive_model {

    private int id;
    private String swine_code;
    private String gender;
    private String count;
    private String weight;
    private String swine_type;
    private String status;

    public Farrowing_Born_Alive_model(int id, String swine_code, String gender, String count, String weight, String swine_type, String status){
        this.id = id;
        this.swine_code = swine_code;
        this.swine_type = swine_type;
        this.gender = gender;
        this.count = count;
        this.weight = weight;
        this.status = status;
    }

    public String getSwine_code() {
        return swine_code;
    }

    public String getCount() {
        return count;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getWeight() {
        return weight;
    }

    public String getSwine_type() {
        return swine_type;
    }

    public String getGender() {
        return gender;
    }
}
