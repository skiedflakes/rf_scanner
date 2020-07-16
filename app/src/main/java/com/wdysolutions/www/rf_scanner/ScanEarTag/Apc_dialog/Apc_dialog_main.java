package com.wdysolutions.www.rf_scanner.ScanEarTag.Apc_dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wdysolutions.www.rf_scanner.AppController;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Feeding.Feeding_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Feeding.Feeding_model;
import com.wdysolutions.www.rf_scanner.ScanEarTag.RFscanner_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.RFscanner_piglets_add_model;
import com.wdysolutions.www.rf_scanner.ScanEarTag.RFscanner_piglets_model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class Apc_dialog_main extends DialogFragment {

    RecyclerView recyclerView;
    ProgressBar loading_save, progressBar;
    Button btn_close;
    LinearLayout layout_;
    String company_id, swine_id, company_code, category_id;
    ArrayList<apc_model> apc_models = new ArrayList<>();


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.apc_dialog_main, container, false);

        swine_id = getArguments().getString("swine_id");
        category_id = getArguments().getString("category_id");
        company_id = getArguments().getString("company_id");
        company_code = getArguments().getString("company_code");

        recyclerView = view.findViewById(R.id.recyclerView);
        loading_save = view.findViewById(R.id.loading_save);
        progressBar = view.findViewById(R.id.progressBar);
        btn_close = view.findViewById(R.id.btn_close);
        layout_ = view.findViewById(R.id.layout_);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        getAPC(company_code, company_id, swine_id);
        return view;
    }

    public void getAPC(final String company_code, final String company_id, final String swine_id) {
        String URL = getString(R.string.URL_online)+"scan_eartag/get_swine_apc2.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    progressBar.setVisibility(View.GONE);
                    layout_.setVisibility(View.VISIBLE);

                    apc_models.add(new apc_model("Module", "Amount", "Date Added"));

                    JSONObject Object = new JSONObject(response);
                    JSONArray details = Object.getJSONArray("data");
                    for (int i=0; i<details.length(); i++) {
                        JSONObject r = details.getJSONObject(i);

                        if (r.getString("product").equals("Sow Cost") && r.getString("amount").equals("0.00")){

                        } else if (r.getString("product").equals("Adoption Cost") && r.getString("amount").equals("0.00")){

                        } else {
                            apc_models.add(new apc_model(r.getString("product"),
                                    r.getString("amount"),
                                    r.getString("date")));
                        }
                    }

                    apc_adapter adapter = new apc_adapter(getContext(), apc_models);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setAdapter(adapter);


                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    dismiss();
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("category_id", category_id);
                hashMap.put("swine_id", swine_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private class apc_adapter extends RecyclerView.Adapter<apc_adapter.MyHolder>{
        ArrayList<apc_model> data;
        private Context context;
        private LayoutInflater inflater;
        int num;

        public apc_adapter(Context context, ArrayList<apc_model> data){
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.apc_dialog_container, parent,false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            final String getAmount = data.get(position).getAmount();
            final String getDate = data.get(position).getDate();
            final String getProduct = data.get(position).getProduct();
            num = position;
            num++;

            holder.txt_date.setText(getDate);
            holder.txt_amount.setText(getAmount);
            holder.txt_module.setText(getProduct);

            if (getProduct.equals("Module")){
                holder.txt_module.setBackgroundColor(getResources().getColor(R.color.light_grey));
            }
            if (getDate.equals("Date Added")){
                holder.txt_date.setBackgroundColor(getResources().getColor(R.color.light_grey));
            }
            if (getAmount.equals("Amount")){
                holder.txt_amount.setBackgroundColor(getResources().getColor(R.color.light_grey));
            }
            if (getProduct.equals("TOTAL")){
                holder.bg_.setBackgroundColor(getResources().getColor(R.color.light_grey));
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            TextView txt_module, txt_amount, txt_date;
            LinearLayout bg_;
            public MyHolder(View itemView) {
                super(itemView);
                txt_module = itemView.findViewById(R.id.txt_module);
                txt_amount = itemView.findViewById(R.id.txt_amount);
                txt_date = itemView.findViewById(R.id.txt_date);
                bg_ = itemView.findViewById(R.id.bg_);
            }
        }
    }
}
