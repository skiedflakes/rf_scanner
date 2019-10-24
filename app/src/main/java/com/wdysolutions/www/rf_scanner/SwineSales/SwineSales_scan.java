package com.wdysolutions.www.rf_scanner.SwineSales;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.wdysolutions.www.rf_scanner.AuditPen.AuditPen_model_building;
import com.wdysolutions.www.rf_scanner.AuditPen.AuditPen_model_pen;
import com.wdysolutions.www.rf_scanner.AuditPen.AuditPen_model_pig;
import com.wdysolutions.www.rf_scanner.Modal_fragment;
import com.wdysolutions.www.rf_scanner.MultiAction.Dialog.Dialog_transferpen;
import com.wdysolutions.www.rf_scanner.MultiAction.TransferPen_main;
import com.wdysolutions.www.rf_scanner.MultiAction.Transfer_adapter;
import com.wdysolutions.www.rf_scanner.MultiAction.Transfer_model_pig;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.SQLiteHelper;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;
import com.wdysolutions.www.rf_scanner.SwineSales.dialog_viewDetails.viewDetails_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class SwineSales_scan extends Fragment implements SwineSales_scan_adapter.EventListener,Modal_fragment.dialog_interface {

    ArrayList<AuditPen_model_branch>audit_pen_model_list_branch  = new ArrayList<>();
    ArrayList<AuditPen_model_building> audit_pen_model_list_building = new ArrayList<>();
    ArrayList<AuditPen_model_pen> audit_pen_model_list_pen = new ArrayList<>();
    ArrayList<SwineSales_scan_pigs_model> audit_pen_model_list_pig = new ArrayList<>();
    //initialize bundle
    String branch_id="", dr_header_id="",ave_price="",ave_weight="",dr_date="",dr_num="",yes_no="";
    BroadcastReceiver epcReceiver;
    String company_code, company_id,user_id;
    SessionPreferences sessionPreferences;
    ArrayList<SwineSales_scan_model> swine_sales_list_pig = new ArrayList<>();
    ArrayList<SwineSales_scan_model> swine_sales_list_pig_temp = new ArrayList<>();
    SwineSales_scan_adapter adapter_pig;
    Button btn_add_cd;
    int count_pigs=0;
    int counter = 0;
    int count_total=0;
    double price=0,weight=0,subtotal=0;
    TextView tv_clear;
    int total_scanned = 0;
    boolean isLoading = false;
    boolean start_scan= false;

    SQLiteHelper sqlite;

    RecyclerView rec_list_pigs;

    LinearLayout l_layout_input_eartag,l_layout_input,bg_pen,bg_branch,bg_building,layout_,loading_,layout_table,bg_scan_status;
    CheckBox cb_input;
    EditText et_weight,et_price;

    TextView tv_scaned_total, txt_error_pen, txt_error_building;
    String scanned_total;
    ProgressDialog loadingScan;

    //filter
    ProgressBar loading_pen, loading_building, loading_table;
    String selectedBranch="",selectedBuilding="",selectedPen="",selectedBuilding_name;
    Spinner spinner_pen,spinner_branch,spinner_building;

    //test
    Button btn_test;
    EditText et_test;

    //modal anti spam
    Boolean isModalOff=true;

    //enable scan by pen
    Boolean is_scan_enable=true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.swine_sales_scan, container, false);
        sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        sqlite  = new SQLiteHelper(getActivity());

        bg_scan_status = view.findViewById(R.id.bg_scan_status);
        layout_table = view.findViewById(R.id.layout_table);
        loading_table = view.findViewById(R.id.loading_table);
        txt_error_pen = view.findViewById(R.id.txt_error_pen);
        txt_error_building = view.findViewById(R.id.txt_error_building);
        bg_pen = view.findViewById(R.id.bg_pen);
        bg_branch = view.findViewById(R.id.bg_branch);
        bg_building = view.findViewById(R.id.bg_building);
        loadingScan = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        et_weight = view.findViewById(R.id.et_weight);
        et_price = view.findViewById(R.id.et_price);
        l_layout_input_eartag = view.findViewById(R.id.l_layout_input_eartag);
        l_layout_input = view.findViewById(R.id.l_layout_input);
        cb_input = view.findViewById(R.id.cb_input);
        tv_scaned_total = view.findViewById(R.id.tv_scaned_total);
        layout_ = view.findViewById(R.id.layout_);
        loading_ = view.findViewById(R.id.loading_);

        //add filter
        spinner_branch = view.findViewById(R.id.spinner_branch);
        spinner_building = view.findViewById(R.id.spinner_building);
        spinner_pen = view.findViewById(R.id.spinner_pen);
        loading_pen = view.findViewById(R.id.loading_pen);
        loading_building = view.findViewById(R.id.loading_building);
        get_branch(company_id,company_code,"get_branch");

        //bundle
        Bundle bundle = getArguments();
        if(bundle != null){

            branch_id = bundle.getString("branch_id");
            dr_header_id = bundle.getString("dr_header_id");
            ave_price = bundle.getString("ave_price");
            ave_weight = bundle.getString("ave_weight");
            dr_date = bundle.getString("dr_date");
            dr_num = bundle.getString("dr_num");
            yes_no = bundle.getString("yes_no");

            if(yes_no.equals("yes")){
                l_layout_input.setVisibility(View.VISIBLE);
            }else{
                l_layout_input.setVisibility(View.GONE);
            }


            try {

                if (!ave_price.equals("")) {
                    price = Double.valueOf(ave_price);
                }
                if( !ave_weight.equals("")){
                    weight = Double.valueOf(ave_weight);
                }

                subtotal = price * weight;
            } catch (Exception e){}
        }

        //recycler
        rec_list_pigs = view.findViewById(R.id.rec_list_pigs);

        //button
        btn_add_cd = view.findViewById(R.id.btn_add_cd);
        btn_add_cd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_to_delivery();
            }
        });

        tv_clear = view.findViewById(R.id.tv_clear);
        tv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBox_clear();
            }
        });

        //if sqlite is not empty set previously scanned

        try {
            if (sqlite.ss_get_pigs_sqlite(dr_num).size() != 0) {
                //    count_scanned = sqlite.get_pigs_sqlite().size(); //set counter scanned
//            tv_scaned_total.setText(String.valueOf(count_scanned));
                swine_sales_list_pig = sqlite.ss_get_pigs_sqlite(dr_num);
                adapter_pig = new SwineSales_scan_adapter(getContext(), swine_sales_list_pig, SwineSales_scan.this);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                rec_list_pigs.setLayoutManager(layoutManager);
                rec_list_pigs.setAdapter(adapter_pig);
                rec_list_pigs.setNestedScrollingEnabled(false);

                scanned_total = String.valueOf(swine_sales_list_pig.size());
                tv_scaned_total.setText("Total Scanned: "+scanned_total);

            } else {
            }
        }catch (Exception e){}


       // test
