package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Farrowing_Stats.Born_Alive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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
import com.google.gson.Gson;
import com.wdysolutions.www.rf_scanner.AppController;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Farrowing_Born_alive_main extends DialogFragment {

    RecyclerView recyclerView;
    ProgressBar loading_save, progressBar;
    Button btn_save;
    LinearLayout layout_add;
    ArrayList<Farrowing_Born_Alive_model> arrayList = new ArrayList<>();
    ArrayList<Farrowing_Born_Alive_addArray_model> arrayList_add = new ArrayList<>();
    bornAlive_adapter adapter;
    int counter = 0;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.farrowing_born_alive, container, false);
        final SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        final String company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        final String company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        final String farr_id = getArguments().getString("farr_id");
        final String swine_scanned_id = getArguments().getString("swine_scanned_id");

        layout_add = view.findViewById(R.id.layout_add);
        recyclerView = view.findViewById(R.id.recyclerView);
        loading_save = view.findViewById(R.id.loading_save);
        progressBar = view.findViewById(R.id.progressBar);
        btn_save = view.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges(company_code, company_id);
            }
        });

        getBornAlive(company_code, company_id, farr_id, swine_scanned_id);
        return view;
    }

    public void saveChanges(final String company_code, final String company_id) {
        btn_save.setVisibility(View.GONE);
        loading_save.setVisibility(View.VISIBLE);
        String URL = getString(R.string.URL_online)+"scan_eartag/history/pig_updateSwineCode.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    btn_save.setVisibility(View.VISIBLE);
                    loading_save.setVisibility(View.GONE);

                    String swine_code = "";
                    JSONObject Object = new JSONObject(response);
                    JSONArray details = Object.getJSONArray("data");
                    for(int i = 0; i < details.length(); i++){
                        JSONObject r = details.getJSONObject(i);

                        swine_code += r.getString("status")+" \n";
                    }

                    dialogBox(swine_code);
                    
                } catch (JSONException e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    btn_save.setVisibility(View.VISIBLE);
                    loading_save.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("born_alive_list", new Gson().toJson(arrayList_add));
                hashMap.put("counter", String.valueOf(counter));
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void getBornAlive(final String company_code, final String company_id, final String farr_id, final String swine_scanned_id) {
        progressBar.setVisibility(View.VISIBLE);
        layout_add.setVisibility(View.GONE);
        String URL = getString(R.string.URL_online)+"scan_eartag/history/pig_born_alive_get.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    progressBar.setVisibility(View.GONE);
                    layout_add.setVisibility(View.VISIBLE);

                    JSONObject Object = new JSONObject(response);
                    JSONArray details = Object.getJSONArray("data");
                    for(int i = 0; i < details.length(); i++){
                        JSONObject r = details.getJSONObject(i);
                        arrayList.add(new Farrowing_Born_Alive_model(r.getInt("id"),
                                r.getString("swine_code"),
                                r.getString("gender"),
                                r.getString("count"),
                                r.getString("weight"),
                                r.getString("swine_type"),
                                r.getString("status")));

                        Farrowing_Born_Alive_addArray_model array_model = new Farrowing_Born_Alive_addArray_model(r.getInt("id"),
                                r.getString("swine_code"),
                                r.getString("status"));
                        arrayList_add.add(array_model);

                        if (r.getString("status").equals("alive")){
                            counter++;
                        }

                    }
                    adapter = new bornAlive_adapter(getContext(), arrayList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                }
                catch (JSONException e){}
                catch (Exception e){}
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
                hashMap.put("swine_id", swine_scanned_id);
                hashMap.put("farrowing_id", farr_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public class bornAlive_adapter extends RecyclerView.Adapter<bornAlive_adapter.MyHolder> {
        ArrayList<Farrowing_Born_Alive_model> data;
        private Context context;
        private LayoutInflater inflater;
        int num, getId;
        String getSwine_code, getCount, getSwine_type, getGender, getStatus, getWeight;

        public bornAlive_adapter(Context context, ArrayList<Farrowing_Born_Alive_model> data){
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.farrowing_born_alive_container, parent,false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            getSwine_code = data.get(position).getSwine_code();
            getSwine_type = data.get(position).getSwine_type();
            getCount = data.get(position).getCount();
            getGender = data.get(position).getGender();
            getStatus = data.get(position).getStatus();
            getWeight = data.get(position).getWeight();
            getId = data.get(position).getId();
            num = position;
            num++;

            holder.text_count.setText(String.valueOf(num));
            holder.text_gender.setText(getGender);
            holder.text_weight.setText(getWeight);
            holder.text_swine_type.setText(getSwine_type);

            if (getStatus.equals("alive")){
                holder.text_eartag.setVisibility(View.GONE);
                holder.inputtext_eartag.setVisibility(View.VISIBLE);
                holder.inputtext_eartag.setText(getSwine_code);
                holder.text_status.setText("Alive");

            } else if (getStatus.equals("dead")){
                holder.inputtext_eartag.setVisibility(View.GONE);
                holder.text_eartag.setVisibility(View.VISIBLE);
                holder.text_eartag.setText(getSwine_code);
                holder.text_status.setText("This swine may be culled,died,sold or condemned.");
                // holder.inputtext_eartag.setText(getSwine_code);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            TextView text_status, text_count, text_eartag, text_swine_type, text_gender, text_weight;
            EditText inputtext_eartag;
            CardView layout;
            public MyHolder(View itemView) {
                super(itemView);
                layout = itemView.findViewById(R.id.layout);
                text_count = itemView.findViewById(R.id.text_count);
                inputtext_eartag = itemView.findViewById(R.id.inputtext_eartag);
                text_eartag = itemView.findViewById(R.id.text_eartag);
                text_swine_type = itemView.findViewById(R.id.text_swine_type);
                text_gender = itemView.findViewById(R.id.text_gender);
                text_weight = itemView.findViewById(R.id.text_weight);
                text_status = itemView.findViewById(R.id.text_status);

//                Farrowing_Born_Alive_addArray_model array_model = new Farrowing_Born_Alive_addArray_model(getId, inputtext_eartag.getText().toString());
//                arrayList_add.set(pos, array_model);

               inputtext_eartag.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                     //  Toast.makeText(context, String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String  getSwine_code = data.get(getAdapterPosition()).getSwine_code();
                        String getSwine_type = data.get(getAdapterPosition()).getSwine_type();
                        String  getCount = data.get(getAdapterPosition()).getCount();
                        String getGender = data.get(getAdapterPosition()).getGender();
                        String getStatus = data.get(getAdapterPosition()).getStatus();
                        String getWeight = data.get(getAdapterPosition()).getWeight();
                        int getId = data.get(getAdapterPosition()).getId();
                        String new_edit_text_string = s.toString();

                        try{
                            //readjust adapter
                            Farrowing_Born_Alive_model array_model = new Farrowing_Born_Alive_model(getId, new_edit_text_string,getGender,getCount,getWeight,getSwine_type,getStatus);
                            arrayList.set(getAdapterPosition(), array_model);
                            data.set(getAdapterPosition(), array_model);

                            //save to new list to be post to php
                            Farrowing_Born_Alive_addArray_model array_add_model = new Farrowing_Born_Alive_addArray_model(getId, new_edit_text_string, getStatus);
                            arrayList_add.set(getAdapterPosition(), array_add_model);
                        }
                        catch (Exception e){}
                    }
                });
            }
        }
    }

    void dialogBox(String name){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(name);
        alertDialog.setPositiveButton("Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

}
