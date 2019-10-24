package com.wdysolutions.www.rf_scanner.SwineSales;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.SwineSales.dialog_viewDetails.viewDetails_model;

import java.util.ArrayList;

/**
 * Created by aronandrada on 3/28/19.
 */

public class SwineSales_add_adapter extends RecyclerView.Adapter<SwineSales_add_adapter.MyHolder>{

    ArrayList<viewDetails_model> mdata;
    private Context context;
    private LayoutInflater inflater;
    int count =0;
    buttonListener buttonListener;


    public SwineSales_add_adapter(Context context,ArrayList<viewDetails_model> data, buttonListener buttonListener){
        this.context = context;
        this.mdata = data;
        this.buttonListener = buttonListener;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = inflater.inflate(R.layout.swine_sales_add_container,parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, final int position) {
        final String getPackaging_id = mdata.get(position).getPackaging_id();
        final String getPrice_kg = mdata.get(position).getPrice_kg();
        final String getPrice = mdata.get(position).getPrice();
        final String getStock_id = mdata.get(position).getStock_id();
        final String getSub_total = mdata.get(position).getSub_total();
        final String getWeight = mdata.get(position).getWeight();
        final String getId = mdata.get(position).getId();
        count = position;
        count++;

        myHolder.tv_num.setText(String.valueOf(count));
        myHolder.tv_eartag.setText(getStock_id);
        myHolder.tv_price.setText(getPrice_kg);
        myHolder.tv_subtotal.setText(getSub_total);
        myHolder.tv_weight.setText(getWeight);

        myHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = String.valueOf("Are you sure you wan't to delete "+getStock_id+" ?");
                dialogBox_msg(position, msg, getId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView tv_eartag,tv_num,tv_weight,tv_price,tv_subtotal;
        ImageView delete, finish;
        public MyHolder(View itemView) {
            super(itemView);
            delete = itemView.findViewById(R.id.delete);
            tv_eartag = itemView.findViewById(R.id.tv_eartag);
            tv_num = itemView.findViewById(R.id.tv_num);
            tv_weight = itemView.findViewById(R.id.tv_weight);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_subtotal =itemView.findViewById(R.id.tv_subtotal);

        }
    }

    void dialogBox_msg(final int position, String msg, final String id){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setNegativeButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        buttonListener.passData(position, id);
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public interface buttonListener {
        void passData(int position, String id);
    }

}
