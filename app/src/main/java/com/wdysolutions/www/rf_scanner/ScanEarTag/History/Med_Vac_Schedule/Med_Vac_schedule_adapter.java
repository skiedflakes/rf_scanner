package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Med_Vac_Schedule;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.wdysolutions.www.rf_scanner.R;

import java.util.ArrayList;

public class Med_Vac_schedule_adapter extends RecyclerView.Adapter<Med_Vac_schedule_adapter.MyHolder> {

    ArrayList<Med_Vac_sched_model> data;
    ArrayList<Med_Vac_sched_model> data_dosage;
    private Context context;
    private LayoutInflater inflater;


    public Med_Vac_schedule_adapter(Context context, ArrayList<Med_Vac_sched_model> data, ArrayList<Med_Vac_sched_model> data_dosage){
        this.context = context;
        this.data = data;
        this.data_dosage = data_dosage;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.med_vac_schedule_row, parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        final String getMed_vacc_sched_id = data.get(position).getMed_vacc_sched_id();
        final String getProd_name = data.get(position).getProd_name();
        final String getDate = data.get(position).getDate();
        final String getDisease = data.get(position).getDisease();
        final String getDosage = data.get(position).getDosage();
        final String getCheck_status = data.get(position).getCheck_status();

        holder.edittxt_dosage.setText(getDosage);
        holder.tv_diagnos.setText(getDisease);
        holder.tv_product.setText(getProd_name);
        holder.tv_schedule_date.setText(getDate);

        if (getCheck_status.equals("1")){
            holder.chck_box.setChecked(true);
        } else {
            holder.chck_box.setChecked(false);
        }

        holder.chck_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.chck_box.isChecked()){
                    data.set(position, new Med_Vac_sched_model(getMed_vacc_sched_id,
                            getProd_name,
                            getDosage,
                            getDisease,
                            getDate,
                            "1"));
                } else {
                    data.set(position, new Med_Vac_sched_model(getMed_vacc_sched_id,
                            getProd_name,
                            getDosage,
                            getDisease,
                            getDate,
                            "0"));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        CheckBox chck_box;
        TextView tv_product, tv_diagnos, tv_schedule_date;
        EditText edittxt_dosage;
        public MyHolder(View itemView) {
            super(itemView);
            chck_box = itemView.findViewById(R.id.chck_box);
            tv_product = itemView.findViewById(R.id.tv_product);
            edittxt_dosage = itemView.findViewById(R.id.edittxt_dosage);
            tv_diagnos = itemView.findViewById(R.id.tv_diagnos);
            tv_schedule_date = itemView.findViewById(R.id.tv_schedule_date);

            edittxt_dosage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable editable) {
                    data_dosage.set(getAdapterPosition(), new Med_Vac_sched_model("",
                            "",
                            editable.toString(),
                            "",
                            "",
                            ""));
                }
            });
        }
    }
}
