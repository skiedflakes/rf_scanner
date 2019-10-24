package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Wean;

/**
 * Created by aronandrada on 1/31/19.
 */

public class Wean_model {

    private String id;
    private String swine_code;
    private String gender;
    private String count;
    private String weight;
    private String swine_breed;
    private String genetic_line;
    private String classification;
    private String birthdate;
    private String no_days;

    public Wean_model(String id, String swine_code, String gender, String count, String weight, String swine_breed, String genetic_line,
                      String classification, String birthdate, String no_days){
        this.id = id;
        this.swine_code = swine_code;
        this.gender = gender;
        this.count = count;
        this.weight = weight;
        this.swine_breed = swine_breed;
        this.genetic_line = genetic_line;
        this.classification = classification;
        this.birthdate = birthdate;
        this.no_days = no_days;
    }

    public String getId() {
        return id;
    }

    public String getGender() {
        return gender;
    }

    public String getWeight() {
        return weight;
    }

    public String getCount() {
        return count;
    }

    public String getSwine_code() {
        return swine_code;
    }

    public String getSwine_breed() {
        return swine_breed;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getClassification() {
        return classification;
    }

    public String getGenetic_line() {
        return genetic_line;
    }

    public String getNo_days() {
        return no_days;
    }

}
