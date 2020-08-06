package com.wdysolutions.www.rf_scanner.SwineSales.Swine_Sales_Scan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.wdysolutions.www.rf_scanner.R;

import java.util.ArrayList;

public class SwineSales_scan_adapter extends RecyclerView.Adapter<SwineSales_scan_adapter.MyHolder> {

    ArrayList<SwineSales_scan_model> mdata;
    private Context context;
    private LayoutInflater inflater;
    EventListener listener;

    public SwineSales_scan_adapter(Context context, ArrayList<SwineSales_scan_model> data, EventListener listener){
        this.context = context;
        this.mdata = data;
        this.listener=listener;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_swine_sales_scan, parent,false);
        MyHolder holder = new MyHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        final String eartag = mdata.get(position).getSwine_code();
        final String age = mdata.get(position).getAge();
        final String feed_cons = mdata.get(position).getFeeds_cons();
        final double weight = mdata.get(position).getWeight();
        final double price = mdata.get(position).getPrice();
        final double subtotal = mdata.get(position).getSubtotal();

        holder.text_.setText(eartag);
        holder.tv_age.setText(age);
        holder.tv_feed_cons.setText(feed_cons);


        if(price!=0){
            holder.et_price.setText(String.valueOf(price));
        }
        if(weight!=0){
            holder.et_weight.setText(String.valueOf(weight));
        }
        if(subtotal!=0){
            holder.tv_total.setText(String.valueOf(subtotal));
        }

        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBox(holder,position,eartag);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView text_,tv_age,tv_feed_cons,tv_total;
        TextView btn_remove;
        EditText et_weight,et_price;


        public MyHolder(final View itemView) {
            super(itemView);
            text_ = itemView.findViewById(R.id.text_);
            btn_remove = itemView.findViewById(R.id.btn_remove);
            tv_age = itemView.findViewById(R.id.tv_age);
            tv_feed_cons = itemView.findViewById(R.id.tv_feed_cons);
            et_weight = itemView.findViewById(R.id.et_weight);
            et_price = itemView.findViewById(R.id.et_price);
            tv_total = itemView.findViewById(R.id.tv_total);


            et_weight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    try {
                        double int_weight;

                        if(!String.valueOf(s).equals("")){
                            int_weight= Double.valueOf(String.valueOf(s));
                        }else{
                            int_weight =0;
                        }

                        int position = getAdapterPosition();

                        int swine_id = mdata.get(position).getSwine_id();
                        String swine_code = mdata.get(position).getSwine_code();

                        String age = mdata.get(position).getAge();
                        String feeds_cons = mdata.get(position).getFeeds_cons();
                        double price = mdata.get(position).getPrice();
                        double subtotal = int_weight * price;
                        String delivery_number =  mdata.get(position).getDelivery_number();


                        mdata.set(position, new SwineSales_scan_model(swine_id, swine_code,age, feeds_cons, int_weight, price, subtotal,delivery_number));

                        //sqlite on update
                        listener.on_Update(String.valueOf(swine_id),String.valueOf(int_weight),String.valueOf(price),String.valueOf(subtotal));


                        tv_total.setText(String.valueOf(subtotal));
                    }catch (Exception e){}

                }
            });



            et_price.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    try {
                        double int_price;
                        int position = getAdapterPosition();
                        if(!String.valueOf(s).equals("")){
                            int_price= Double.valueOf(String.valueOf(s));
                        }else{
                            int_price =0;
                        }

                        int swine_id = mdata.get(position).getSwine_id();
                        String swine_code = mdata.get(position).getSwine_code();

                        String age = mdata.get(position).getAge();
                        String feeds_cons = mdata.get(position).getFeeds_cons();
                        double weight = mdata.get(position).getWeight();
                        double subtotal = weight * int_price;
                        String delivery_number =  mdata.get(position).getDelivery_number();

                        mdata.set(position, new SwineSales_scan_model(swine_id, swine_code, age, feeds_cons, weight, int_price, subtotal,delivery_number));

                        //sqlite on update
                        listener.on_Update(String.valueOf(swine_id),String.valueOf(weight),String.valueOf(int_price),String.valueOf(subtotal));



                        tv_total.setText(String.valueOf(subtotal));
                    }catch (Exception e){}

                }
            });
        }
    }


    public void removeAt(final int position) {
        final int swine_id = mdata.get(position).getSwine_id();
        listener.onEvent(String.valueOf(swine_id));
    }

    public interface EventListener {
        void onEvent(String swine_id);
        void on_Update(String swine_id, String weight, String price, String subtotal);
        void open_delete(boolean yes_no);
    }

    void dialogBox(final MyHolder holder,final int position, String name){
        listener.open_delete(true);
        holder.btn_remove.setEnabled(false);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage("Are you sure you want to delete "+name+"?");
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        holder.btn_remove.setEnabled(true);
                        removeAt(position);
                        listener.open_delete(false);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        holder.btn_remove.setEnabled(true);
                        dialog.cancel();
                        listener.open_delete(false);
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
