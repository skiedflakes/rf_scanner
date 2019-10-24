package com.wdysolutions.www.rf_scanner.Home;

/**
 * Created by aronandrada on 12/3/18.
 */

public class Main_menu_model {

    private int id;
    private String name;
    private String img;
    private String details;
    private int iconColor;
    private int disable_status;

    public Main_menu_model(int id, String name, String img, int iconColor, String details, int disable_status){
        this.id = id;
        this.disable_status = disable_status;
        this.name = name;
        this.img = img;
        this.iconColor = iconColor;
        this.details = details;
    }

    public int getDisable_status() {
        return disable_status;
    }

    public int getIconColor() {
        return iconColor;
    }

    public String getImg() {
        return img;
    }

    public String getDetails() {
        return details;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
