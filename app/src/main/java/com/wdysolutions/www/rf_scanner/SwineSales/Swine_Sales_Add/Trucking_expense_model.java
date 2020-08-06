package com.wdysolutions.www.rf_scanner.SwineSales.Swine_Sales_Add;

public class Trucking_expense_model {

    String trucking_expense_id;
    String trucking_expense;
    String status;

    public Trucking_expense_model(String trucking_expense,String trucking_expense_id,String status){
        this.trucking_expense=trucking_expense;
        this.trucking_expense_id=trucking_expense_id;
        this.status = status;
    }

    public String getTrucking_expense() {
        return trucking_expense;
    }

    public String getTrucking_expense_id() {
        return trucking_expense_id;
    }

    public String getStatus() {
        return status;
    }
}
