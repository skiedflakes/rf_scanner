package com.wdysolutions.www.rf_scanner.LocateEartag;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.wdysolutions.www.rf_scanner.AuditPen.AuditPen_adapter;
import com.wdysolutions.www.rf_scanner.Constant;
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.ScanEarTag.RFscanner_main;

import java.util.ArrayList;

public class LocateEartag_adapter extends RecyclerView.Adapter<LocateEartag_adapter.MyHolder>  {
    ArrayList<LocateEartag_model_pig> mdata;
    private Context context;
    private LayoutInflater inflater;
    private AuditPen_adapter.ItemClickListener mClickListener;


    public LocateEartag_adapter(Context context, ArrayList<LocateEartag_model_pig> data){
        this.context = context;
        this.mdata = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_locateeartag, parent,false);
        LocateEartag_adapter.MyHolder holder = new LocateEartag_adapter.MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        final String eartag = mdata.get(position).getSwine_code();
        int check_status = mdata.get(position).getCheck_status();

        holder.cb_check_status.setText(eartag);
        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBox(holder, position, eartag);
            }
        });

        if(check_status==1) {//if eartag is found within the pen
            holder.cb_check_status.setTextColor(Color.BLACK);
            holder.cb_check_status.setChecked(true);
            holder.cb_check_status.setClickable(false);
            holder.btn_remove.setVisibility(View.GONE);
        }else{
            holder.cb_check_status.setChecked(false);
            holder.cb_check_status.setTextColor(Color.BLACK);
            holder.cb_check_status.setClickable(false);
            holder.btn_remove.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        CheckBox cb_check_status;
        Button btn_remove;
        public MyHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this); //listener for onclick item
            cb_check_status = itemView.findViewById(R.id.cb_check_status);
            btn_remove =itemView.findViewById(R.id.btn_remove);
        }

        @Override
        public void onClick(View view) { //onclick item
            final int check_status = mdata.get(getAdapterPosition()).getCheck_status();
            if(check_status==1){ //allow to swine card if check status is 1
                final int swine_id = mdata.get(getAdapterPosition()).getSwine_id();

                Bundle bundle = new Bundle();
                bundle.putString("swine_id", String.valueOf(swine_id));
                bundle.putString("power_level", Constant.power_level);
                FragmentTransaction fragmentTransaction = ((ActivityMain) context).getSupportFragmentManager().beginTransaction();
                RFscanner_main frag = new RFscanner_main();
                frag.setArguments(bundle);
                //fragmentTransaction.setCustomAnimations(android.R.anim.fade_out, android.R.anim.slide_out_right);
                fragmentTransaction.add(R.id.container, frag);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
    }

    public void removeAt(final int position) {
        mdata.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mdata.size());
    }

    void dialogBox(final MyHolder holder, final int position, String name){
        holder.btn_remove.setEnabled(false);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage("Are you sure you want to delete "+name+"?");
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        removeAt(position);
                        holder.btn_remove.setEnabled(true);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                        holder.btn_remove.setEnabled(true);
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
