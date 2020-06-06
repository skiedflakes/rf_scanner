package com.wdysolutions.www.rf_scanner.AuditPen;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
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
import com.wdysolutions.www.rf_scanner.Constant;
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.Modal_fragment;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.SQLiteHelper;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;
import com.wdysolutions.www.rf_scanner.MultiAction.Dialog.Dialog_transferpen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AuditPen_main extends Fragment implements AuditPen_adapter.EventListener, Dialog_transferpen.uploadDialogInterface,Modal_fragment.dialog_interface{

    Spinner  spinner_pen,spinner_branch,spinner_building;
    String company_code, company_id, swine_scanned_id,selectedBranch="",selectedBuilding,selectedPen,user_id;
    String selectedBuilding_name="",selectedPen_name="";
    SessionPreferences sessionPreferences;
    ArrayList<AuditPen_model_branch> audit_pen_model_list_branch = new ArrayList<>();
    ArrayList<AuditPen_model_building> audit_pen_model_list_building = new ArrayList<>();
    ArrayList<AuditPen_model_pen> audit_pen_model_list_pen = new ArrayList<>();
    ArrayList<AuditPen_model_pig> audit_pen_model_list_pig = new ArrayList<>();

    AuditPen_adapter adapter_pig;
    RecyclerView rec_pigs;
    // Button btn_scan;
    BroadcastReceiver epcReceiver;
    LinearLayout layout_, loading_, layout_error, loading_list, bg_pen, bg_branch, bg_building, bg_scan_status;

    ProgressBar loading_pen, loading_building;
    TextView tv_scaned_total,tv_nip;
    int count_scanned=0;
    int count_total_pigs=0;
    int count_nip;

    //textview
    TextView tv_refresh;
    //test
    Button btn_test;
    EditText et_test;
    SQLiteHelper sqlite;
    MenuItem max, min,low;
    TextView tx_range, txt_error_pen, txt_error_building, txt_error;

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
        Constant.power_level = "Max";
        tx_range.setText(Constant.power_level);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.audit_pen_main, container, false);
        sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);

        txt_error = view.findViewById(R.id.txt_error);
        bg_scan_status = view.findViewById(R.id.bg_scan_status);
        txt_error_pen = view.findViewById(R.id.txt_error_pen);
        txt_error_building = view.findViewById(R.id.txt_error_building);
        bg_pen = view.findViewById(R.id.bg_pen);
        bg_branch = view.findViewById(R.id.bg_branch);
        bg_building = view.findViewById(R.id.bg_building);
        layout_ = view.findViewById(R.id.layout_);
        loading_ = view.findViewById(R.id.loading_);
        layout_error = view.findViewById(R.id.layout_error);
        loading_list = view.findViewById(R.id.loading_list);
        rec_pigs = view.findViewById(R.id.rec_list_pigs);
        rec_pigs.addItemDecoration(new DividerItemDecoration(rec_pigs.getContext(), DividerItemDecoration.VERTICAL));
        tv_scaned_total = view.findViewById(R.id.tv_scaned_total);
        tv_nip = view.findViewById(R.id.tv_nip);
        loading_pen = view.findViewById(R.id.loading_pen);
        loading_building = view.findViewById(R.id.loading_building);
        spinner_branch = view.findViewById(R.id.spinner_branch);
        spinner_pen = view.findViewById(R.id.spinner_pen);
        spinner_building = view.findViewById(R.id.spinner_building);
        tx_range = view.findViewById(R.id.tx_range);

//        btn_scan = view.findViewById(R.id.btn_scan);
//        btn_scan.setVisibility(View.GONE);
//        btn_scan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                updateSingleItem(et_test.getText().toString());
//            }
//        });

        //define sqlite
        sqlite  = new SQLiteHelper(getActivity());

        Constant.isMultiple = true;

        layout_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_branch(company_id,company_code,"get_branch");
            }
        });


        get_branch(company_id,company_code,"get_branch");
        initMenu(view);

        //test
