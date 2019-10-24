package com.wdysolutions.www.rf_scanner.Medication_Vaccination;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.wdysolutions.www.rf_scanner.AuditPen.AuditPen_adapter;
import com.wdysolutions.www.rf_scanner.LocateEartag.LocateEartag_adapter;
import com.wdysolutions.www.rf_scanner.LocateEartag.LocateEartag_model_pig;
import com.wdysolutions.www.rf_scanner.Medication_Vaccination.Dialog_apply.Med_Vac_locate_main;
import com.wdysolutions.www.rf_scanner.R;

import java.util.ArrayList;

/**
 * Created by aronandrada on 3/1/19.
 */

public class Med_Vac_adapter extends RecyclerView.Adapter<Med_Vac_adapter.MyHolder>  {

    ArrayList<Med_Vac_model_pig> mdata;
    private Context context;
    private LayoutInflater inflater;
    private ItemClickListener mClickListener;
    EventListener listener;
    int count;


    public Med_Vac_adapter(Context context, ArrayList<Med_Vac_model_pig> data, EventListener listener){
        this.context = context;
        this.mdata = data;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.med_vac_container, parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        final String getSwine_id = mdata.get(position).getSwine_id();
        final String getSwine_code = mdata.get(position).getSwine_code();
        final String getStatus = mdata.get(position).getStatus();
        count = position;
        count++;

        holder.txt.setText(String.valueOf(getSwine_code));
        if (getStatus.equals("0")){
            holder.txt.setTextColor(Color.RED);
            holder.btn_apply.setVisibility(View.VISIBLE);
        } else {
            holder.txt.setTextColor(Color.BLACK);
            holder.btn_apply.setVisibility(View.GONE);
        }

        holder.btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEvent(getSwine_id, getSwine_code, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView txt;
        Button btn_apply;
        public MyHolder(View itemView) {
            super(itemView);
            btn_apply = itemView.findViewById(R.id.btn_apply);
            txt = itemView.findViewById(R.id.txt);
        }
    }


    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface EventListener {
        void onEvent(String swine_id, String swine_code, int position);
    }


}
