package com.wdysolutions.www.rf_scanner.Medication_Vaccination.Dialog_apply;

/**
 * Created by aronandrada on 2/28/19.
 */

public class Med_Vac_model_product {

    private int product_id;
    private String product_name;
    private String dosage;
    private String disease_id;
    private String med_vac_sched_id;
    private int count_not_selected_action;

    public Med_Vac_model_product(int product_id, String product_name, String dosage, String disease_id, String med_vac_sched_id,
                                 int count_not_selected_action){
        this.product_id = product_id;
        this.product_name = product_name;
        this.dosage = dosage;
        this.disease_id = disease_id;
        this.med_vac_sched_id = med_vac_sched_id;
        this.count_not_selected_action = count_not_selected_action;
    }

    public int getCount_not_selected_action() {
        return count_not_selected_action;
    }

    public String getMed_vac_sched_id() {
        return med_vac_sched_id;
    }

    public String getDisease_id() {
        return disease_id;
    }

    public String getDosage() {
        return dosage;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }
}
