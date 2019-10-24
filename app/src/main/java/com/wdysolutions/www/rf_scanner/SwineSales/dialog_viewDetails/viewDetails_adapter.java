package com.wdysolutions.www.rf_scanner.SwineSales.dialog_viewDetails;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wdysolutions.www.rf_scanner.R;

import java.util.ArrayList;

/**
 * Created by aronandrada on 3/27/19.
 */

public class viewDetails_adapter extends RecyclerView.Adapter<viewDetails_adapter.MyHolder> {

    ArrayList<viewDetails_model> mdata;
    private Context context;
    private LayoutInflater inflater;


    public viewDetails_adapter(Context context,ArrayList<viewDetails_model> data){
        this.context = context;
        this.mdata = data;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = inflater.inflate(R.layout.swine_sales_viewdetails_container,parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, final int position) {
        final String getPackaging_id = mdata.get(position).getPackaging_id();
        final String getPrice_kg = mdata.get(position).getPrice_kg();
        final String getAge = mdata.get(position).getAge();
        final String getPrice = mdata.get(position).getPrice();
        final String getStock_id = mdata.get(position).getStock_id();
        final String getSub_total = mdata.get(position).getSub_total();
        final String getTotal_feed_comsumption = mdata.get(position).getTotal_feed_comsumption();
        final String getWeight = mdata.get(position).getWeight();

        myHolder.tv_age.setText(getAge);
        myHolder.tv_eartag.setText(getStock_id);
        myHolder.tv_feed.setText(getTotal_feed_comsumption);
        myHolder.tv_price.setText(getPrice_kg);
        myHolder.tv_subtotal.setText(getSub_total);
        myHolder.tv_weight.setText(getWeight);
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView tv_eartag,tv_age,tv_feed,tv_weight,tv_price,tv_subtotal;

        public MyHolder(View itemView) {
            super(itemView);
            tv_eartag = itemView.findViewById(R.id.tv_eartag);
            tv_age = itemView.findViewById(R.id.tv_age);
            tv_feed = itemView.findViewById(R.id.tv_feed);
            tv_weight = itemView.findViewById(R.id.tv_weight);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_subtotal =itemView.findViewById(R.id.tv_subtotal);
        }

    }
}
