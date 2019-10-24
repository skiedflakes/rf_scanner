package com.wdysolutions.www.rf_scanner.Feeding.FeedingPerPen_Details;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wdysolutions.www.rf_scanner.Feeding.FeedingPerPen_Details.Update.Feeding_Pen_Update_main;
import com.wdysolutions.www.rf_scanner.Feeding.Feeding_model_data;
import com.wdysolutions.www.rf_scanner.Feeding.Feeding_module_main;
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.SwineSales.SwineSales_main;

import java.util.ArrayList;

public class Feeding_Pen_adapter extends RecyclerView.Adapter<Feeding_Pen_adapter.MyHolder>{
    ArrayList<Feeding_Pen_model> data;
    private Context context;
    private LayoutInflater inflater;

    public Feeding_Pen_adapter(Context context, ArrayList<Feeding_Pen_model> data){
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.feeding_pen_details_container, parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        final String getFeeding_id = data.get(position).getFeeding_id();
        final String getBtn_status = data.get(position).getBtn_status();
        final String getEar_tag = data.get(position).getEar_tag();
        final String getQuantity = data.get(position).getQuantity();
        final String getCheck_status = data.get(position).getCheck_status();
        final String getSwine_id = data.get(position).getSwine_id();

        holder.txt_count.setText(String.valueOf(position+1));
        holder.txt_ear_tag.setText(getEar_tag);
        holder.txt_feed_amount.setText(getQuantity);

        if (getCheck_status.equals("1")){
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("getFeeding_id", getFeeding_id);
                DialogFragment selectedDialogFragment = new Feeding_Pen_Update_main();
                FragmentTransaction ft = ((ActivityMain)context).getSupportFragmentManager().beginTransaction();
                Fragment prev = ((ActivityMain)context).getSupportFragmentManager().findFragmentByTag("dialog_2");
                if (prev != null) {ft.remove(prev);}
                ft.addToBackStack(null);
                selectedDialogFragment.setArguments(bundle);
                selectedDialogFragment.show(ft, "dialog_2");
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkStatus;
                if (holder.checkBox.isChecked()){
                    checkStatus = "1";
                } else {
                    checkStatus = "0";
                }
                data.set(position, new Feeding_Pen_model(getFeeding_id,
                        getEar_tag,
                        getQuantity,
                        getBtn_status,
                        getSwine_id,
                        checkStatus));
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView txt_count, txt_ear_tag, txt_feed_amount;
        CheckBox checkBox;
        ImageView btn_edit;
        public MyHolder(View itemView) {
            super(itemView);
            btn_edit = itemView.findViewById(R.id.btn_edit);
            checkBox = itemView.findViewById(R.id.checkBox);
            txt_count = itemView.findViewById(R.id.txt_count);
            txt_ear_tag = itemView.findViewById(R.id.txt_ear_tag);
            txt_feed_amount = itemView.findViewById(R.id.txt_feed_amount);
        }
    }
}
