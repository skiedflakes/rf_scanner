package com.wdysolutions.www.rf_scanner.MonthPicker;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.wdysolutions.www.rf_scanner.DatePicker.DatePickerSelectionInterfaceCustom;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.SwineSales.SwineSales_adapter;
import com.wdysolutions.www.rf_scanner.SwineSales.SwineSales_main;
import com.wdysolutions.www.rf_scanner.SwineSales.SwineSales_model;

import java.util.ArrayList;


public class monthPicker extends DialogFragment {

    public monthPickerInterface delegate = null;
    Button btn_cancel;
    RecyclerView recyclerView;
    TextView txt_title;
    ArrayList<monthPicker_model> monthPicker_models = new ArrayList<>();


    private ArrayList<monthPicker_model> getMonth(){
        monthPicker_models.add(new monthPicker_model("January", "1"));
        monthPicker_models.add(new monthPicker_model("February", "2"));
        monthPicker_models.add(new monthPicker_model("March", "3"));
        monthPicker_models.add(new monthPicker_model("April", "4"));
        monthPicker_models.add(new monthPicker_model("May", "5"));
        monthPicker_models.add(new monthPicker_model("June", "6"));
        monthPicker_models.add(new monthPicker_model("July", "7"));
        monthPicker_models.add(new monthPicker_model("August", "8"));
        monthPicker_models.add(new monthPicker_model("September", "9"));
        monthPicker_models.add(new monthPicker_model("October", "10"));
        monthPicker_models.add(new monthPicker_model("November", "11"));
        monthPicker_models.add(new monthPicker_model("December", "12"));
        return monthPicker_models;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.month_picker, container, false);

        txt_title = view.findViewById(R.id.txt_title);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        recyclerView = view.findViewById(R.id.recyclerView);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        initRecycler();
        return view;
    }

    private void initRecycler(){
        monthAdapter monthAdapter = new monthAdapter(getContext(), getMonth());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(monthAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    public class monthAdapter extends RecyclerView.Adapter<monthAdapter.MyHolder> {
        ArrayList<monthPicker_model> mdata;
        private Context context;
        private LayoutInflater inflater;

        public monthAdapter(Context context, ArrayList<monthPicker_model> data){
            this.context = context;
            this.mdata = data;
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflater.inflate(R.layout.month_picker_container,parent,false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final MyHolder myHolder, final int position) {
            final String getText = mdata.get(position).getText();
            final String getValue = mdata.get(position).getValue();

            myHolder.text_.setText(getText);
            myHolder.text_.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delegate.setMonth(getText, getValue);
                    dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mdata.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            TextView text_;
            public MyHolder(View itemView) {
                super(itemView);
                text_ = itemView.findViewById(R.id.text_);
            }
        }
    }



}
