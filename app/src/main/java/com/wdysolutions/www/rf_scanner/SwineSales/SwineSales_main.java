package com.wdysolutions.www.rf_scanner.SwineSales;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.google.gson.Gson;
import com.wdysolutions.www.rf_scanner.AppController;
import com.wdysolutions.www.rf_scanner.AuditPen.AuditPen_model_branch;
import com.wdysolutions.www.rf_scanner.AuditPen.AuditPen_model_pig;
import com.wdysolutions.www.rf_scanner.Constant;
import com.wdysolutions.www.rf_scanner.DatePicker.DatePickerCustom;
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.Modal_fragment;
import com.wdysolutions.www.rf_scanner.MonthPicker.monthPicker;
import com.wdysolutions.www.rf_scanner.MonthPicker.monthPickerInterface;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.SQLiteHelper;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;
import com.wdysolutions.www.rf_scanner.SwineSales.dialog_viewDetails.swineSales_viewDetails_main;
import com.wdysolutions.www.rf_scanner.SwineSales.dialog_viewDetails.viewDetails_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class SwineSales_main extends Fragment implements Modal_fragment.dialog_interface,SwineSales_adapter.EventListener,
                            swineSales_viewDetails_main.refreshData, monthPickerInterface {

    //spinnerbranch
    Spinner spinner_branch;
    ArrayList<AuditPen_model_branch> audit_pen_model_list_branch = new ArrayList<>();
    String selectedBranch="", month="";

    ProgressDialog loadingScan;

    //session
    SessionPreferences sessionPreferences;
    String company_code, company_id, user_id;

    //arraylist details
    ArrayList<SwineSales_model> swineSales_models = new ArrayList<>();
    SwineSales_adapter swineSales_adapter;
    RecyclerView rec_swinesales;
    Button btn_add;
    ScrollView sv_swine_sales;
    LinearLayout l_layout, layout_, layout_loading, layout_main, layout_main_loading, layout_nodata, layout_error, layout_month;

    //checked array
    ArrayList<String> checked_ids=new ArrayList<>();

    //button
    Button btn_remove;

    //checkbox
    CheckBox cb_all;
    TextView btn_date;
    String selectedMonth = "", category_id;

    SQLiteHelper sqlite;



    MenuItem max, min,low;

    TextView tx_range;
    private void initMenu(final View view){
        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        MenuItem test;
        toolbar.inflateMenu(R.menu.menu_scan_eartag);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.max) {
                    Constant.power_level = "Max";
                    tx_range.setText(Constant.power_level);
                    ((ActivityMain)getActivity()).setPower("max");
                    Toast.makeText(getActivity(), "Scan range set to Max", Toast.LENGTH_SHORT).show();
                }
                else if(menuItem.getItemId() == R.id.min) {
                    Constant.power_level = "Medium";
                    tx_range.setText(Constant.power_level);
                    ((ActivityMain)getActivity()).setPower("med");
                    Toast.makeText(getActivity(), "Scan range set to Med", Toast.LENGTH_SHORT).show();
                }
                else if(menuItem.getItemId() == R.id.low) {
                    Constant.power_level = "Short";
                    tx_range.setText(Constant.power_level);
                    ((ActivityMain)getActivity()).setPower("short");
                    Toast.makeText(getActivity(), "Scan range set to Short", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        // set default power level
        Constant.power_level = "Medium";
        tx_range.setText(Constant.power_level);
        ((ActivityMain)getActivity()).setPower("med");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.swine_sales, container, false);
        spinner_branch = view.findViewById(R.id.spinner_branch);
        sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        category_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_CATEGORY_ID);

        layout_month = view.findViewById(R.id.layout_month);
        layout_error = view.findViewById(R.id.layout_error);
        l_layout = view.findViewById(R.id.l_layout);
        layout_ = view.findViewById(R.id.layout_);
        layout_loading = view.findViewById(R.id.layout_loading);
        layout_main = view.findViewById(R.id.layout_main);
        layout_main_loading = view.findViewById(R.id.layout_main_loading);
        layout_nodata = view.findViewById(R.id.layout_nodata);
        btn_date = view.findViewById(R.id.btn_date);
        tx_range = view.findViewById(R.id.tx_range);

        btn_add = view.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_add.setEnabled(false);
                open_add_swinesales();
            }
        });

        //scrollview
        sv_swine_sales = view.findViewById(R.id.sv_swine_sales);
        sv_swine_sales.setVerticalScrollBarEnabled(false);

        //recycler main
        rec_swinesales = view.findViewById(R.id.rec_swinesales);

        //button
        btn_remove = view.findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBox_cofirmation();
            }
        });

        //checkbox
        cb_all = view.findViewById(R.id.cb_all);

        cb_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb_all.isChecked()){
                    check_all();
                }else{
                    uncheck_all();
                }
            }
        });

        layout_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_branch(company_id,company_code, "get_branch");
            }
        });

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMonthPicker();
            }
        });

        //onload get branches
        get_branch(company_id,company_code, "get_branch");

        sqlite  = new SQLiteHelper(getActivity());
        loadingScan = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        initMenu(view);
        return view;
    }

    public void openMonthPicker() {
        monthPicker datePickerFragment = new monthPicker();
        datePickerFragment.delegate = this;
        datePickerFragment.setCancelable(false);
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    public void open_add_swinesales(){
        Bundle bundle = new Bundle();
        bundle.putString("add_edit","add");
        bundle.putString("branch_id",selectedBranch);
        Fragment fragment = new SwineSales_add();
        fragment.setTargetFragment(SwineSales_main.this, 202);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,0);
        fragmentTransaction.add(R.id.container, fragment, "Main_menu").addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public void onDetach() {
        super.onDetach();
//        if(epcReceiver != null){getActivity().unregisterReceiver(epcReceiver);}
        ((ActivityMain)getActivity()).setPower("max");
    }

    @Override
    public void senddata(int okay) {

    }

    public void get_branch(final String company_id, final String company_code, final String get_type){
        layout_error.setVisibility(View.GONE);
        layout_loading.setVisibility(View.VISIBLE);
        layout_.setVisibility(View.GONE);
        audit_pen_model_list_branch.clear();
        String URL = getString(R.string.URL_online)+"audit_pen/audit_pen_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    layout_loading.setVisibility(View.GONE);
                    layout_.setVisibility(View.VISIBLE);
                    audit_pen_model_list_branch.add(new AuditPen_model_branch(0,"Please Select"));
                    JSONObject Object = new JSONObject(response);
                    JSONArray diag = Object.getJSONArray("response_branch");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        audit_pen_model_list_branch.add(new AuditPen_model_branch(cusObj.getInt("branch_id"),
                                cusObj.getString("branch")));
                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < audit_pen_model_list_branch.size(); i++) {
                        lables.add(audit_pen_model_list_branch.get(i).getBranch());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_branch.setAdapter(spinnerAdapter);
                    spinner_branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            AuditPen_model_branch click = audit_pen_model_list_branch.get(position);
                            if (!click.getBranch().equals("Please Select")){
                                selectedBranch = String.valueOf(click.getBranch_id());
                                get_swinesales(company_id,company_code, selectedBranch);
                            } else {
                                selectedBranch = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                }
                catch (JSONException e) {}
                catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    layout_error.setVisibility(View.VISIBLE);
                    layout_loading.setVisibility(View.GONE);
                    layout_.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Error internet connection", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("get_type",get_type);
                hashMap.put("company_code", company_code);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void get_swinesales(final String company_id, final String company_code, final String selectedBranch){
        swineSales_models.clear();
        l_layout.setClickable(false);
        layout_main_loading.setVisibility(View.VISIBLE);
        layout_main.setVisibility(View.GONE);
        layout_nodata.setVisibility(View.GONE);
        String URL = getString(R.string.URL_online)+"swine_sales/swine_sales_details2.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String id, count,delivery_number,pay_type,invoice_no,date_added,customer,total_amount,trSwine_a,trSwine,trSwine_e,
                        vatSwine,SwinewithHold,SwinewithHold_a,remarks,status,discount;

                try {
                    swineSales_models.clear();
                    layout_month.setVisibility(View.VISIBLE);
                    layout_main_loading.setVisibility(View.GONE);
                    layout_main.setVisibility(View.VISIBLE);
                    sv_swine_sales.setVisibility(View.VISIBLE);
                    l_layout.setClickable(true);


                    JSONObject Object = new JSONObject(response);
                    JSONArray diag = Object.getJSONArray("data");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);
                        id = cusObj.getString("id");
                        count = cusObj.getString("count");
                        delivery_number = cusObj.getString("delivery_number");
                        pay_type = cusObj.getString("pay_type");
                        invoice_no = cusObj.getString("invoice_no");
                        date_added = cusObj.getString("date_added");
                        customer = cusObj.getString("customer");
                        total_amount = cusObj.getString("total_amount");
                        trSwine_a = cusObj.getString("trSwine_a");
                        trSwine = cusObj.getString("trSwine");
                        trSwine_e = cusObj.getString("trSwine_e");
                        vatSwine = cusObj.getString("vatSwine");
                        SwinewithHold = cusObj.getString("SwinewithHold");
                        SwinewithHold_a = cusObj.getString("SwinewithHold_a");
                        remarks = cusObj.getString("remarks");
                        status = cusObj.getString("status");
                        discount = cusObj.getString("discount");
                        month = cusObj.getString("month");

                        swineSales_models.add(new SwineSales_model(id,delivery_number,pay_type,invoice_no,date_added,customer,
                                total_amount,trSwine_a,trSwine,trSwine_e,vatSwine,SwinewithHold,SwinewithHold_a,remarks,status,discount,0));
                    }

                    displayCurrentMonth();

                    if (swineSales_models.size() == 0){
                        layout_main.setVisibility(View.GONE);
                        layout_nodata.setVisibility(View.VISIBLE);
                    } else {
                        layout_main.setVisibility(View.VISIBLE);
                        layout_nodata.setVisibility(View.GONE);
                        swineSales_adapter = new SwineSales_adapter(getContext(), swineSales_models,SwineSales_main.this);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        rec_swinesales.setLayoutManager(layoutManager);
                        rec_swinesales.setAdapter(swineSales_adapter);
                        rec_swinesales.setNestedScrollingEnabled(false);
                    }

                }catch (Exception e){}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    layout_nodata.setVisibility(View.GONE);
                    layout_main_loading.setVisibility(View.GONE);
                    layout_main.setVisibility(View.VISIBLE);
                    l_layout.setClickable(true);
                    Toast.makeText(getActivity(), "Error internet connection", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("branch_id",selectedBranch);
                hashMap.put("company_code", company_code);
                hashMap.put("selectedMonth", selectedMonth);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void displayCurrentMonth(){
        if (selectedMonth.equals("")){
            String text_month = "";
            if (month.equals("01")){
                text_month = "January";
            } else if (month.equals("02")){
                text_month = "February";
            } else if (month.equals("03")){
                text_month = "March";
            } else if (month.equals("04")){
                text_month = "April";
            } else if (month.equals("05")){
                text_month = "May";
            } else if (month.equals("06")){
                text_month = "June";
            } else if (month.equals("07")){
                text_month = "July";
            } else if (month.equals("08")){
                text_month = "August";
            } else if (month.equals("09")){
                text_month = "September";
            } else if (month.equals("10")){
                text_month = "October";
            } else if (month.equals("11")){
                text_month = "November";
            } else if (month.equals("12")){
                text_month = "December";
            }
            btn_date.setText(text_month);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!selectedBranch.equals("")){
            get_swinesales(company_id,company_code, selectedBranch);
        }
    }

    @Override
    public void onEvent(String status, String delivery_number, String invoice, String payment_type, String date, String customer, String tv_total, String remarks, String tr_swine, String tr_swine_a, String tr_swine_e,String discount,String id) {

        Bundle bundle = new Bundle();
        bundle.putString("add_edit","edit");
        bundle.putString("branch_id",selectedBranch);
        bundle.putString("delivery_number",delivery_number);
        bundle.putString("invoice",invoice);
        bundle.putString("payment_type",payment_type);
        bundle.putString("date",date);
        bundle.putString("customer",customer);
        bundle.putString("remarks",remarks);
        bundle.putString("tr_swine",tr_swine);
        bundle.putString("tr_swine_a",tr_swine_a);
        bundle.putString("tr_swine_e",tr_swine_e);
        bundle.putString("discount",discount);
        bundle.putString("id",id);
        bundle.putString("tv_total",tv_total);
        bundle.putString("company_code", company_code);
        bundle.putString("company_id", company_id);

        if(status.equals("Saved")){
            Fragment fragment = new SwineSales_add();
            fragment.setTargetFragment(SwineSales_main.this, 202);
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,0);
            fragmentTransaction.add(R.id.container, fragment, "Main_menu").addToBackStack(null);
            fragmentTransaction.commit();
        } 
        else if (status.equals("Finished")){
            bundle.putString("status", "finish");
            swineSales_viewDetails_main swineSales_viewDetails_main = new swineSales_viewDetails_main();
            swineSales_viewDetails_main.setTargetFragment(this, 0);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog_swineSales");
            if (prev != null) {ft.remove(prev);}
            ft.addToBackStack(null);
            swineSales_viewDetails_main.setArguments(bundle);
            swineSales_viewDetails_main.show(ft, "dialog_swineSales");
        }else if(status.equals("Paid")){
            bundle.putString("status", "paid");
            swineSales_viewDetails_main swineSales_viewDetails_main = new swineSales_viewDetails_main();
            swineSales_viewDetails_main.setTargetFragment(this, 0);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog_swineSales");
            if (prev != null) {ft.remove(prev);}
            ft.addToBackStack(null);
            swineSales_viewDetails_main.setArguments(bundle);
            swineSales_viewDetails_main.show(ft, "dialog_swineSales");
        }
    }

    @Override
    public void onChecked(String dr_header_id) {
        checked_ids.add(dr_header_id);
    }

    @Override
    public void removeChecked(String dr_header_id) {
        checked_ids.remove(dr_header_id);
    }

    void dialogBox(String name){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        EditText text = new EditText(getActivity());
        text.setText(name);
        alertDialog.setView(text);
        alertDialog.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public void refresh() {
        get_swinesales(company_id,company_code, selectedBranch);
    }



    public void check_all(){
        checked_ids.clear();
        //dialogBox("hello");
        ArrayList<SwineSales_model> new_ss_list = new ArrayList<>();
        String id, count,delivery_number,pay_type,invoice_no,date_added,customer,total_amount,trSwine_a,trSwine,trSwine_e,
                vatSwine,SwinewithHold,SwinewithHold_a,remarks,status,discount;
        try {
            JSONArray jsonArray = new JSONArray(new Gson().toJson(swineSales_models));

          //  swineSales_models.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject r = jsonArray.getJSONObject(i);
                id = r.getString("id");

                delivery_number = r.getString("delivery_number");
                pay_type = r.getString("pay_type");
                invoice_no = r.getString("invoice_no");
                date_added = r.getString("date_added");
                customer = r.getString("customer");
                total_amount = r.getString("total_amount");
                trSwine_a = r.getString("trSwine_a");
                trSwine = r.getString("trSwine");
                trSwine_e = r.getString("trSwine_e");
                vatSwine = r.getString("vatSwine");
                SwinewithHold = r.getString("SwinewithHold");
                SwinewithHold_a = r.getString("SwinewithHold_a");
                remarks = r.getString("remarks");
                status = r.getString("status");
                discount = r.getString("discount");
                if(status.equals("Saved")){

                    swineSales_models.set(i,new SwineSales_model(id,delivery_number,pay_type,invoice_no,date_added,customer,
                            total_amount,trSwine_a,trSwine,trSwine_e,vatSwine,SwinewithHold,SwinewithHold_a,remarks,status,discount,1));
                    checked_ids.add(id);
                    swineSales_adapter.notifyItemChanged(i);
                }else{

                }
            }

        }catch (Exception e){}
      //  swineSales_models.addAll(new_ss_list);

    }

    public void uncheck_all(){

        ArrayList<SwineSales_model> new_ss_list = new ArrayList<>();
        String id, count,delivery_number,pay_type,invoice_no,date_added,customer,total_amount,trSwine_a,trSwine,trSwine_e,
                vatSwine,SwinewithHold,SwinewithHold_a,remarks,status,discount;
        try {
            JSONArray jsonArray = new JSONArray(new Gson().toJson(swineSales_models));

            //swineSales_models.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject r = jsonArray.getJSONObject(i);
                id = r.getString("id");

                delivery_number = r.getString("delivery_number");
                pay_type = r.getString("pay_type");
                invoice_no = r.getString("invoice_no");
                date_added = r.getString("date_added");
                customer = r.getString("customer");
                total_amount = r.getString("total_amount");
                trSwine_a = r.getString("trSwine_a");
                trSwine = r.getString("trSwine");
                trSwine_e = r.getString("trSwine_e");
                vatSwine = r.getString("vatSwine");
                SwinewithHold = r.getString("SwinewithHold");
                SwinewithHold_a = r.getString("SwinewithHold_a");
                remarks = r.getString("remarks");
                status = r.getString("status");
                discount = r.getString("discount");

                if(status.equals("Saved")){

                    if(checked_ids.size()>0){
                        checked_ids.remove(id);

                        swineSales_models.set(i,new SwineSales_model(id,delivery_number,pay_type,invoice_no,date_added,customer,
                                total_amount,trSwine_a,trSwine,trSwine_e,vatSwine,SwinewithHold,SwinewithHold_a,remarks,status,discount,0));
                        swineSales_adapter.notifyItemChanged(i);
                    }

                }else{
//                    new_ss_list.add(new SwineSales_model(id,delivery_number,pay_type,invoice_no,date_added,customer,
//                            total_amount,trSwine_a,trSwine,trSwine_e,vatSwine,SwinewithHold,SwinewithHold_a,remarks,status,discount,0));
                }
            }

        }catch (Exception e){}
      //  swineSales_models.addAll(new_ss_list);
        //swineSales_adapter.notifyDataSetChanged();
    }


    public void remove_selected_swinesales() {

        if(checked_ids.size()>0){
            showLoading(loadingScan, "Deleting...").show();
            btn_remove.setEnabled(false);
            counter_delete=0;
            success_delete=0;
        //    delete_swinesales(checked_ids.get(0).toString();
            for(int i =0; i<checked_ids.size();i++){
                counter_delete++;
                delete_swinesales(checked_ids.get(i),i);
            }

        }else{
            btn_remove.setEnabled(true);
            showLoading(loadingScan, null).dismiss();
            Toast.makeText(getActivity(), "Please select swine sales to delete", Toast.LENGTH_SHORT).show();
        }
    }

    int counter_delete = 0, success_delete=0;
    public void delete_swinesales(final String id, final int position){
        String URL = getString(R.string.URL_online)+"swine_sales/delete_bulk_dr.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("1")){

                    String dr_id;
                    try {
                        JSONArray jsonArray = new JSONArray(new Gson().toJson(swineSales_models));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject r = jsonArray.getJSONObject(i);
                            dr_id = r.getString("id");
                            if(dr_id.equals(id)){
                                success_delete++;
                                checked_ids.remove(id);
                                swineSales_models.remove(i);
                                swineSales_adapter.notifyDataSetChanged();
                                sqlite.ss_delete_all_table_pig(dr_id);
                            }
                        }

                        if(counter_delete==success_delete){
                            btn_remove.setEnabled(true);
                            showLoading(loadingScan, null).dismiss();
                            Toast.makeText(getActivity(), "delete success", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {}

//                    Toast.makeText(getActivity(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                } else {
                    showLoading(loadingScan, null).dismiss();
                    btn_remove.setEnabled(true);
                    Toast.makeText(getActivity(), "some dr sales failed to delete", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLoading(loadingScan, null).dismiss();
                btn_remove.setEnabled(true);
                Toast.makeText(getActivity(), "Connection error, please try again.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override


            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("user_id", user_id);
                hashMap.put("branch_id", selectedBranch);
                hashMap.put("category_id", category_id);
                hashMap.put("company_id", company_id);
                hashMap.put("id", id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode==202){
                String branch_id = data.getStringExtra("branch_id");
                get_swinesales(company_id,company_code, branch_id);
                btn_add.setEnabled(true);
            }
        }
    }

    @Override
    public void setMonth(String selectedMonth1, String value) {
        btn_date.setText(selectedMonth1);
        selectedMonth = value;
        get_swinesales(company_id,company_code, selectedBranch);
    }

    private ProgressDialog showLoading(ProgressDialog loading, String msg){
        loading.setMessage(msg);
        loading.setCancelable(false);
        return loading;
    }

    void dialogBox_cofirmation(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("Are you sure you want to delete selected items");
        alertDialog.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setNegativeButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        remove_selected_swinesales();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}