package com.wdysolutions.www.rf_scanner.Feeding.Add.DialogMultiSelectSpinner;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wdysolutions.www.rf_scanner.Feeding.Add.Feeding_model_eartag;
import com.wdysolutions.www.rf_scanner.Feeding.Add.Feeding_model_selected;
import com.wdysolutions.www.rf_scanner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Feeding_module_multiselect extends DialogFragment {

    ArrayList<Feeding_model_eartag> feeding_model_eartag = new ArrayList<>();
    eartagAdapter Adapter;
    checkboxSelected checkboxSelected;
    CheckBox checkBox2;
    TextView tv_result;


    public interface checkboxSelected{
        void selected(ArrayList<Feeding_model_eartag> arraySelected);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feeding_module_multiselect, container, false);

        Button btn_select = view.findViewById(R.id.btn_select);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        tv_result = view.findViewById(R.id.tv_result);
        checkBox2 = view.findViewById(R.id.checkBox2);
        String eartag_list = getArguments().getString("array");
        checkboxSelected = (checkboxSelected)getTargetFragment();

        try {
            feeding_model_eartag.clear();
            JSONArray jsonArray = new JSONArray(eartag_list);
            for (int i=0; i<jsonArray.length(); i++){
                JSONObject j = (JSONObject)jsonArray.get(i);
                feeding_model_eartag.add(new Feeding_model_eartag(j.getString("swine_id"),
                        j.getString("swine_name"),
                        j.getString("checkStatus"),
                        j.getBoolean("isCheckAll")));

                if (j.getBoolean("isCheckAll")){
                    checkBox2.setChecked(true);
                    checkBox2.setText("Deselect All");
                }
            }


            if (jsonArray.length() == 0){
                btn_select.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                tv_result.setVisibility(View.VISIBLE);
                tv_result.setText("No data found");
            } else {
                checkBox2.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                tv_result.setVisibility(View.GONE);

                Adapter = new eartagAdapter(getContext(), feeding_model_eartag);
                RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager1);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(Adapter);
            }

        } catch (JSONException e){}


        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox2.isChecked()){
                    selectAll("1");
                    checkBox2.setText("Deselect All");
                } else {
                    selectAll("0");
                    checkBox2.setText("Select All");
                }
            }
        });

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkboxSelected.selected(feeding_model_eartag);
                dismiss();
            }
        });

        return view;
    }

    private void selectAll(String check){
        for (int i=0; i<feeding_model_eartag.size(); i++){
            Feeding_model_eartag data = feeding_model_eartag.get(i);

            boolean isSelectAll;
            String setCheck;
            if (check.equals("1")){
                setCheck = "1";
                isSelectAll = true;
            } else {
                setCheck = "0";
                isSelectAll = false;
            }
            feeding_model_eartag.set(i, new Feeding_model_eartag(data.getSwine_id(),
                    data.getSwine_name(),
                    setCheck,
                    isSelectAll));
        }
        Adapter.notifyDataSetChanged();
    }

    private class eartagAdapter extends RecyclerView.Adapter<eartagAdapter.MyHolder>{
        ArrayList<Feeding_model_eartag> data;
        private Context context;
        private LayoutInflater inflater;


        public eartagAdapter(Context context, ArrayList<Feeding_model_eartag> data){
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.feeding_module_multiselect_container, parent,false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            final String getSwine_id = data.get(position).getSwine_id();
            final String getSwine_name = data.get(position).getSwine_name();
            final String getCheckStatus = data.get(position).getCheckStatus();
            final boolean getisCheckAll = data.get(position).getisCheckAll();

            if (getCheckStatus.equals("1")){ holder.checkBox.setChecked(true); }
            else { holder.checkBox.setChecked(false); }

            holder.checkBox.setText(getSwine_name);
            holder.btn_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String check;
                    if (getCheckStatus.equals("1")){
                        check = "0";
                        holder.checkBox.setChecked(false);
                    } else {
                        check = "1";
                        holder.checkBox.setChecked(true);
                    }

                    feeding_model_eartag.set(position, new Feeding_model_eartag(getSwine_id,
                            getSwine_name,
                            check,
                            getisCheckAll));
                    notifyItemChanged(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            CheckBox checkBox;
            LinearLayout btn_select;
            public MyHolder(View itemView) {
                super(itemView);
                btn_select = itemView.findViewById(R.id.btn_select);
                checkBox = itemView.findViewById(R.id.checkBox);
            }
        }
    }
}
