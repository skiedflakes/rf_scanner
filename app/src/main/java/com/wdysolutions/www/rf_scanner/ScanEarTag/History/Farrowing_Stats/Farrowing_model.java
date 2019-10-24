package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Farrowing_Stats;

/**
 * Created by aronandrada on 1/10/19.
 */

public class Farrowing_model {

    private int id;
    private String count;
    private String date_farrowed;
    private String born_alive;
    private String birth_mort;
    private String litter_size;
    private String boar_mated;
    private String ave_birth_wt;
    private String pre_wean_mort;
    private String post_wean_mort;
    private String num_heads_wean;
    private String num_rebreed;
    private String dry_days;
    private String gestation_days;
    private String days_weaned;
    private String breeding_failed_days;
    private String farrowing_interval;
    private String date_weaned;
    private String ave_weaning_wt;
    private String wean_wt_a;
    private String wean_wt_b;
    private String wean_wt_c;
    private String ave_weight_at_70;
    private String breeding_date;
    private String breeding_date_minus21;
    private String breeding_id;
    private String status;
    private String allow;
    private String adg;
    private String fcr;
    private String sw_abnormal;
    private String sw_undersize;
    private String sw_mummified;
    private String sw_stillbirth;
    private String condemned;
    private String litter_size_color;
    private String born_alive_color;
    private String num_heads_wean_color;

    public Farrowing_model(int id, String count, String date_farrowed, String born_alive, String birth_mort, String litter_size, String boar_mate,
                           String ave_birth_wt, String pre_wean_mort, String post_wean_mort, String num_heads_wean, String num_rebreed,
                           String dry_days, String gestation_days, String days_weaned, String breeding_failed_days, String farrowing_interval,
                           String date_weaned, String ave_weaning_wt, String wean_wt_a, String wean_wt_b, String wean_wt_c, String ave_weight_at_70,
                           String breeding_date, String breeding_id, String status, String allow, String adg, String fcr, String sw_abnormal,
                           String sw_undersize, String sw_mummified, String sw_stillbirth, String condemned, String litter_size_color,
                           String born_alive_color, String num_heads_wean_color, String breeding_date_minus21){
        this.id = id;
        this.count = count;
        this.date_farrowed = date_farrowed;
        this.born_alive = born_alive;
        this.birth_mort = birth_mort;
        this.litter_size = litter_size;
        this.boar_mated = boar_mate;
        this.ave_birth_wt = ave_birth_wt;
        this.pre_wean_mort = pre_wean_mort;
        this.post_wean_mort = post_wean_mort;
        this.num_heads_wean = num_heads_wean;
        this.num_rebreed = num_rebreed;
        this.dry_days = dry_days;
        this.gestation_days = gestation_days;
        this.days_weaned = days_weaned;
        this.breeding_failed_days = breeding_failed_days;
        this.farrowing_interval = farrowing_interval;
        this.date_weaned = date_weaned;
        this.ave_weaning_wt = ave_weaning_wt;
        this.wean_wt_a = wean_wt_a;
        this.wean_wt_b = wean_wt_b;
        this.wean_wt_c = wean_wt_c;
        this.ave_weight_at_70 = ave_weight_at_70;
        this.breeding_date = breeding_date;
        this.breeding_id = breeding_id;
        this.status = status;
        this.allow = allow;
        this.adg = adg;
        this.fcr = fcr;
        this.sw_abnormal = sw_abnormal;
        this.sw_undersize = sw_undersize;
        this.sw_mummified = sw_mummified;
        this.sw_stillbirth = sw_stillbirth;
        this.condemned = condemned;
        this.born_alive_color = born_alive_color;
        this.litter_size_color = litter_size_color;
        this.num_heads_wean_color = num_heads_wean_color;
        this.breeding_date_minus21 = breeding_date_minus21;
    }

    public String getBreeding_date_minus21() {
        return breeding_date_minus21;
    }

    public String getBorn_alive_color() {
        return born_alive_color;
    }

    public String getLitter_size_color() {
        return litter_size_color;
    }

    public String getNum_heads_wean_color() {
        return num_heads_wean_color;
    }

    public String getCount() {
        return count;
    }

    public int getId() {
        return id;
    }

    public String getLitter_size() {
        return litter_size;
    }

    public String getDate_farrowed() {
        return date_farrowed;
    }

    public String getAve_birth_wt() {
        return ave_birth_wt;
    }

    public String getDry_days() {
        return dry_days;
    }

    public String getBreeding_date() {
        return breeding_date;
    }

    public String getAve_weaning_wt() {
        return ave_weaning_wt;
    }

    public String getBirth_mort() {
        return birth_mort;
    }

    public String getStatus() {
        return status;
    }

    public String getAve_weight_at_70() {
        return ave_weight_at_70;
    }

    public String getBoar_mated() {
        return boar_mated;
    }

    public String getBorn_alive() {
        return born_alive;
    }

    public String getAdg() {
        return adg;
    }

    public String getAllow() {
        return allow;
    }

    public String getBreeding_failed_days() {
        return breeding_failed_days;
    }

    public String getBreeding_id() {
        return breeding_id;
    }

    public String getCondemned() {
        return condemned;
    }

    public String getDate_weaned() {
        return date_weaned;
    }

    public String getDays_weaned() {
        return days_weaned;
    }

    public String getFarrowing_interval() {
        return farrowing_interval;
    }

    public String getFcr() {
        return fcr;
    }

    public String getGestation_days() {
        return gestation_days;
    }

    public String getNum_heads_wean() {
        return num_heads_wean;
    }

    public String getNum_rebreed() {
        return num_rebreed;
    }

    public String getPost_wean_mort() {
        return post_wean_mort;
    }

    public String getPre_wean_mort() {
        return pre_wean_mort;
    }

    public String getSw_abnormal() {
        return sw_abnormal;
    }

    public String getSw_mummified() {
        return sw_mummified;
    }

    public String getSw_stillbirth() {
        return sw_stillbirth;
    }

    public String getSw_undersize() {
        return sw_undersize;
    }

    public String getWean_wt_a() {
        return wean_wt_a;
    }

    public String getWean_wt_b() {
        return wean_wt_b;
    }

    public String getWean_wt_c() {
        return wean_wt_c;
    }

}