//        btn_test = view.findViewById(R.id.btn_test);
//        et_test = view.findViewById(R.id.et_test);
//
//        btn_test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
////                String eart_tag =  hexToASCII(et_test.getText().toString());
//                String eart_tag = et_test.getText().toString();
//                String[] separated = eart_tag.split("-");
//                updateSingleItem(separated[1],separated[0]);
//
//            // int a= sqlite.audit_add_pigs_sqlite(1,"23",eart_tag);
//
//          //      Toast.makeText(getActivity(), String.valueOf(a), Toast.LENGTH_SHORT).show();
//            }
//        });

        tv_refresh = view.findViewById(R.id.tv_refresh);

        tv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedBranch.equals("")){
                    Toast.makeText(getActivity(), "Please select branch", Toast.LENGTH_SHORT).show();
                } else if (selectedBuilding.equals("")){
                    Toast.makeText(getActivity(), "Please select building", Toast.LENGTH_SHORT).show();
                } else if (selectedPen.equals("")){
                    Toast.makeText(getActivity(), "Please select pen", Toast.LENGTH_SHORT).show();
                } else {
                    refresh_audit_pen();
                    sqlite.audit_delete_all_table_pig();
                }
            }
        });

        scan_status();
        sqlite.audit_delete_all_table_pig();
        return view;
    }

    private void scan_status(){
        if (!selectedBranch.equals("") && !selectedBuilding.equals("") && !selectedPen.equals("") && getPigsStatus.equals("1")){
            bg_scan_status.setBackgroundResource(R.drawable.bg_circle_green);
        } else {
            bg_scan_status.setBackgroundResource(R.drawable.bg_circle_red);
        }
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

    private void listLoading(boolean status){
        if (status){
            tv_refresh.setVisibility(View.GONE);
            loading_list.setVisibility(View.VISIBLE);
            rec_pigs.setVisibility(View.GONE);
        } else {
            tv_refresh.setVisibility(View.VISIBLE);
            loading_list.setVisibility(View.GONE);
            rec_pigs.setVisibility(View.VISIBLE);
        }
    }

    public void get_branch(final String company_id, final String company_code, final String get_type){
        selectedBuilding = "";
        sqlite.audit_delete_all_table_pig();
        layout_error.setVisibility(View.GONE);
        layout_.setVisibility(View.GONE);
        loading_.setVisibility(View.VISIBLE);
        count_nip=0;
        tv_nip.setText(String.valueOf(count_nip));
        tv_scaned_total.setText("");
        String URL = getString(R.string.URL_online)+"transfer_pen/pen_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    layout_.setVisibility(View.VISIBLE);
                    loading_.setVisibility(View.GONE);

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
                                selectedBuilding = "";
                                selectedPen = "";
                                bg_branch.setBackgroundResource(R.drawable.bg_border_red);
                                bg_building.setBackgroundResource(R.drawable.bg_border_red);
                                bg_pen.setBackgroundResource(R.drawable.bg_border_red);
                            }
                            scan_status();
                            spinner_pen.setAdapter(null);
                            spinner_building.setAdapter(null);
                            rec_pigs.setAdapter(null);
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
//                    loading_.setVisibility(View.GONE);
//                    layout_error.setVisibility(View.VISIBLE);
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
        sqlite.audit_delete_all_table_pig();
        buildingLoading(true);
        tv_scaned_total.setText("");
        audit_pen_model_list_building = new ArrayList<>();
        count_nip=0;
        tv_nip.setText(String.valueOf(count_nip));
        String URL = getString(R.string.URL_online)+"transfer_pen/pen_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
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
                                selectedPen = "";
                                bg_building.setBackgroundResource(R.drawable.bg_border_red);
                                bg_pen.setBackgroundResource(R.drawable.bg_border_red);
                            }
                            scan_status();
                            spinner_pen.setAdapter(null);
                            rec_pigs.setAdapter(null);
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
        bg_pen.setBackgroundResource(R.drawable.bg_border_red);
        count_nip=0;
        tv_nip.setText(String.valueOf(count_nip));
        sqlite.audit_delete_all_table_pig();
        penLoading(true);
        audit_pen_model_list_pen = new ArrayList<>();
        tv_scaned_total.setText("");
        spinner_pen.setAdapter(null);
        count_scanned=0;
        count_total_pigs=0;
        String URL = getString(R.string.URL_online)+"transfer_pen/pen_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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
                                selectedPen_name =String.valueOf(click.getPen_name());
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
        sqlite.audit_delete_all_table_pig();
        listLoading(true);
        audit_pen_model_list_pig = new ArrayList<>();
        txt_error.setVisibility(View.GONE);
        txt_error.setTextColor(getResources().getColor(R.color.color_text_light_black));
        tv_scaned_total.setText("");
        count_scanned=0;
        count_total_pigs=0;
        count_nip=0;
        getPigsStatus = "";
        scan_status();
        tv_nip.setText(String.valueOf(count_nip));
        String URL = getString(R.string.URL_online)+"transfer_pen/pen_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    //dialogBox(response);
                    listLoading(false);
                    getPigsStatus = "1";
                    scan_status();

                    if(!response.equals("{\"response_swine\":[]}")){
                        JSONObject Object = new JSONObject(response);
                        JSONArray details = Object.getJSONArray("response_swine");

                        for(int i = 0; i < details.length(); i++){

                            JSONObject r = details.getJSONObject(i);
                            audit_pen_model_list_pig.add(new AuditPen_model_pig(r.getInt("swine_id"),
                                    r.getString("swine_code"),0,0));
                        }

                        //counter set text of total count of pigs inside the pen
                        count_total_pigs = details.length();
                        String total  = String.valueOf(count_scanned)+"/"+String.valueOf(count_total_pigs);
                        if(count_total_pigs>0){
                            tv_scaned_total.setText(total);
                        }

                        adapter_pig = new AuditPen_adapter(getContext(), audit_pen_model_list_pig,AuditPen_main.this);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
                        rec_pigs.setLayoutManager(layoutManager);
                        rec_pigs.setAdapter(adapter_pig);
                        //btn_scan.setVisibility(View.VISIBLE);
                        rec_pigs.scrollToPosition(audit_pen_model_list_pig.size()-1);

                        if(epcReceiver == null){
                            epcReceiver = new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    String tag = intent.getExtras().get("epc").toString();

                                    if (!tag.equals("No transponders seen")){

                                        try {
                                            String eart_tag = hexToASCII(tag);
                                            String[] separated = eart_tag.split("-");

                                            String str_characters = separated[0].replaceAll("[^A-Za-z]+", "");
                                            String str_digits = separated[0].replaceAll("\\D+","");

                                            if(str_characters.equals("wdy")){
                                                if(separated.length!=2){
                                                    Toast.makeText(context, "some ear tag is invalid", Toast.LENGTH_SHORT).show();

                                                }else{
                                                    updateSingleItem(separated[1], "1");
                                                }
                                            }else{
                                                Toast.makeText(context, "some ear tag is invalid", Toast.LENGTH_SHORT).show();
                                            }

                                        }catch (Exception e){}

                                    } else {
                                        Toast.makeText(context, "No ear tag found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            };
                        }
                        getActivity().registerReceiver(epcReceiver, new IntentFilter("epc_receive"));

                    }else{
                        rec_pigs.setVisibility(View.GONE);
                        txt_error.setVisibility(View.VISIBLE);
                        txt_error.setText("Pen is empty");
                        txt_error.setTextColor(getResources().getColor(R.color.color_red));
                    }

                }
                catch (JSONException e){}
                catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    getPigsStatus = "";
                    scan_status();
                    listLoading(false);
                    txt_error.setVisibility(View.VISIBLE);
                    txt_error.setText(getResources().getString(R.string.volley_error));
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
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void updateSingleItem(String scanned_swine_id,String actual_id) {
        int found = 0;
        if(audit_pen_model_list_pig.size()==0){
            //check_swine_id(company_id,company_code,scanned_swine_id,actual_id);
            Toast.makeText(getActivity(), "Pen is empty", Toast.LENGTH_SHORT).show();
        }else{

            try{
                JSONArray jsonArray = new JSONArray(new Gson().toJson(audit_pen_model_list_pig));
                for(int i = 0;i<jsonArray.length();i++){
                    JSONObject r = jsonArray.getJSONObject(i);
                    String swine_id = String.valueOf(r.getInt("swine_id"));
                    int check_status = r.getInt("check_status");
                    if(swine_id.equals(scanned_swine_id)){
                        found = 1;
                        if(check_status==2||check_status==3){//if check status is 2 red swine code text
                            String swine_code = r.getString("swine_code");

                            int a= sqlite.audit_add_pigs_sqlite(r.getInt("swine_id"),swine_code,actual_id);
                            if(a>0){
                                //counter trigger plus 1 here

                            }else{
                                //already in sqlite

                                audit_pen_model_list_pig.remove(i);
                                audit_pen_model_list_pig.add(new AuditPen_model_pig(r.getInt("swine_id"),swine_code,2,r.getInt("scanned_counter")+1));
                                adapter_pig.notifyDataSetChanged();
                                rec_pigs.smoothScrollToPosition(audit_pen_model_list_pig.size());
                            }

                        }else{

                            if(check_status==1){ //if check status is 1 checkbox is checked

                                // Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();

                                String swine_code = r.getString("swine_code");

                                //  check sqlite if not found add sqlite


                                int a= sqlite.audit_add_pigs_sqlite(r.getInt("swine_id"),swine_code,actual_id);
                                if(a>0){
                                    //counter trigger plus 1 here

                                }else{
                                    //already in sqlite

                                    audit_pen_model_list_pig.remove(i);
                                    audit_pen_model_list_pig.add(new AuditPen_model_pig(r.getInt("swine_id"),swine_code,1,r.getInt("scanned_counter")+1));
                                    adapter_pig.notifyDataSetChanged();
                                    rec_pigs.smoothScrollToPosition(audit_pen_model_list_pig.size());
                                }

                                //  change counter

                            }else{ //if uncheck but found in the list


                                String swine_code = r.getString("swine_code");

//                                //save sqlite
                                int a= sqlite.audit_add_pigs_sqlite(r.getInt("swine_id"),swine_code,actual_id);


//                                //count sqlite
//                                String counter = String.valueOf(sqlite.audit_count_data());
//                                Toast.makeText(getActivity(), counter, Toast.LENGTH_SHORT).show();

                                // int swine_id = r.getInt("swine_id");
                                //audit_pen_model_list_pig.set(i,new AuditPen_model_pig(r.getInt("swine_id"),swine_code,1));
                                audit_pen_model_list_pig.remove(i);
                                audit_pen_model_list_pig.add(new AuditPen_model_pig(r.getInt("swine_id"),swine_code,1,1));
                                adapter_pig.notifyDataSetChanged();
                                rec_pigs.smoothScrollToPosition(audit_pen_model_list_pig.size());

                                //counter
                                count_scanned++;
                                String total  = String.valueOf(count_scanned)+"/"+String.valueOf(count_total_pigs);
                                tv_scaned_total.setText(total);
                                // Toast.makeText(getActivity(), "Tag found", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }else{

                    }
                }

            }catch (Exception e){}

            if(found==0){ //if found

                try{
                    int counter = 0;
                    JSONArray jsonArray = new JSONArray(new Gson().toJson(audit_pen_model_list_pig));
                    for(int i = 0;i<jsonArray.length();i++){
                        JSONObject r = jsonArray.getJSONObject(i);
                        String swine_id2 = String.valueOf(r.getInt("swine_id"));
                        if(swine_id2.equals(String.valueOf(scanned_swine_id))){
                            counter = 1;
                        }else{

                        }
                    }

                    if(counter==1){

                    }else{
                        check_swine_id(company_id,company_code,scanned_swine_id,actual_id);
                    }
                }catch (Exception e){}
            }
        }
    }

    public void check_swine_id(final String company_id, final String company_code, final String swine_id,final String actual_id){

        String URL = getString(R.string.URL_online)+"audit_pen/check_swine_id2.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    int swine_id;
                    String swine_code;
                    int found=0;
                    int swine_status = 0;

                    JSONObject Object = new JSONObject(response);
                    JSONArray details = Object.getJSONArray("response_swine");
                    JSONObject x = details.getJSONObject(0);
                    swine_id = x.getInt("swine_id");
                    swine_code = x.getString("swine_code");
                    swine_status = x.getInt("swine_status");

                    int counter = 0;
                    JSONArray jsonArray = new JSONArray(new Gson().toJson(audit_pen_model_list_pig));
                    for(int i = 0;i<jsonArray.length();i++){

                        JSONObject r = jsonArray.getJSONObject(i);
                        String swine_id2 = String.valueOf(r.getInt("swine_id"));
                        if(swine_id2.equals(String.valueOf(swine_id))){
                            counter = 1;
                        }else{

                        }
                    }


                    if(counter==1){


                    }else{

                        int a = sqlite.audit_add_pigs_sqlite(swine_id,swine_code,actual_id);

                        if(audit_pen_model_list_pig.size()==0){
                            audit_pen_model_list_pig.add(new AuditPen_model_pig(swine_id,swine_code,swine_status,1));
                            adapter_pig = new AuditPen_adapter(getContext(), audit_pen_model_list_pig,AuditPen_main.this);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            ((LinearLayoutManager) layoutManager).setReverseLayout(true);
                            rec_pigs.setLayoutManager(layoutManager);
                            rec_pigs.setAdapter(adapter_pig);
                            Toast.makeText(getActivity(), "Tag not found", Toast.LENGTH_SHORT).show();
                            count_nip++;
                            tv_nip.setText(String.valueOf(count_nip));
                        }else{
                            audit_pen_model_list_pig.add(new AuditPen_model_pig(swine_id,swine_code,swine_status,1));
                            adapter_pig.notifyDataSetChanged();
                            rec_pigs.smoothScrollToPosition(audit_pen_model_list_pig.size());
                            Toast.makeText(getActivity(), "Tag not found", Toast.LENGTH_SHORT).show();
                            count_nip++;
                            tv_nip.setText(String.valueOf(count_nip));
                        }
                    }

                }catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    Toast.makeText(getActivity(), "Error internet connection please rescan tag", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("swine_id",swine_id);
                hashMap.put("company_code", company_code);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Constant.isMultiple = false;
        if(epcReceiver != null){getActivity().unregisterReceiver(epcReceiver);}
        ((ActivityMain)getActivity()).setPower("max");
    }

    @Override
    public void onEvent(String swine_id) {
        Bundle bundle = new Bundle();
        bundle.putString("swine_id",swine_id);
        bundle.putString("branch_id",selectedBranch);
        bundle.putString("selectedBuilding",selectedBuilding);
        bundle.putString("selectedPen",selectedPen);
        bundle.putString("selectedPen_name",selectedPen_name);
        bundle.putString("selectedBuilding_name",selectedBuilding_name);

        Dialog_transferpen fragment = new Dialog_transferpen();
        fragment.setTargetFragment(this, 0);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        fragment.setArguments(bundle);
        fragment.show(ft, "UploadDialogFragment");
        fragment.setCancelable(true);
    }

    @Override
    public void locate_eartag(String swine_id) {
        Bundle bundle = new Bundle();
        bundle.putString("swine_id",swine_id);
    }

    @Override
    public void senddata(String selectedPen, String remarks, String selectedDate, String selectedLocation,String swine_id) {

        try{
            if(selectedLocation.equals("")){
                if(audit_pen_model_list_pig.size()>0){

                    JSONArray jsonArray = new JSONArray(new Gson().toJson(audit_pen_model_list_pig));
                    for(int i = 0;i<jsonArray.length();i++){
                        JSONObject r = jsonArray.getJSONObject(i);
                        int check_status = r.getInt("check_status");
                        int swine_id2 = r.getInt("swine_id");
                        if(Integer.valueOf(swine_id)==swine_id2){
                            saveTransfer(String.valueOf(swine_id),selectedPen,remarks,selectedDate);
                        }
                    }
                }
            }else{

                if(audit_pen_model_list_pig.size()>0){
                    //   dialogBox(selectedLocation);
                    JSONArray jsonArray = new JSONArray(new Gson().toJson(audit_pen_model_list_pig));
                    for(int i = 0;i<jsonArray.length();i++){
                        JSONObject r = jsonArray.getJSONObject(i);
                        int check_status = r.getInt("check_status");
                        int swine_id2 = r.getInt("swine_id");
                        saveTransfer_other_branch(String.valueOf(swine_id),selectedPen,remarks,selectedDate,selectedLocation);
                    }
                }
            }
            //get swine ids to transfer pen
        }catch (Exception e){}

    }
    public void saveTransfer(final String swine_id, final String pen_id, final String remarks, final String transfer_date) {
        layout_.setVisibility(View.GONE);
        loading_.setVisibility(View.VISIBLE);
        String URL = getString(R.string.URL_online)+"transfer_pen/pig_transfer_pen_add2.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    layout_.setVisibility(View.VISIBLE);
                    loading_.setVisibility(View.GONE);
                    if (response.equals("1")){
                        set_modal("System message","Transfer success","green");
                        count_nip--;
                        tv_nip.setText(String.valueOf(count_nip));
                        if(audit_pen_model_list_pig.size()>0) {


                            if(pen_id.equals(selectedPen)){

                                String total = String.valueOf(count_scanned)+"/"+String.valueOf(count_total_pigs);
                                tv_scaned_total.setText(total);
                                JSONArray jsonArray = new JSONArray(new Gson().toJson(audit_pen_model_list_pig));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject r = jsonArray.getJSONObject(i);
                                    int swine_id2 = r.getInt("swine_id");
                                    String swine_code = r.getString("swine_code");
                                    if(swine_id2==Integer.valueOf(swine_id)){
                                        audit_pen_model_list_pig.remove(i);
                                        audit_pen_model_list_pig.add(new AuditPen_model_pig(r.getInt("swine_id"),swine_code,0,0));
                                        adapter_pig.notifyDataSetChanged();
                                    }
                                }
                            }else{

                                count_total_pigs--;
                                String total  = String.valueOf(count_scanned)+"/"+String.valueOf(count_total_pigs);
                                tv_scaned_total.setText(total);

                                JSONArray jsonArray = new JSONArray(new Gson().toJson(audit_pen_model_list_pig));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject r = jsonArray.getJSONObject(i);
                                    int swine_id2 = r.getInt("swine_id");

                                    if(swine_id2==Integer.valueOf(swine_id)){
                                        audit_pen_model_list_pig.remove(i);
                                        adapter_pig.notifyDataSetChanged();
                                    }
                                }
                            }
                        }

                    } else if (response.equals("2")){
                        set_modal("System message","Pen is Full!","red");
                    } else if (response.equals("3")){
                        //  Toast.makeText(getActivity(), "Unable to transfer Non-weaner to Farrowing Pen", Toast.LENGTH_SHORT).show();
                        set_modal("System message","Unable to transfer Non-weaner to Farrowing Pen!","red");

                    }
                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("branch_id", selectedBranch);
                hashMap.put("userid", user_id);
                hashMap.put("pen_id", pen_id);
                hashMap.put("remarks", remarks);
                hashMap.put("swine_id", swine_id);
                hashMap.put("transfer_date", transfer_date);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void saveTransfer_other_branch(final String swine_id, final String pen_id, final String remarks, final String transfer_date,final String selectedLocation) {
//        dialogBox("swine_id: "+swine_id+" company_id: "+company_id+" selectedBranch: "+selectedBranch+" user_id: "+user_id+" pen_id: "+pen_id);
//      //  Toast.makeText(getContext(), "savetransfer", Toast.LENGTH_SHORT).show();
        layout_.setVisibility(View.GONE);
        loading_.setVisibility(View.VISIBLE);
        String URL = getString(R.string.URL_online)+"transfer_pen/pig_transfer_pen_add_other_loc.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    layout_.setVisibility(View.VISIBLE);
                    loading_.setVisibility(View.GONE);
                    if (response.equals("1")){
                        set_modal("System message","Transfer success","green");
                        count_nip--;
                        tv_nip.setText(String.valueOf(count_nip));
                        if(audit_pen_model_list_pig.size()>0) {
                            count_total_pigs--;
                            String total  = String.valueOf(count_scanned)+"/"+String.valueOf(count_total_pigs);
                            tv_scaned_total.setText(total);

                            JSONArray jsonArray = new JSONArray(new Gson().toJson(audit_pen_model_list_pig));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject r = jsonArray.getJSONObject(i);
                                int swine_id2 = r.getInt("swine_id");

                                if(swine_id2==Integer.valueOf(swine_id)){
                                    audit_pen_model_list_pig.remove(i);
                                    adapter_pig.notifyDataSetChanged();
                                }
                            }
                        }
                    } else if (response.equals("2")){
                        set_modal("System message","Pen is Full!","red");

                    } else if (response.equals("3")){
                        set_modal("System message","Unable to transfer Non-weaner to Farrowing Pen!","red");

                    }
                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
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
                hashMap.put("userid", user_id);
                hashMap.put("pen_id", pen_id);
                hashMap.put("remarks", remarks);
                hashMap.put("breed_date", transfer_date);
                hashMap.put("locations_other_branch", selectedLocation);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void set_modal(String tittle, String text, String color){
        Bundle bundle = new Bundle();
        bundle.putString("tittle",tittle);
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

    }

    void dialogBox(String name){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(name);
        alertDialog.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void refresh_audit_pen(){
        count_nip=0;
        tv_nip.setText("");
        get_pigs(company_id,company_code,"get_swine",selectedBranch,selectedPen);
    }

}