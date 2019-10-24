package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Birth_Mortality;

/**
 * Created by aronandrada on 1/10/19.
 */

public class birthMortality_model {

    private int id;
    private String count;
    private String date_farrowed;
    private String litter_size;
    private String nummified_count;
    private String nummified_stillbirth;

    public birthMortality_model(int id, String count, String date_farrowed, String litter_size, String nummified_count, String nummified_stillbirth){
        this.id = id;
        this.count = count;
        this.date_farrowed = date_farrowed;
        this.litter_size = litter_size;
        this.nummified_count = nummified_count;
        this.nummified_stillbirth = nummified_stillbirth;
    }

    public String getCount() {
        return count;
    }

    public String getDate_farrowed() {
        return date_farrowed;
    }

    public String getLitter_size() {
        return litter_size;
    }

    public String getNummified_count() {
        return nummified_count;
    }

    public int getId() {
        return id;
    }

    public String getNummified_stillbirth() {
        return nummified_stillbirth;
    }
}