//        btn_test = view.findViewById(R.id.btn_test);
//        et_test = view.findViewById(R.id.et_test);

        //btn_test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String ear_tag = et_test.getText().toString();
//                String[] separated = ear_tag.split("-");
////                            getDetails(company_code, company_id,separated[1]);
//
//                String str_characters = separated[0].replaceAll("[^A-Za-z]+", "");
//                String str_digits = separated[0].replaceAll("\\D+","");
//                if(str_characters.equals("wdy")){
//
//
//                    if (sqlite.ss_get_pigs_sqlite(dr_num).size() != 0) {
//
//                        String check_swine = sqlite.checkSwine(separated[1]);
//
//                        if (check_swine.equals("")) {
//                            add_eartag(company_id, company_code, separated[1]);
//                        } else {
//                            Toast.makeText(getActivity(), "ear tag already added", Toast.LENGTH_SHORT).show();
//                        }
//
//                    } else {
//
//                        String check_swine = sqlite.checkSwine(separated[1]);
//
//                        if (check_swine.equals("")) {
//                            add_eartag(company_id, company_code, separated[1]);
//                        } else {
//                            Toast.makeText(getActivity(), "ear tag already added", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//
//
//
//                }else{
//                    Toast.makeText(getContext(), "ear tag invalid", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });

        l_layout_input_eartag.setVisibility(View.GONE);
        //
        cb_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_input.isChecked()){
                    l_layout_input_eartag.setVisibility(View.VISIBLE);
                }else{

                    l_layout_input_eartag.setVisibility(View.GONE);
                    weight = 0;
                    price = 0;
                    subtotal =0;
                }
            }
        });


        et_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!String.valueOf(editable).equals("")){
                    Double price2 =Double.valueOf(String.valueOf(editable));
                    price = price2;
                    update_all_price(price2);
                }
            }
        });

        et_weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!String.valueOf(editable).equals("")){
                    Double weight2 =Double.valueOf(String.valueOf(editable));
                    weight = weight2;
                    update_all_weight(weight2);
                }
            }
        });



        //scanner
        if(epcReceiver == null){
            epcReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                        if(isModalOff){
                            String tag = intent.getExtras().get("epc").toString();
                            if (!tag.equals("No transponders seen")){

                                try {

                                    if (isLoading){
                                        Toast.makeText(context, "Please wait loading data...", Toast.LENGTH_SHORT).show();
                                    }
                                    else {

                                        //
                                        String eart_tag =  hexToASCII(tag);
                                        String[] separated = eart_tag.split("-");
                                        //                            getDetails(company_code, company_id,separated[1]);

                                        String str_characters = separated[0].replaceAll("[^A-Za-z]+", "");
                                        String str_digits = separated[0].replaceAll("\\D+","");
                                        if(str_characters.equals("wdy")){


                                            if (sqlite.ss_get_pigs_sqlite(dr_num).size() != 0) {

                                                String check_swine = sqlite.checkSwine(separated[1]);

                                                if (check_swine.equals("")) {
                                                    check_swine_list( separated[1]);
                                                    //add_eartag(company_id, company_code, separated[1]);
                                                } else {
                                                    Toast.makeText(getActivity(), "Some scanned ear tag is already saved", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {

                                                String check_swine = sqlite.checkSwine(separated[1]);

                                                if (check_swine.equals("")) {
                                                    check_swine_list( separated[1]);
                                                    // add_eartag(company_id, company_code, separated[1]);
                                                } else {
                                                    Toast.makeText(getActivity(), "Some scanned ear tag is already saved", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                        }else{
                                            Toast.makeText(context, "ear tag invalid", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }catch (Exception e){}


                            } else {
                                Toast.makeText(context, "No eartag found", Toast.LENGTH_SHORT).show();
                            }


                        }else{

                            Toast.makeText(context, "Unable to scan ear tag please complete action.", Toast.LENGTH_SHORT).show();
                        }

                }
            };
        }

        getActivity().registerReceiver(epcReceiver, new IntentFilter("epc_receive"));

        return view;
    }

    private void scan_status(){
        if (!selectedBranch.equals("") && !selectedBuilding.equals("") && !selectedPen.equals("") && getPigsStatus.equals("1")){
            bg_scan_status.setBackgroundResource(R.drawable.bg_circle_green);
        } else {
            bg_scan_status.setBackgroundResource(R.drawable.bg_circle_red);
        }
    }

    private ProgressDialog showLoading(ProgressDialog loading, String msg){
        loading.setMessage(msg);
        loading.setCancelable(false);
        return loading;
    }

    public void update_all_price(Double price){
        try{
            if(swine_sales_list_pig.size()>0) {
                sqlite.ss_delete_all_table_pig(dr_num);
                JSONArray jsonArray = new JSONArray(new Gson().toJson(swine_sales_list_pig));

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject r = jsonArray.getJSONObject(i);
                    int swine_id = r.getInt("swine_id");
                    String swine_code =  r.getString("swine_code");
                    String age =  r.getString("age");
                    String feeds_cons = r.getString("feeds_cons");
                    Double weight = r.getDouble("weight");
                    Double subtotal = price * weight;

                    swine_sales_list_pig.set(i,new SwineSales_scan_model(swine_id, swine_code,age,feeds_cons,weight,price,subtotal,dr_num));


                    //sqlite add single array
                    swine_sales_list_pig_temp = new ArrayList<>();
                    swine_sales_list_pig_temp.add(new SwineSales_scan_model(swine_id, swine_code,age,feeds_cons,weight,price,subtotal,dr_num));
                    sqlite.ss_add_pigs_sqlite(swine_sales_list_pig_temp);
                }
                adapter_pig = new SwineSales_scan_adapter(getContext(), swine_sales_list_pig,SwineSales_scan.this);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                rec_list_pigs.setLayoutManager(layoutManager);
                rec_list_pigs.setAdapter(adapter_pig);
                rec_list_pigs.setNestedScrollingEnabled(false);
            }
        }catch (Exception e){}
    }

    public void update_all_weight(Double weight){
        try{
            if(swine_sales_list_pig.size()>0) {
                sqlite.ss_delete_all_table_pig(dr_num);
                JSONArray jsonArray = new JSONArray(new Gson().toJson(swine_sales_list_pig));

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject r = jsonArray.getJSONObject(i);
                    int swine_id = r.getInt("swine_id");
                    String swine_code =  r.getString("swine_code");
                    String age =  r.getString("age");
                    String feeds_cons = r.getString("feeds_cons");
                    Double price = r.getDouble("price");
                    Double subtotal = price * weight;

                    swine_sales_list_pig.set(i,new SwineSales_scan_model(swine_id, swine_code,age,feeds_cons,weight,price,subtotal,dr_num));

                    //sqlite add single array
                    swine_sales_list_pig_temp = new ArrayList<>();
                    swine_sales_list_pig_temp.add(new SwineSales_scan_model(swine_id, swine_code,age,feeds_cons,weight,price,subtotal,dr_num));
                    sqlite.ss_add_pigs_sqlite(swine_sales_list_pig_temp);

                }
                adapter_pig = new SwineSales_scan_adapter(getContext(), swine_sales_list_pig,SwineSales_scan.this);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                rec_list_pigs.setLayoutManager(layoutManager);
                rec_list_pigs.setAdapter(adapter_pig);
                rec_list_pigs.setNestedScrollingEnabled(false);
            }
        }catch (Exception e){}
    }

    private static String asciiToHex(String asciiValue) {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++)
        {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    private static String hexToASCII(String hexValue) {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexValue.length(); i += 2)
        {
            String str = hexValue.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString().replaceAll(" ","");
    }

    void dialogBox_clear(){
        isModalOff=false;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage("Are you sure you want to clear all scanned tags?");
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        isModalOff=true;

                        sqlite.ss_delete_all_table_pig(dr_num);
                        swine_sales_list_pig.clear();
                        rec_list_pigs.setAdapter(null);

                        scanned_total = String.valueOf(swine_sales_list_pig.size());
                        tv_scaned_total.setText("Total Scanned: "+scanned_total);

                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    //  ~~ php volley ~~

    public void check_swine_list(final String ear_tag){
        swine_sales_list_pig_temp = new ArrayList<>();
        if(audit_pen_model_list_pig.size()>0){

            boolean isInsideList = false;
            for(int i=0;i<audit_pen_model_list_pig.size();i++){
                SwineSales_scan_pigs_model scan_model  = audit_pen_model_list_pig.get(i);
                int swine_id = scan_model.getSwine_id();
                String swine_code = scan_model.getSwine_code();
                String age = scan_model.getAge();
                if(scan_model.getSwine_id()==Integer.valueOf(ear_tag)){
                    isInsideList = true;

                    if (swine_sales_list_pig.size() == 0) {

                        //add to recyclerview
                        swine_sales_list_pig.add(new SwineSales_scan_model(swine_id, swine_code,age,"",weight,price,subtotal,dr_num));
                        adapter_pig = new SwineSales_scan_adapter(getContext(), swine_sales_list_pig,SwineSales_scan.this);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        rec_list_pigs.setLayoutManager(layoutManager);
                        rec_list_pigs.setAdapter(adapter_pig);
                        rec_list_pigs.setNestedScrollingEnabled(false);
                        total_scanned++;

                        scanned_total = String.valueOf(swine_sales_list_pig.size());
                        tv_scaned_total.setText("Total Scanned: "+scanned_total);

                        //sqlite add single array
                        swine_sales_list_pig_temp.add(new SwineSales_scan_model(swine_id, swine_code,age,"",weight,price,subtotal,dr_num));
                        sqlite.ss_add_pigs_sqlite(swine_sales_list_pig_temp);

                    }else {
                        int found=0;
                        //counter
                        try {
                            JSONArray jsonArray2 = new JSONArray(new Gson().toJson(swine_sales_list_pig));
                            for (int x = 0; x < jsonArray2.length(); x++) {

                                JSONObject a = jsonArray2.getJSONObject(x);
                                int swine_id_new = a.getInt("swine_id");

                                if (swine_id_new == swine_id) {
                                    found = 1;
                                }
                            }
                        }catch (Exception e){}
                        if (found == 1) {

                        } else {

                            //counter
//                                count_scanned++;
//                                String total = String.valueOf(count_scanned);
//                                tv_scaned_total.setText(total);

                            //add data to recycler view
                            swine_sales_list_pig.add(new SwineSales_scan_model(swine_id, swine_code, age, "", weight, price, subtotal, dr_num));
                            adapter_pig.notifyDataSetChanged();
                            total_scanned++;


                            scanned_total = String.valueOf(swine_sales_list_pig.size());
                            tv_scaned_total.setText("Total Scanned: "+scanned_total);

                            //sqlite add single array
                            swine_sales_list_pig_temp.add(new SwineSales_scan_model(swine_id, swine_code, age, "", weight, price, subtotal, dr_num));
                            sqlite.ss_add_pigs_sqlite(swine_sales_list_pig_temp);

                        }
                    }

                }
            }

            if(!isInsideList){
                yes_no_alert_dialog(ear_tag,"Scanned ear tag is in WRONG PEN or invalid. Do you wish to proceed?");

            }

        }else{

        }


    }


    //modal check if wrong pen
    public void yes_no_alert_dialog(final String swine_id,String name){
        isModalOff=false;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("System message");
        alertDialog.setMessage(name);
        alertDialog.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                        request_data_pen(swine_id,"get_pen");
                    }
                });
        alertDialog.setNegativeButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                        request_data_swine(swine_id,"get_swine");
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void request_data_pen(final String swine_id,final String request_type){
        isModalOff=false;
        loading_table.setVisibility(View.VISIBLE);
        layout_table.setVisibility(View.GONE);

        String URL = getString(R.string.URL_online) + "swine_sales/request_swine_data.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    loading_table.setVisibility(View.GONE);
                    layout_table.setVisibility(View.VISIBLE);

                    if(response.equals("{\"get_swine\":[]}")){
                        set_modal("System Message","Invalid ear tag","red");
                    }else{

                        JSONObject Object = new JSONObject(response);

                        JSONArray diag2 = Object.getJSONArray("get_pen");
                        JSONObject cusObj2 = (JSONObject) diag2.get(0);
                        String building_name = cusObj2.getString("building_name");
                        String pen_name = cusObj2.getString("pen_name");
                        String branch = cusObj2.getString("branch");
                        String swine_code = cusObj2.getString("swine_code");

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("Scanned Ear tag: "+swine_code);
                        alertDialog.setMessage("Branch: "+branch+"\n"+
                                "Building: "+building_name+"\n"+
                                "Pen: "+pen_name+"\n");
                        alertDialog.setNegativeButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int which) {
                                        dialog.dismiss();
                                        isModalOff=true;

                                    }
                                });
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    }


                } catch (Exception e) {}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    loading_table.setVisibility(View.GONE);
                    layout_table.setVisibility(View.VISIBLE);
                    isModalOff=true;

                    Toast.makeText(getActivity(), "Connection error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {}
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("branch_id", branch_id);
                hashMap.put("company_code", company_code);
                hashMap.put("swine_id", swine_id);
                hashMap.put("request_type", request_type);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void request_data_swine(final String swine_id,final String request_type){
        loading_table.setVisibility(View.VISIBLE);
        layout_table.setVisibility(View.GONE);

        String URL = getString(R.string.URL_online) + "swine_sales/request_swine_data.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                isModalOff=true;

                try {
                    loading_table.setVisibility(View.GONE);
                    layout_table.setVisibility(View.VISIBLE);
                    if(response.equals("{\"get_swine\":[]}")){
                        set_modal("System Message","Invalid ear tag","red");
                    }else{
                        String building_name = "";

                        JSONObject Object = new JSONObject(response);
                        JSONArray diag = Object.getJSONArray("get_swine");

                        JSONObject cusObj = (JSONObject) diag.get(0);
                        String swine_code = cusObj.getString("swine_code");
                        String age = cusObj.getString("age");
                        String piglet_status = cusObj.getString("piglet_status");

                        if(piglet_status.equals("true")){
                            set_modal("Unable to scan","unable to add ear tag, ear tag is a piglet or pen has a piglet","red");
                        }else{

                            if (swine_sales_list_pig.size() == 0) {
                                //add to recyclerview
                                swine_sales_list_pig.add(new SwineSales_scan_model(Integer.valueOf(swine_id), swine_code,age,"",weight,price,subtotal,dr_num));
                                adapter_pig = new SwineSales_scan_adapter(getContext(), swine_sales_list_pig,SwineSales_scan.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                rec_list_pigs.setLayoutManager(layoutManager);
                                rec_list_pigs.setAdapter(adapter_pig);
                                rec_list_pigs.setNestedScrollingEnabled(false);
                                total_scanned++;

                                scanned_total = String.valueOf(swine_sales_list_pig.size());
                                tv_scaned_total.setText("Total Scanned: "+scanned_total);

                                //sqlite add single array
                                swine_sales_list_pig_temp.add(new SwineSales_scan_model(Integer.valueOf(swine_id), swine_code,age,"",weight,price,subtotal,dr_num));
                                sqlite.ss_add_pigs_sqlite(swine_sales_list_pig_temp);

                            }else {
                                int found=0;
                                //counter
                                try {
                                    JSONArray jsonArray2 = new JSONArray(new Gson().toJson(swine_sales_list_pig));
                                    for (int x = 0; x < jsonArray2.length(); x++) {

                                        JSONObject a = jsonArray2.getJSONObject(x);
                                        int swine_id_new = a.getInt("swine_id");

                                        if (swine_id_new == Integer.valueOf(swine_id)) {
                                            found = 1;
                                        }
                                    }
                                }catch (Exception e){}
                                if (found == 1) {

                                } else {

                                    //counter
//                                count_scanned++;
//                                String total = String.valueOf(count_scanned);
//                                tv_scaned_total.setText(total);

                                    //add data to recycler view
                                    swine_sales_list_pig.add(new SwineSales_scan_model(Integer.valueOf(swine_id), swine_code, age, "", weight, price, subtotal, dr_num));
                                    adapter_pig.notifyDataSetChanged();
                                    total_scanned++;


                                    scanned_total = String.valueOf(swine_sales_list_pig.size());
                                    tv_scaned_total.setText("Total Scanned: "+scanned_total);

                                    //sqlite add single array
                                    swine_sales_list_pig_temp.add(new SwineSales_scan_model(Integer.valueOf(swine_id), swine_code, age, "", weight, price, subtotal, dr_num));
                                    sqlite.ss_add_pigs_sqlite(swine_sales_list_pig_temp);

                                }
                            }

                        }

                    }


                } catch (Exception e) {}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    isModalOff=true;
                    loading_table.setVisibility(View.GONE);
                    layout_table.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Connection error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {}
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("branch_id", branch_id);
                hashMap.put("company_code", company_code);

                hashMap.put("swine_id", swine_id);
                hashMap.put("request_type", request_type);

                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void add_to_delivery(){

        isLoading = true;
        showLoading(loadingScan, "Loading...").show();
        int invalid_status =0;
        counter = 0;
        count_pigs = 0;
        count_total = swine_sales_list_pig.size();
        try{
            if(swine_sales_list_pig.size()>0){
                JSONArray jsonArray = new JSONArray(new Gson().toJson(swine_sales_list_pig));

                for(int i = 0;i<jsonArray.length();i++){
                    JSONObject r = jsonArray.getJSONObject(i);
                    String swine_id = String.valueOf(r.getInt("swine_id"));
                    String weight =  String.valueOf(r.getDouble("weight"));
                    String price =  String.valueOf(r.getDouble("price"));

                    if(r.getDouble("price")==0){
                        invalid_status = 1;
                    }
                    if(r.getDouble("weight")==0){
                        invalid_status = 1;
                    }
                }

                if(invalid_status!=1){
                    for(int i = 0;i<jsonArray.length();i++){
                        JSONObject r = jsonArray.getJSONObject(i);
                        String swine_id = String.valueOf(r.getInt("swine_id"));
                        String weight =  String.valueOf(r.getDouble("weight"));
                        String price =  String.valueOf(r.getDouble("price"));
                        insert_dr_details(swine_id,dr_num,dr_date,weight,price);
                        btn_add_cd.setText("Please wait loading data");
                    }
                }else{

                    showLoading(loadingScan, null).dismiss();
                    isLoading = false;
                    Toast.makeText(getActivity(), "Please fill up required fields", Toast.LENGTH_SHORT).show();
                }


            }else{
                showLoading(loadingScan, null).dismiss();
                isLoading = false;
                Toast.makeText(getActivity(), "No Scanned ear tag", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){}
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

    public void insert_dr_details(final String swine_id, final String dr_number, final String dr_date, final String weight, final String price){
        btn_add_cd.setEnabled(false);
        String URL = getString(R.string.URL_online) + "swine_sales/add_dr_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if(response.equals("1")){
                        if(swine_sales_list_pig.size()>0){
                            scanned_total = String.valueOf(swine_sales_list_pig.size());
                            tv_scaned_total.setText("Total Scanned: "+scanned_total);

                            JSONArray jsonArray = new JSONArray(new Gson().toJson(swine_sales_list_pig));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject r = jsonArray.getJSONObject(i);
                                int swine_id2 = r.getInt("swine_id");

                                if(swine_id2==Integer.valueOf(swine_id)){
                                    count_pigs++;
                                    swine_sales_list_pig.remove(i);
                                    adapter_pig.notifyDataSetChanged();

                                    //sql_lite remove
                                    sqlite.ss_delete_table_pig(swine_id);
                                }
                            }

                        }
                        //system error

                        if(count_pigs==count_total){
                            btn_add_cd.setEnabled(true);
                            //success
                            set_modal("System Message","Add success","green");
                            btn_add_cd.setText("Add to Delivery");
                            sqlite.ss_delete_all_table_pig(dr_num);
                            swine_sales_list_pig.clear();
                            rec_list_pigs.setAdapter(null);
                            rec_list_pigs.setNestedScrollingEnabled(false);

                            scanned_total = String.valueOf(swine_sales_list_pig.size());
                            tv_scaned_total.setText(scanned_total);
                            showLoading(loadingScan, null).dismiss();
                            isLoading = false;
                            intent_frag();
                        }

                    }else {
                        showLoading(loadingScan, null).dismiss();
                        isLoading = false;
                        btn_add_cd.setEnabled(true);
                        //failed
                        set_modal("System Message","failed to add remaining","red");
                        btn_add_cd.setText("Add to Delivery");

                    }

                } catch (Exception e) {}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    counter++;

                    if(count_pigs==count_total){
                        btn_add_cd.setEnabled(true);
                        //success
                        set_modal("System Message","Add success","green");
                        btn_add_cd.setText("Add to Delivery");
                        intent_frag();
                    }else if(counter==count_total){
                        btn_add_cd.setEnabled(true);
                        //failed
                        set_modal("System Message","failed to add remaining","red");
                        btn_add_cd.setText("Add to Delivery");

                    }else{

                    }
                    showLoading(loadingScan, null).dismiss();
                    isLoading = false;
                    Toast.makeText(getActivity(), "Connection error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {}
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("branch_id", branch_id);
                hashMap.put("company_code", company_code);
                hashMap.put("dr_number", dr_number);
                hashMap.put("dr_date", dr_date);
                hashMap.put("swine_id", swine_id);
                hashMap.put("weight", weight);
                hashMap.put("price", price);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void set_modal(String tittle, String text, String color){
        isModalOff=false;
        Bundle bundle = new Bundle();
        bundle.putString("tittle",tittle );
        bundle.putString("text",text);
        bundle.putString("color",color);
        Modal_fragment fragment = new Modal_fragment();
        fragment.setTargetFragment(this, 0);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        fragment.setArguments(bundle);
        fragment.show(ft, "UploadDialogFragment");
        fragment.setCancelable(true);
    }

    @Override
    public void senddata(int okay) {

        if(okay==1){
            isModalOff=true;
        }else{

        }
    }

    public void intent_frag(){
        Intent intent = new Intent(getContext(), SwineSales_scan.class);
        intent.putExtra("refresh", 1);
        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
        getFragmentManager().popBackStack();
    }

    @Override
    public void onEvent(String swine_id) {
        //delete sqlite
        scanned_total = String.valueOf(swine_sales_list_pig.size()-1);
        tv_scaned_total.setText("Ear tag saved: "+scanned_total);
        sqlite.ss_delete_table_pig(swine_id);

        for (int i=0; i<swine_sales_list_pig.size(); i++){
            SwineSales_scan_model r = swine_sales_list_pig.get(i);
            if (String.valueOf(r.getSwine_id()).equals(swine_id)){
                swine_sales_list_pig.remove(i);
                adapter_pig.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void on_Update(String swine_id, String weight, String price, String subtotal) {
        sqlite.ss_update_table_pig(swine_id,weight,price,subtotal);
    }

    @Override
    public void open_delete(boolean yes_no) {
        if(yes_no){isModalOff = false;}else{isModalOff = true;}
    }

    //new update
    public void get_branch(final String company_id, final String company_code, final String get_type){

        tv_scaned_total.setText("");
        loading_.setVisibility(View.VISIBLE);
        layout_.setVisibility(View.GONE);
        String URL = getString(R.string.URL_online)+"audit_pen/audit_pen_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    loading_.setVisibility(View.GONE);
                    layout_.setVisibility(View.VISIBLE);

                    audit_pen_model_list_branch  = new ArrayList<>();
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
                                get_building(company_id,company_code,"get_building",selectedBranch);
                                bg_branch.setBackgroundResource(R.drawable.bg_border);
                            } else {
                                selectedBranch = "";
                                bg_branch.setBackgroundResource(R.drawable.bg_border_red);
                                bg_building.setBackgroundResource(R.drawable.bg_border_red);
                                bg_pen.setBackgroundResource(R.drawable.bg_border_red);
                            }
                            scan_status();
                            spinner_pen.setAdapter(null);
                            spinner_building.setAdapter(null);
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
                    get_branch(company_id,company_code,"get_branch");
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

    public void get_building(final String company_id, final String company_code, final String get_type,final String branch_id){
        selectedBuilding = "";

        bg_building.setBackgroundResource(R.drawable.bg_border_red);
        bg_pen.setBackgroundResource(R.drawable.bg_border_red);
        buildingLoading(true);
        String URL = getString(R.string.URL_online)+"audit_pen/audit_pen_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    audit_pen_model_list_building = new ArrayList<>();
                   buildingLoading(false);
                    audit_pen_model_list_building.add(new AuditPen_model_building(0,"Please Select"));
                    JSONObject Object = new JSONObject(response);
                    JSONArray diag = Object.getJSONArray("response_building");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        audit_pen_model_list_building.add(new AuditPen_model_building(cusObj.getInt("building_id"),
                                cusObj.getString("building")));
                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < audit_pen_model_list_building.size(); i++) {
                        lables.add(audit_pen_model_list_building.get(i).getBuilding());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_building.setAdapter(spinnerAdapter);
                    spinner_building.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            AuditPen_model_building click = audit_pen_model_list_building.get(position);
                            if (!click.getBuilding().equals("Please Select")){
                                selectedBuilding = String.valueOf(click.getBuilding_id());
                                selectedBuilding_name = String.valueOf(click.getBuilding());
                                get_pen(company_id,company_code,"get_pen",selectedBranch,selectedBuilding);
                                bg_building.setBackgroundResource(R.drawable.bg_border);
                            } else {
                                selectedBuilding = "";
                                bg_building.setBackgroundResource(R.drawable.bg_border_red);
                                bg_pen.setBackgroundResource(R.drawable.bg_border_red);
                            }
                            scan_status();
                            spinner_pen.setAdapter(null);
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
                    scan_status();
                    loading_building.setVisibility(View.GONE);
                    txt_error_building.setText("Internet error, tap to refresh");
                    txt_error_building.setVisibility(View.VISIBLE);
                    txt_error_building.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            get_building(company_id,company_code,"get_building",selectedBranch);
                        }
                    });
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("get_type",get_type);
                hashMap.put("company_code", company_code);
                hashMap.put("branch_id", branch_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void get_pen(final String company_id, final String company_code, final String get_type,final String branch_id,final String building_id){
        selectedPen = "";

        penLoading(true);
        bg_pen.setBackgroundResource(R.drawable.bg_border_red);
        String URL = getString(R.string.URL_online)+"audit_pen/audit_pen_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                audit_pen_model_list_pen = new ArrayList<>();
                try {
                    penLoading(false);
                    audit_pen_model_list_pen.add(new AuditPen_model_pen(0,"Please Select"));
                    JSONObject Object = new JSONObject(response);
                    JSONArray diag = Object.getJSONArray("response_pen");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        audit_pen_model_list_pen.add(new AuditPen_model_pen(cusObj.getInt("pen_assignment_id"),
                                cusObj.getString("pen_name")));

                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < audit_pen_model_list_pen.size(); i++) {
                        lables.add(audit_pen_model_list_pen.get(i).getPen_name());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_pen.setAdapter(spinnerAdapter);
                    spinner_pen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            AuditPen_model_pen click = audit_pen_model_list_pen.get(position);
                            if (!click.getPen_name().equals("Please Select")){
                                selectedPen = String.valueOf(click.getPen_assignment_id());
//                                selectedPen_name =String.valueOf(click.getPen_name());
                                try{
                                    get_pigs(company_id,company_code,"get_swine",selectedBranch,selectedPen);
                                }catch (Exception e){}
                                bg_pen.setBackgroundResource(R.drawable.bg_border);
                            } else {
                                selectedPen = "";
                                bg_pen.setBackgroundResource(R.drawable.bg_border_red);
                            }
                            scan_status();
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
                    scan_status();
                    loading_pen.setVisibility(View.GONE);
                    txt_error_pen.setVisibility(View.VISIBLE);
                    txt_error_pen.setText("Internet error, tap to refresh");
                    txt_error_pen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            get_pen(company_id,company_code,"get_pen",selectedBranch,selectedBuilding);
                        }
                    });
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("get_type",get_type);
                hashMap.put("company_code", company_code);
                hashMap.put("building_id", building_id);
                hashMap.put("branch_id", branch_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    String getPigsStatus = "";
    public void get_pigs(final String company_id, final String company_code, final String get_type,final String branch_id,final String pen_code){
        getPigsStatus = "";
        scan_status();
        loading_table.setVisibility(View.VISIBLE);
        layout_table.setVisibility(View.GONE);
        String URL = getString(R.string.URL_online)+"swine_sales/get_pigs_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                boolean check_if_pen_has_piglet=false;
                try{
                    loading_table.setVisibility(View.GONE);
                    layout_table.setVisibility(View.VISIBLE);
                    getPigsStatus = "1";
                    scan_status();

                    audit_pen_model_list_pig = new ArrayList<>();
                    if(!response.equals("{\"response_swine\":[]}")) {

                        JSONObject Object = new JSONObject(response);
                        JSONArray details = Object.getJSONArray("response_swine");

                        for (int i = 0; i < details.length(); i++) {
                            JSONObject r = details.getJSONObject(i);

                            String check_piglet =  r.getString("piglet_status");
                            if(check_piglet.equals("true")){
                                check_if_pen_has_piglet=true;
                            }else{
                                audit_pen_model_list_pig.add(new SwineSales_scan_pigs_model(r.getInt("swine_id"),
                                        r.getString("swine_code"), 0, 0,r.getString("age")));
                            }
                        }

                        if(check_if_pen_has_piglet){
                            set_modal("Unable to scan","pen has a piglet","red");
                        }else{
                            set_modal("Start Scan","You may start scanning","green");
                        }


                    }else{

                        // set_modal("System Message","failed to load please refre","red");
                        Toast.makeText(getActivity(), "Error internet connection", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    getPigsStatus = "";
                    scan_status();

                    get_pigs(company_id,company_code,"get_swine",selectedBranch,selectedPen);
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("get_type",get_type);
                hashMap.put("company_code", company_code);
                hashMap.put("pen_code", pen_code);
                hashMap.put("branch_id", branch_id);
                hashMap.put("user_id", user_id);
                hashMap.put("dr_number", dr_num);
                hashMap.put("pen_id", pen_code);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void penLoading(boolean status){
        if (status){
            txt_error_pen.setVisibility(View.GONE);
            spinner_pen.setVisibility(View.GONE);
            loading_pen.setVisibility(View.VISIBLE);
        } else {
            spinner_pen.setVisibility(View.VISIBLE);
            loading_pen.setVisibility(View.GONE);
        }
    }

    private void buildingLoading(boolean status){
        if (status){
            txt_error_building.setVisibility(View.GONE);
            spinner_building.setVisibility(View.GONE);
            loading_building.setVisibility(View.VISIBLE);
        } else {
            spinner_building.setVisibility(View.VISIBLE);
            loading_building.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(epcReceiver != null){getActivity().unregisterReceiver(epcReceiver);}
    }

}
