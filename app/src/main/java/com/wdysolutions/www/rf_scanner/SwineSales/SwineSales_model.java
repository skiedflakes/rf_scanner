package com.wdysolutions.www.rf_scanner.SwineSales;

public class SwineSales_model {
   // String
    String id;
    String delivery_number;
    String pay_type;
    String invoice_no;
    String date_added;
    String customer;
    String total_amount;
    String trSwine_a;
    String trSwine;
    String trSwine_e;
    String vatSwine;
    String SwinewithHold;
    String SwinewithHold_a;
    String remarks;
    String status;
    String discount;
    int checked_status;

    public SwineSales_model(String id,
                            String delivery_number,
                            String pay_type,
                            String invoice_no,
                            String date_added,
                            String customer,
                            String total_amount,
                            String trSwine_a,
                            String trSwine,
                            String trSwine_e,
                            String vatSwine,
                            String SwinewithHold,
                            String SwinewithHold_a,
                            String remarks,
                            String status,
                            String discount,
                            int checked_status
                            ){
        this.id=id;
        this.delivery_number=delivery_number;
        this.pay_type=pay_type;
        this.invoice_no=invoice_no;
        this.date_added=date_added;
        this.customer=customer;
        this.total_amount=total_amount;
        this.trSwine_a=trSwine_a;
        this.trSwine=trSwine;
        this.trSwine_e=trSwine_e;
        this.vatSwine=vatSwine;
        this.SwinewithHold=SwinewithHold;
        this.SwinewithHold_a=SwinewithHold_a;
        this.remarks=remarks;
        this.status=status;
        this.discount = discount;
        this.checked_status=checked_status;
    }

    public String getDiscount() {
        return discount;
    }

    public String getDelivery_number() {
        return delivery_number;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getPay_type() {
        return pay_type;
    }

    public String getInvoice_no() {
        return invoice_no;
    }

    public String getDate_added() {
        return date_added;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public String getCustomer() {
        return customer;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getSwinewithHold() {
        return SwinewithHold;
    }

    public String getSwinewithHold_a() {
        return SwinewithHold_a;
    }

    public String getTrSwine() {
        return trSwine;
    }

    public String getTrSwine_a() {
        return trSwine_a;
    }

    public String getTrSwine_e() {
        return trSwine_e;
    }

    public String getVatSwine() {
        return vatSwine;

    }

    public int getChecked_status() {
        return checked_status;
    }
}
