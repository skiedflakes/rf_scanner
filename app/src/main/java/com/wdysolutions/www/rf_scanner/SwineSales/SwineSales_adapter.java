package com.wdysolutions.www.rf_scanner.SwineSales;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.wdysolutions.www.rf_scanner.R;

import java.util.ArrayList;

public class SwineSales_adapter extends RecyclerView.Adapter<SwineSales_adapter.MyHolder> {
    ArrayList<SwineSales_model> mdata;
    private Context context;
    private LayoutInflater inflater;
    EventListener listener;


    public SwineSales_adapter(Context context,ArrayList<SwineSales_model> data,EventListener listener){
            this.context = context;
            this.mdata = data;
            this.listener=listener;
            inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = inflater.inflate(R.layout.row_swinesales,parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, final int position) {
        final String id = mdata.get(position).getId();
        final String delivery_number = mdata.get(position).getDelivery_number();
        final String invoice = mdata.get(position).getInvoice_no();
        final String payment_type = mdata.get(position).getPay_type();
        final String date = mdata.get(position).getDate_added();
        final String customer = mdata.get(position).getCustomer();
        final String tv_total = mdata.get(position).getTotal_amount();
        final String remarks = mdata.get(position).getRemarks();
        final String status = mdata.get(position).getStatus();
        final String tr_swine =mdata.get(position).getTrSwine();
        final String tr_swine_a= mdata.get(position).getTrSwine_a();
        final String tr_swine_e =mdata.get(position).getTrSwine_e();
        final String discount = mdata.get(position).getDiscount();
        final int checked_status = mdata.get(position).getChecked_status();

        if(checked_status==0){
            myHolder.cb_selected.setChecked(false);
        }else{
            myHolder.cb_selected.setChecked(true);
        }

        myHolder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myHolder.btn_edit.setEnabled(false);
                listener.onEvent(status,
                        delivery_number,
                        invoice,
                        payment_type,
                        date,
                        customer,
                        tv_total,
                        remarks,
                        tr_swine,
                        tr_swine_a,
                        tr_swine_e,
                        discount,id);
            }
        });

        myHolder.tv_delivery_number.setText(delivery_number);
        myHolder.tv_invoice.setText(invoice);
        myHolder.tv_payment.setText(payment_type);
        myHolder.tv_date.setText(date);
        myHolder.tv_customer.setText(customer);
        myHolder.tv_total.setText(tv_total);
        myHolder.tv_remarks.setText(remarks);
        myHolder.tv_status.setText(status);

        myHolder.tv_delivery_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, String.valueOf(checked_status), Toast.LENGTH_SHORT).show();
            }
        });


        if (status.equals("Paid")){
            //myHolder.cb_selected.setVisibility(View.INVISIBLE);
            myHolder.cb_selected.setEnabled(false);
            myHolder.tv_status.setTextColor(context.getResources().getColor(R.color.btn_blue_color1));
        } else if (status.equals("Finished")){
            myHolder.tv_status.setTextColor(context.getResources().getColor(R.color.btn_color1));
       //     myHolder.cb_selected.setVisibility(View.INVISIBLE);
            myHolder.cb_selected.setEnabled(false);
        } else if (status.equals("Cancelled")){
            myHolder.tv_status.setTextColor(context.getResources().getColor(R.color.btn_light_red_color1));
          //  myHolder.cb_selected.setVisibility(View.INVISIBLE);
            myHolder.cb_selected.setEnabled(false);
        }else{
            myHolder.cb_selected.setEnabled(true);
          //  myHolder.cb_selected.setVisibility(View.VISIBLE);

            myHolder.cb_selected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(myHolder.cb_selected.isChecked()){

                        listener.onChecked(id);

                    }else{
                        listener.removeChecked(id);
                    }
                }
            });
        }
    }
    
    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView tv_delivery_number,tv_invoice,tv_payment,tv_date,tv_customer,tv_total,tv_remarks,tv_status,btn_edit;
        CheckBox cb_selected;
        public MyHolder(View itemView) {
            super(itemView);
            tv_delivery_number = itemView.findViewById(R.id.tv_delivery_number);
            tv_invoice = itemView.findViewById(R.id.tv_invoice);
            tv_payment = itemView.findViewById(R.id.tv_payment);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_customer = itemView.findViewById(R.id.tv_customer);
            tv_total =itemView.findViewById(R.id.tv_total);
            tv_remarks = itemView.findViewById(R.id.tv_remarks);
            tv_status=itemView.findViewById(R.id.tv_status);
            btn_edit = itemView.findViewById(R.id.btn_edit);
            cb_selected = itemView.findViewById(R.id.cb_selected);

        }
    }

    public interface EventListener {
        void onEvent(String status, String delivery_number, String invoice, String payment_type, String date,
                     String customer, String tv_total, String remarks, String tr_swine, String tr_swine_a,
                     String tr_swine_e, String discount, String id);
        void onChecked(String dr_header_id);
        void removeChecked(String dr_header_id);
    }
}
