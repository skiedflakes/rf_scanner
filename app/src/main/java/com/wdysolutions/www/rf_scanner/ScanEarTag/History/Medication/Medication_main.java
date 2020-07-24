package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Medication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wdysolutions.www.rf_scanner.AppController;
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Med_Vac_Schedule.Med_Vac_schedule_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Medication.Add_Medication.addMedication_main;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Medication_main extends Fragment {

    RecyclerView recyclerView;
    TextView null_result, error_result;
    ProgressBar loading_;
    Button btn_add;
    ArrayList<Medication_model> arrayList = new ArrayList<>();
    Medication_adapter adapter;
    LinearLayout layout_button;
    String company_code, company_id, swine_scanned_id, selectView, pen_code, array_piglets, checkedCounter, pen_type, user_id,
            category_id;


    private void initMenu(View view){
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Medication");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.medication_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        category_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_CATEGORY_ID);
        swine_scanned_id = getArguments().getString("swine_scanned_id");
        array_piglets = getArguments().getString("array_piglets");
        selectView = getArguments().getString("selectView");
        pen_code = getArguments().getString("pen_code");
        pen_type = getArguments().getString("pen_type");
        checkedCounter = getArguments().getString("checkedCounter");

        layout_button = view.findViewById(R.id.layout_button);
        recyclerView = view.findViewById(R.id.recyclerView);
        error_result = view.findViewById(R.id.error_result);
        null_result = view.findViewById(R.id.null_result);
        loading_ = view.findViewById(R.id.loading_);
        btn_add = view.findViewById(R.id.btn_add);
        Button btn_sched = view.findViewById(R.id.btn_sched);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("checkedCounter", checkedCounter);
                bundle.putString("selectView", selectView);
                bundle.putString("pen_code", pen_code);
                bundle.putString("array_piglets", array_piglets);
                bundle.putString("swine_scanned_id", swine_scanned_id);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                addMedication_main addMedication_main = new addMedication_main();
                addMedication_main.setArguments(bundle);

                if (isPiglets()){
                    if (Integer.valueOf(checkedCounter) != 0){
                        addMedication_main.show(ft, "dialog");
                    } else {
                        Toast.makeText(getActivity(), "Please select piglet on swine card.", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    addMedication_main.show(ft, "dialog");
                }
            }
        });

        if (pen_type.equals("Deceased") || pen_type.equals("Culled")){
            layout_button.setVisibility(View.GONE);
        } else {
            if (!isPiglets()){

                btn_sched.setVisibility(View.VISIBLE);
                btn_sched.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("swine_scanned_id", swine_scanned_id);
                        bundle.putString("value", "M");
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                        if (prev != null) { ft.remove(prev); }
                        ft.addToBackStack(null);
                        Med_Vac_schedule_main dialogFragment = new Med_Vac_schedule_main();
                        dialogFragment.setArguments(bundle);
                        dialogFragment.show(ft, "dialog");
                    }
                });
            }
        }

        error_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading_.setVisibility(View.VISIBLE);
                error_result.setVisibility(View.GONE);
                getMedicationDetails(company_code, company_id, swine_scanned_id, "3");
            }
        });

        initMenu(view);
        getMedicationDetails(company_code, company_id, swine_scanned_id, "3");
        return view;
    }

    private boolean isPiglets(){
        if (selectView.equals("piglet")){
            return true;
        } else {
            return false;
        }
    }

    public void getMedicationDetails(final String company_code, final String company_id, final String swine_id, final String get_type) {
        String URL = getString(R.string.URL_online)+"scan_eartag/history/"+(isPiglets() ? "pig_piglets_medication.php" : "medication_get.php");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    if (!response.equals("{\"response\":[]}")){
                        arrayList.clear();
                        null_result.setVisibility(View.GONE);
                        error_result.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        loading_.setVisibility(View.GONE);
                        JSONObject Object = new JSONObject(response);
                        JSONArray details = Object.getJSONArray("response");

                        if (isPiglets()){
                            for(int i = 0; i < details.length(); i++){
                                JSONObject r = details.getJSONObject(i);
                                arrayList.add(new Medication_model(r.getInt("id"),
                                        r.getString("product_id"),
                                        r.getString("diagnosis"),
                                        "",
                                        r.getString("cost"),
                                        r.getString("date"),
                                        r.getString("amount"),
                                        r.getString("swine_id")));
                            }
                        }
                        // sow
                        else {
                            for(int i = 0; i < details.length(); i++){
                                JSONObject r = details.getJSONObject(i);
                                arrayList.add(new Medication_model(r.getInt("id"),
                                        r.getString("product_id"),
                                        r.getString("diagnosis"),
                                        r.getString("btn"),
                                        r.getString("total_cost"),
                                        r.getString("date"),
                                        r.getString("amount"),
                                        ""));
                            }
                        }

                        adapter = new Medication_adapter(getContext(), arrayList);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    } else {
                        null_result.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        error_result.setVisibility(View.GONE);
                        loading_.setVisibility(View.GONE);
                    }
                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    error_result.setVisibility(View.VISIBLE);
                    null_result.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    loading_.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("swine_id", swine_id);
                hashMap.put("get_type", get_type);
                hashMap.put("pen_code", pen_code);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public class Medication_adapter extends RecyclerView.Adapter<Medication_adapter.MyHolder> {

        ArrayList<Medication_model> data;
        private Context context;
        private LayoutInflater inflater;
        int lastPosition = -1, num;

        public Medication_adapter(Context context, ArrayList<Medication_model> data){
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.medication_container, parent,false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            final int getId = data.get(position).getId();
            final String getDate = data.get(position).getDate();
            final String getDiagnosis = data.get(position).getDiagnosis();
            final String getAmount = data.get(position).getAmount();
            final String getBtn = data.get(position).getBtn();
            final String getTotal_cost = data.get(position).getTotal_cost();
            final String getProduct_id = data.get(position).getProduct_id();
            final String getSwine_id = data.get(position).getSwine_id();
            num = position;
            num++;

            holder.text_eartag.setText(getSwine_id);
            holder.text_count.setText(String.valueOf(num));
            holder.text_date.setText(getDate);
            holder.text_diagnosis.setText(getDiagnosis);
            holder.text_dosage.setText(getAmount);
            holder.text_total_cost.setText(getTotal_cost);
            holder.text_medicine.setText(getProduct_id);
            //setAnimation(holder.layout, position);
            holder.delete_loading.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_IN );

            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteDialog(String.valueOf(getId), holder, position);
                }
            });

            if (getBtn.equals("disabled")){
                holder.delete_layout.setVisibility(View.GONE);
            } else {
                holder.delete_layout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            TextView text_date, text_dosage, text_total_cost, text_medicine, text_diagnosis, text_count, text_eartag, eart_tag_display;
            CardView layout;
            Button btn_delete;
            ProgressBar delete_loading;
            RelativeLayout delete_layout;
            public MyHolder(View itemView) {
                super(itemView);
                delete_loading = itemView.findViewById(R.id.delete_loading);
                btn_delete = itemView.findViewById(R.id.btn_delete);
                layout = itemView.findViewById(R.id.layout);
                text_date = itemView.findViewById(R.id.text_date);
                text_dosage = itemView.findViewById(R.id.text_dosage);
                text_total_cost = itemView.findViewById(R.id.text_total_cost);
                text_medicine = itemView.findViewById(R.id.text_medicine);
                text_diagnosis = itemView.findViewById(R.id.text_diagnosis);
                text_count = itemView.findViewById(R.id.text_count);
                text_eartag = itemView.findViewById(R.id.text_eartag);
                eart_tag_display = itemView.findViewById(R.id.eart_tag_display);
                delete_layout = itemView.findViewById(R.id.delete_layout);

                if (isPiglets()){
                    eart_tag_display.setVisibility(View.VISIBLE);
                    text_eartag.setVisibility(View.VISIBLE);
                } else {
                    eart_tag_display.setVisibility(View.GONE);
                    text_eartag.setVisibility(View.GONE);
                }
            }
        }

        private void setAnimation(View viewToAnimate, int position) {
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        public void deleteMedicine(final String id, final MyHolder holder, final int position) {
            holder.btn_delete.setVisibility(View.GONE);
            holder.delete_loading.setVisibility(View.VISIBLE);
            String URL = context.getString(R.string.URL_online)+"scan_eartag/history/pig_delete_med.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try{
                        holder.btn_delete.setVisibility(View.VISIBLE);
                        holder.delete_loading.setVisibility(View.GONE);
                        if (response.equals("okay")){
                            data.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e){}
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try{
                        holder.btn_delete.setVisibility(View.VISIBLE);
                        holder.delete_loading.setVisibility(View.GONE);
                        Toast.makeText(context, "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e){}
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("company_code", company_code);
                    hashMap.put("company_id", company_id);
                    hashMap.put("swine_id", swine_scanned_id);
                    hashMap.put("id", id);
                    hashMap.put("category_id", category_id);
                    hashMap.put("user_id", user_id);
                    return hashMap;
                }
            };
            AppController.getInstance().setVolleyDuration(stringRequest);
            AppController.getInstance().addToRequestQueue(stringRequest);
        }

        void deleteDialog(final String id, final MyHolder holder, final int position){
            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(context);
            alertDialog2.setTitle("Confirm Delete...");
            alertDialog2.setMessage("Are you sure you want delete this medicine?");
            alertDialog2.setIcon(R.drawable.ic_delete_forever_24dp);
            alertDialog2.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog2.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                deleteMedicine(id, holder, position);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
            alertDialog2.show();
        }
    }
}
