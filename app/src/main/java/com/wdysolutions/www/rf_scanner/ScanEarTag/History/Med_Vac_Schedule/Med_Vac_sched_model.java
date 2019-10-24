package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Med_Vac_Schedule;

public class Med_Vac_sched_model {

    private String med_vacc_sched_id, prod_name, dosage, disease, date, check_status;

    public Med_Vac_sched_model(String med_vacc_sched_id, String prod_name, String dosage, String disease, String date, String check_status){
        this.med_vacc_sched_id = med_vacc_sched_id;
        this.prod_name = prod_name;
        this.dosage = dosage;
        this.disease = disease;
        this.date = date;
        this.check_status = check_status;
    }

    public String getCheck_status() {
        return check_status;
    }

    public String getDate() {
        return date;
    }

    public String getDisease() {
        return disease;
    }

    public String getDosage() {
        return dosage;
    }

    public String getMed_vacc_sched_id() {
        return med_vacc_sched_id;
    }

    public String getProd_name() {
        return prod_name;
    }
}
