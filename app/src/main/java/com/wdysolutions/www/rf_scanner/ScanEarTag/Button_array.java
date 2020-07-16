package com.wdysolutions.www.rf_scanner.ScanEarTag;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by aronandrada on 2/9/19.
 */

public class Button_array {

    Context context;

    public Button_array(Context context){
        this.context = context;
    }

    private boolean is_NIP_and_ABORT_hide, is_ConfirmPreg_hide, is_Convert_hide;

    // button array for sow
    public ArrayList setActionButton(boolean isActions, String swine_type, String pen_type, String pregnant_status, String piglets_count, String alert_parameter_status){
        ArrayList<RFscanner_buttonActions_model> buttonArraylist = new ArrayList<>();


        if (Integer.valueOf(alert_parameter_status) < 2){is_NIP_and_ABORT_hide=true;}else{is_NIP_and_ABORT_hide=false;}

        if (Integer.valueOf(alert_parameter_status) <= 1){is_ConfirmPreg_hide=false;}else{is_ConfirmPreg_hide=true;}

        // Gestating ----------------------------------------------------------------------------------------------------------------
        if(pen_type.equals("Gestating")){
            if(swine_type.equals("Sow")){
                if(pregnant_status.equals("Pregnant")){
                    if (isActions){
                        buttonArraylist.add(new RFscanner_buttonActions_model(0, "AI/Natural Breeding", "1"));
                        buttonArraylist.add(new RFscanner_buttonActions_model(1, "Breeding Failed", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(2, "Confirm Pregnant", is_ConfirmPreg_hide ? "1" : ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(3, "Culling", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(4, "Abort", is_NIP_and_ABORT_hide ? "1" : ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(5, "Not in Pig", is_NIP_and_ABORT_hide ? "1" : ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(6, "Transfer Pen", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(7, "Mortality", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(8, "Add Schedule for Culling", ""));
                    } else {
                        buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(1, "Mated History", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(2, "Farrowing Stats", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(3, "Birth Mortality", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(4, "AI Materials/Semen", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(5, "Medication", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(6, "Vaccination", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(7, "Feeding", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(8, "Remarks", ""));
                    }
                }
                else {
                    if (isActions){
                        buttonArraylist.add(new RFscanner_buttonActions_model(0, "AI/Natural Breeding", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(1, "Breeding Failed", "1"));
                        buttonArraylist.add(new RFscanner_buttonActions_model(2, "Confirm Pregnant", "1"));
                        buttonArraylist.add(new RFscanner_buttonActions_model(3, "Culling", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(4, "Abort", "1"));
                        buttonArraylist.add(new RFscanner_buttonActions_model(5, "Not in Pig", "1"));
                        buttonArraylist.add(new RFscanner_buttonActions_model(6, "Transfer Pen", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(7, "Mortality", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(8, "Add Schedule for Culling", ""));
                    } else {
                        buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(1, "Mated History", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(2, "Farrowing Stats", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(3, "Birth Mortality", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(4, "AI Materials/Semen", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(5, "Medication", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(6, "Vaccination", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(7, "Feeding", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(8, "Remarks", ""));
                    }
                }
            }
            else {
                if (isActions){
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "AI/Natural Breeding", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Breeding Failed", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Confirm Pregnant", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Culling", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "Abort", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(5, "Not in Pig", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(6, "Transfer Pen", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(7, "Mortality", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(8, "Schedule for Culling", "1"));
                } else {
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Medication", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Vaccination", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Feeding", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "Remarks", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(5, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(6, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(7, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(8, "", ""));
                }
            }
        }

        // Farrowing ----------------------------------------------------------------------------------------------------------------
        else if (pen_type.equals("Farrowing")){
            if (pregnant_status.equals("Pregnant")){
                if (isActions){
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Transfer Pen", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Culling", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Transfer All to Farrowing", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Mortality", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "Wean", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(5, "Farrow", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(6, "Abort", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(7, "Not in Pig", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(8, "Add Schedule for Culling", ""));
                } else {
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Mated History", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Farrowing Stats", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Birth Mortality", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "AI Materials/Semen", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(5, "Medication", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(6, "Vaccination", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(7, "Feeding", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(8, "Remarks", ""));
                }
            }
            else if (swine_type.equals("Sow")){
                if (Integer.valueOf(piglets_count) > 0){
                    if (isActions){
                        buttonArraylist.add(new RFscanner_buttonActions_model(0, "Transfer Pen", "1"));
                        buttonArraylist.add(new RFscanner_buttonActions_model(1, "Culling", "1"));
                        buttonArraylist.add(new RFscanner_buttonActions_model(2, "Transfer All to Farrowing", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(3, "Mortality", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(4, "Wean", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(5, "Farrow", "1"));
                        buttonArraylist.add(new RFscanner_buttonActions_model(6, "Abort", "1"));
                        buttonArraylist.add(new RFscanner_buttonActions_model(7, "Not in Pig", "1"));
                        buttonArraylist.add(new RFscanner_buttonActions_model(8, "Add Schedule for Culling", ""));
                    } else {
                        buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(1, "Mated History", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(2, "Farrowing Stats", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(3, "Birth Mortality", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(4, "AI Materials/Semen", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(5, "Medication", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(6, "Vaccination", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(7, "Feeding", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(8, "Remarks", ""));
                    }
                }
                else {
                    if (isActions){
                        buttonArraylist.add(new RFscanner_buttonActions_model(0, "Transfer Pen", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(1, "Culling", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(2, "Transfer All to Farrowing", "1"));
                        buttonArraylist.add(new RFscanner_buttonActions_model(3, "Mortality", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(4, "Wean", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(5, "Farrow", "1"));
                        buttonArraylist.add(new RFscanner_buttonActions_model(6, "Abort", "1"));
                        buttonArraylist.add(new RFscanner_buttonActions_model(7, "Not in Pig", "1"));
                        buttonArraylist.add(new RFscanner_buttonActions_model(8, "Add Schedule for Culling", ""));
                    } else {
                        buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(1, "Mated History", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(2, "Farrowing Stats", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(3, "Birth Mortality", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(4, "AI Materials/Semen", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(5, "Medication", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(6, "Vaccination", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(7, "Feeding", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(8, "Remarks", ""));
                    }
                }
            }
            else if (swine_type.equals("Piglet")){
                if (isActions){
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Transfer Pen", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Culling", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Transfer All to Farrowing", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Mortality", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "Wean", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(5, "Farrow", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(6, "Abort", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(7, "Not in Pig", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(8, "Add Schedule for Culling", "1"));
                } else {
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Medication", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Vaccination", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Feeding", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "Remarks", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(5, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(6, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(7, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(8, "", ""));
                }
            }
            else if (swine_type.equals("Grower")){
                if (isActions){
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Transfer Pen", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Culling", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Transfer All to Farrowing", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Mortality", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "Wean", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(5, "Farrow", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(6, "Abort", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(7, "Not in Pig", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(8, "Schedule for Culling", "1"));
                } else {
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Medication", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Vaccination", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Feeding", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "Remarks", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(5, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(6, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(7, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(8, "", ""));
                }
            }
        }

        // Dry ----------------------------------------------------------------------------------------------------------------
        else if (pen_type.equals("Dry")){
            if (swine_type.equals("Gilt")){
                if (isActions){
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "AI/Natural Breeding", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Culling", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Transfer Pen", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Mortality", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "Schedule for Culling", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(5, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(6, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(7, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(8, "", ""));
                } else {
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Mated History", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Farrowing Stats", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Birth Mortality", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "AI Materials/Semen", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(5, "Medication", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(6, "Vaccination", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(7, "Feeding", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(8, "Remarks", ""));
                }
            }
            else if (swine_type.equals("Senior-boar") || swine_type.equals("Junior-boar") || swine_type.equals("Weaner")){
                if (isActions){
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "AI/Natural Breeding", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Culling", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Transfer Pen", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Mortality", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "Schedule for Culling", "1"));
                } else {
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Medication", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Vaccination", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Feeding", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "Remarks", ""));
                }
            }
            else if (swine_type.equals("Sow")){
                if(pregnant_status.equals("Pregnant")){
                    if (isActions){
                        buttonArraylist.add(new RFscanner_buttonActions_model(0, "AI/Natural Breeding", "1"));
                        buttonArraylist.add(new RFscanner_buttonActions_model(1, "Culling", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(2, "Transfer Pen", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(3, "Mortality", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(4, "Add Schedule for Culling", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(5, "", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(6, "", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(7, "", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(8, "", ""));
                    } else {
                        buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(1, "Mated History", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(2, "Farrowing Stats", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(3, "Birth Mortality", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(4, "AI Materials/Semen", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(5, "Medication", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(6, "Vaccination", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(7, "Feeding", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(8, "Remarks", ""));
                    }
                }
                else {
                    if (isActions){
                        buttonArraylist.add(new RFscanner_buttonActions_model(0, "AI/Natural Breeding", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(1, "Culling", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(2, "Transfer Pen", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(3, "Mortality", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(4, "Add Schedule for Culling", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(5, "", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(6, "", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(7, "", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(8, "", ""));
                    } else {
                        buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(1, "Mated History", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(2, "Farrowing Stats", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(3, "Birth Mortality", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(4, "AI Materials/Semen", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(5, "Medication", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(6, "Vaccination", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(7, "Feeding", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(8, "Remarks", ""));
                    }
                }
            }
            else if (swine_type.equals("Finisher")){
                if (isActions){
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "AI/Natural Breeding", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Culling", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Transfer Pen", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Mortality", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "Schedule for Culling", "1"));
                } else {
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Medication", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Vaccination", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Feeding", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "Remarks", ""));
                }
            }
        }

        // Nursery ----------------------------------------------------------------------------------------------------------------
        else if (pen_type.equals("Nursery")){
            if (swine_type.equals("Finisher")){
                if (isActions){
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Transfer Pen", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Mortality", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Schedule for Culling", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "", ""));
                } else {
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Medication", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Vaccination", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Feeding", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "Remarks", ""));
                }
            }
            else if (swine_type.equals("Sow")){
                if (pregnant_status.equals("Pregnant")){
                    if (isActions){
                        buttonArraylist.add(new RFscanner_buttonActions_model(0, "Transfer Pen", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(1, "Mortality", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(2, "Add Schedule for Culling", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(3, "", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(4, "", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(5, "", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(6, "", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(7, "", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(8, "", ""));
                    } else {
                        buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(1, "Mated History", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(2, "Farrowing Stats", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(3, "Birth Mortality", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(4, "AI Materials/Semen", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(5, "Medication", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(6, "Vaccination", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(7, "Feeding", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(8, "Remarks", ""));
                    }
                }
                else {
                    if (isActions){
                        buttonArraylist.add(new RFscanner_buttonActions_model(0, "Transfer Pen", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(1, "Mortality", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(2, "Add Schedule for Culling", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(3, "", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(4, "", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(5, "", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(6, "", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(7, "", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(8, "", ""));
                    } else {
                        buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(1, "Mated History", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(2, "Farrowing Stats", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(3, "Birth Mortality", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(4, "AI Materials/Semen", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(5, "Medication", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(6, "Vaccination", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(7, "Feeding", ""));
                        buttonArraylist.add(new RFscanner_buttonActions_model(8, "Remarks", ""));
                    }
                }
            }
            else {
                if (isActions){
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Transfer Pen", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Mortality", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Schedule for Culling", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "", ""));
                } else {
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Medication", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Vaccination", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Feeding", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "Remarks", ""));
                }
            }
        }

        // Growout ----------------------------------------------------------------------------------------------------------------
        else if (pen_type.equals("Growout")){
            if (swine_type.equals("Finisher")){
                if (isActions){
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Convert to Gilt/Boar", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Transfer Pen", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Mortality", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Schedule for Culling", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "", ""));
                } else {
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Medication", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Vaccination", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Feeding", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "Remarks", ""));
                }
            }
            else if (swine_type.equals("Sow")){
                if (isActions){
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Convert to Gilt/Boar", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Transfer Pen", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Mortality", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Add Schedule for Culling", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(5, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(6, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(7, "", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(8, "", ""));
                } else {
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Mated History", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Farrowing Stats", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Birth Mortality", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "AI Materials/Semen", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(5, "Medication", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(6, "Vaccination", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(7, "Feeding", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(8, "Remarks", ""));
                }
            }
            else {
                if (swine_type.equals("Senior-boar") || swine_type.equals("Junior-boar") || swine_type.equals("Gilt")){
                    is_Convert_hide = true;
                } else {
                    is_Convert_hide = false;}

                if (isActions){
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Convert to Gilt/Boar", is_Convert_hide ? "1" : ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Transfer Pen", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Mortality", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Schedule for Culling", "1"));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "", ""));
                } else {
                    buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(1, "Medication", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(2, "Vaccination", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(3, "Feeding", ""));
                    buttonArraylist.add(new RFscanner_buttonActions_model(4, "Remarks", ""));
                }
            }
        }

        // Hospital --------------------------------------------------------------------------------------------------------------------------
        else if (pen_type.equals("Hospital")){
            if (isActions){
                buttonArraylist.add(new RFscanner_buttonActions_model(0, "Transfer Pen", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(1, "Mortality", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(2, "Schedule for Culling", "1"));
                buttonArraylist.add(new RFscanner_buttonActions_model(3, "", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(4, "", ""));
            } else {
                buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(1, "Medication", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(2, "Vaccination", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(3, "Feeding", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(4, "Remarks", ""));
            }
        }

        // Deceased, Culled or Sold ----------------------------------------------------------------------------------------------------------------
        else if (pen_type.equals("Deceased") || pen_type.equals("Culled") || pen_type.equals("Sold")){
            if (swine_type.equals("Sow")){
                buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(1, "Mated History", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(2, "Farrowing Stats", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(3, "Birth Mortality", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(4, "AI Materials/Semen", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(5, "Medication", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(6, "Vaccination", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(7, "Feeding", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(8, "Remarks", ""));
            } else {
                buttonArraylist.add(new RFscanner_buttonActions_model(0, "Swine History", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(1, "Medication", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(2, "Vaccination", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(3, "Feeding", ""));
                buttonArraylist.add(new RFscanner_buttonActions_model(4, "Remarks", ""));
            }
        }
        return buttonArraylist;
    }


    // button array for piglets
    public ArrayList setActionButtonPiglets(boolean isActions){
        ArrayList<RFscanner_buttonActions_model> buttonArraylist = new ArrayList<>();

        if (isActions){
            buttonArraylist.add(new RFscanner_buttonActions_model(0, "Mortality", ""));
            buttonArraylist.add(new RFscanner_buttonActions_model(1, "Condemn", ""));
            buttonArraylist.add(new RFscanner_buttonActions_model(2, "", ""));
            buttonArraylist.add(new RFscanner_buttonActions_model(3, "", ""));
        } else {
            buttonArraylist.add(new RFscanner_buttonActions_model(0, "Home", ""));
            buttonArraylist.add(new RFscanner_buttonActions_model(1, "Medication", ""));
            buttonArraylist.add(new RFscanner_buttonActions_model(2, "Vaccination", ""));
            buttonArraylist.add(new RFscanner_buttonActions_model(3, "Feeding", ""));
        }
        return buttonArraylist;
    }


}
