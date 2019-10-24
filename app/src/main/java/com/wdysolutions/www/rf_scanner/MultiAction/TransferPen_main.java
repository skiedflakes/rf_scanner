package com.wdysolutions.www.rf_scanner.MultiAction;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.wdysolutions.www.rf_scanner.AuditPen.AuditPen_adapter;
import com.wdysolutions.www.rf_scanner.AuditPen.AuditPen_main;
import com.wdysolutions.www.rf_scanner.AuditPen.AuditPen_model_branch;
import com.wdysolutions.www.rf_scanner.AuditPen.AuditPen_model_building;
import com.wdysolutions.www.rf_scanner.AuditPen.AuditPen_model_pen;
import com.wdysolutions.www.rf_scanner.AuditPen.AuditPen_model_pig;
import com.wdysolutions.www.rf_scanner.Constant;
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.Modal_fragment;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.SQLiteHelper;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;
import com.wdysolutions.www.rf_scanner.MultiAction.Dialog.Dialog_transferpen;
import com.wdysolutions.www.rf_scanner.MultiAction.Transfer_dialog.Transfer_dialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferPen_main extends Fragment implements Transfer_adapter.EventListener, Dialog_transferpen.uploadDialogInterface,Modal_fragment.dialog_interface {
    Spinner spinner_pen, spinner_branch, spinner_building;
    ProgressBar loading_building, loading_pen;
    String company_code, company_id,selectedBranch="",user_id;
    SessionPreferences sessionPreferences;
    ArrayList<Transfer_model_pig> transfer_pen_model_list_pig = new ArrayList<>();
    ArrayList<Transfer_model_pig> transfer_pen_model_list_pig_new = new ArrayList<>();
    ArrayList<Transfer_model_pig> transfer_pen_model_list_pig_temp = new ArrayList<>();
    Transfer_adapter adapter_pig;
    RecyclerView rec_pigs;
    Button btn_transfer;
    BroadcastReceiver epcReceiver;
    LinearLayout layout_, loading_, error_, loading_list, bg_branch, bg_building, bg_pen, bg_scan_status;
    ProgressDialog loadingScan;

    int count_scanned=0;
    int count_total_pigs=0;
    TextView txt_eartag_saved, tv_clear;
    int counter_transfer=0;
    int total_transfer=0;
    SQLiteHelper sqlite;
    TextView tx_range, txt_error_pen, txt_error_building;

    private void initMenu(final View view){
        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

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
    public void onDetach() {
        super.onDetach();
        if(epcReceiver != null){
            getActivity().unregisterReceiver(epcReceiver);
        }
        ((ActivityMain)getActivity()).setPower("max");
    }

    private ProgressDialog showLoading(ProgressDialog loading, String msg){
        loading.setMessage(msg);
        loading.setCancelable(false);
        return loading;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transfer_pen_module_main, container, false);
        sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);

        bg_scan_status = view.findViewById(R.id.bg_scan_status);
        txt_error_pen = view.findViewById(R.id.txt_error_pen);
        txt_error_building = view.findViewById(R.id.txt_error_building);
        bg_pen = view.findViewById(R.id.bg_pen);
        bg_branch = view.findViewById(R.id.bg_branch);
        bg_building = view.findViewById(R.id.bg_building);
        spinner_pen = view.findViewById(R.id.spinner_pen);
        loading_pen = view.findViewById(R.id.loading_pen);
        spinner_building = view.findViewById(R.id.spinner_building);
        loading_building = view.findViewById(R.id.loading_building);
        spinner_branch = view.findViewById(R.id.spinner_branch);
        layout_ = view.findViewById(R.id.layout_);
        loading_ = view.findViewById(R.id.loading_);
        loading_list = view.findViewById(R.id.loading_list);
        error_ =  view.findViewById(R.id.error_);
        rec_pigs = view.findViewById(R.id.rec_list_pigs);
        rec_pigs.addItemDecoration(new DividerItemDecoration(rec_pigs.getContext(), DividerItemDecoration.VERTICAL));
        loadingScan = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        btn_transfer = view.findViewById(R.id.btn_transfer);
        txt_eartag_saved = view.findViewById(R.id.txt_eartag_saved);
        tv_clear = view.findViewById(R.id.tv_clear);
        tx_range = view.findViewById(R.id.tx_range);

        tv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (transfer_pen_model_list_pig.size() == 0){
                    Toast.makeText(getActivity(), "No ear tag saved", Toast.LENGTH_SHORT).show();
                } else {
                    dialogBox();
                }
            }
        });

        //define sqlite
        sqlite  = new SQLiteHelper(getActivity());

        btn_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callUploadDialog();
            }
        });

        loading_.setVisibility(View.GONE);
        layout_.setVisibility(View.VISIBLE);


        //scanner
        if(epcReceiver == null){
            epcReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    String tag = intent.getExtras().get("epc").toString();

                    if (!isModalOpen){

                        if (selectedBranch.equals("")){
                            Toast.makeText(getActivity(), "Please select branch", Toast.LENGTH_SHORT).show();
                        } else if (selectedBuilding.equals("")){
                            Toast.makeText(getActivity(), "Please select building", Toast.LENGTH_SHORT).show();
                        } else if (selectedPen.equals("")){
                            Toast.makeText(getActivity(), "Please select pen", Toast.LENGTH_SHORT).show();
                        } else if (getPigsStatus.equals("")){
                            Toast.makeText(getActivity(), "Scan status must be check", Toast.LENGTH_SHORT).show();
                        } else {

                            if (!tag.equals("No transponders seen")){

                                try{
                                    String eart_tag =  hexToASCII(tag);
                                    String[] separated = eart_tag.split("-");

                                    String str_characters = separated[0].replaceAll("[^A-Za-z]+", "");
                                    String str_digits = separated[1].replaceAll("\\D+","");


                                    if(str_characters.equals("wdy")){

                                        if (saveSQLiteSwine(Integer.valueOf(separated[1])).equals("saved")){
                                            Toast.makeText(context, "Ear tag saved.", Toast.LENGTH_SHORT).show();
                                        } else if (saveSQLiteSwine(Integer.valueOf(separated[1])).equals("already saved")){
                                            dialogBox_msg("Ear tag already saved.");
                                        } else {
                                            yes_no_alert_dialog(separated[1],"Scanned ear tag is in WRONG PEN or invalid. Do you wish to proceed?");
//
//                                          dialogBox_msg("No ear tag found or wrong selected pen.");
                                        }
                                    }else{
                                        dialogBox_msg("Scanned ear tag is invalid.");
                                    }

                                }catch (Exception e){
                                    Toast.makeText(context, "Ear tag invalid", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "No ear tag seen.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please finish action", Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
        getActivity().registerReceiver(epcReceiver, new IntentFilter("epc_receive"));


        //if sqlite is not empty set previously scanned
        if(sqlite.get_pigs_sqlite().size()!=0){
            transfer_pen_model_list_pig = sqlite.get_pigs_sqlite();
            count_scanned = sqlite.get_pigs_sqlite().size(); //set counter scanned
            txt_eartag_saved.setText("Ear tag saved: "+String.valueOf(count_scanned));
            adapter_pig = new Transfer_adapter(getContext(), transfer_pen_model_list_pig,TransferPen_main.this);
            rec_pigs.setLayoutManager(new LinearLayoutManager(getActivity()));
            rec_pigs.setAdapter(adapter_pig);
        }

        test_eartag(view);
        scan_status();
        initMenu(view);
        get_branch(company_id, company_code, "get_branch");
        return view;
    }

    private void test_eartag(View view){
        Button btn_test = view.findViewById(R.id.btn_test);
        final EditText et_test = view.findViewById(R.id.et_test);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String eart_tag =  hexToASCII(et_test.getText().toString());
                String eart_tag = et_test.getText().toString();
                String[] separated = eart_tag.split("-");
                String str_characters = separated[0].replaceAll("[^A-Za-z]+", "");
                String str_digits = separated[0].replaceAll("\\D+","");

                if (selectedBranch.equals("")){
                    Toast.makeText(getActivity(), "Please select branch", Toast.LENGTH_SHORT).show();
                } else if (selectedBuilding.equals("")){
                    Toast.makeText(getActivity(), "Please select building", Toast.LENGTH_SHORT).show();
                } else if (selectedPen.equals("")){
                    Toast.makeText(getActivity(), "Please select pen", Toast.LENGTH_SHORT).show();
                } else if (getPigsStatus.equals("")){
                    Toast.makeText(getActivity(), "Scan status must be check", Toast.LENGTH_SHORT).show();
                } else {
                    if(str_characters.equals("wdy")){

                        if (saveSQLiteSwine(Integer.valueOf(separated[1])).equals("saved")){
                            dialogBox_msg("Ear tag saved.");
                        } else if (saveSQLiteSwine(Integer.valueOf(separated[1])).equals("already saved")){
                            Toast.makeText(getContext(), "Some scanned ear tag is already saved", Toast.LENGTH_SHORT).show();
                        } else {
                            yes_no_alert_dialog(separated[1],"Scanned ear tag is in WRONG PEN or invalid. Do you wish to proceed?");
                        }
                    }else{
                        dialogBox_msg("Scanned ear tag is invalid.");
                    }
                }
            }
        });
    }

    //modal check if wrong pen
    public void yes_no_alert_dialog(final String swine_id,String name){
        isModalOpen=true;
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
        showLoading(loadingScan, "Loading...").show();
//        loading_table.setVisibility(View.VISIBLE);
//        layout_table.setVisibility(View.GONE);

        String URL = getString(R.string.URL_online) + "swine_sales/request_swine_data.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    showLoading(loadingScan, null).dismiss();
//                    loading_table.setVisibility(View.GONE);
//                    layout_table.setVisibility(View.VISIBLE);

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
                                        isModalOpen=false;

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
                    showLoading(loadingScan, null).dismiss();
//                    loading_table.setVisibility(View.GONE);
//                    layout_table.setVisibility(View.VISIBLE);
                    isModalOpen=false;

                    Toast.makeText(getActivity(), "Connection error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {}
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("branch_id", selectedBranch);
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
        showLoading(loadingScan, "Loading...").show();
        String URL = getString(R.string.URL_online) + "swine_sales/request_swine_data.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

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
                        }else {
                            transfer_pen_model_list_pig_new = new ArrayList<>();
                            if (transfer_model_pig_views.size() != 0) {

                                for (int i = 0; i < transfer_model_pig_views.size(); i++) {
                                    Transfer_model_pig model_pig = transfer_model_pig_views.get(i);

                                    if (!isSwineExist(Integer.valueOf(swine_id))) {

                                        //counter
                                        count_scanned++;
                                        String total = String.valueOf(count_scanned);
                                        txt_eartag_saved.setText("Ear tag saved: " + total);

                                        //add data to recycler view
                                        transfer_pen_model_list_pig.add(0, new Transfer_model_pig(Integer.valueOf(swine_id),
                                                swine_code,
                                                model_pig.getBranch_id(),
                                                model_pig.getBuilding_id(),
                                                model_pig.getPen_id(),
                                                0));
                                        adapter_pig = new Transfer_adapter(getActivity(), transfer_pen_model_list_pig, TransferPen_main.this);
                                        rec_pigs.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        rec_pigs.setAdapter(adapter_pig);

                                        //sqlite add single array
                                        transfer_pen_model_list_pig_new.add(new Transfer_model_pig(Integer.valueOf(swine_id),
                                                swine_code,
                                                model_pig.getBranch_id(),
                                                model_pig.getBuilding_id(),
                                                model_pig.getPen_id(),
                                                0));
                                        sqlite.add_pigs_sqlite(transfer_pen_model_list_pig_new);

                                        Toast.makeText(getActivity(), "ear tag saved", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(getActivity(), "already saved", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    }
                    showLoading(loadingScan, null).dismiss();
                    isModalOpen=false;

                } catch (Exception e) {}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    showLoading(loadingScan, null).dismiss();
                    isModalOpen=false;
                    Toast.makeText(getActivity(), "Connection error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {}
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("branch_id", selectedBranch);
                hashMap.put("company_code", company_code);
                hashMap.put("swine_id", swine_id);
                hashMap.put("request_type", request_type);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void scan_status(){
        if (!selectedBranch.equals("") && !selectedBuilding.equals("") && !selectedPen.equals("") && getPigsStatus.equals("1")){
            bg_scan_status.setBackgroundResource(R.drawable.bg_circle_green);
        } else {
            bg_scan_status.setBackgroundResource(R.drawable.bg_circle_red);
        }
    }

    ArrayList<Transfer_model_branch> transfer_model_branches = new ArrayList<>();
    public void get_branch(final String company_id, final String company_code, final String get_type){
        selectedBuilding = "";
        error_.setVisibility(View.GONE);
        layout_.setVisibility(View.GONE);
        loading_.setVisibility(View.VISIBLE);
        scan_status();
        String URL = getString(R.string.URL_online)+"audit_pen/audit_pen_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    layout_.setVisibility(View.VISIBLE);
                    loading_.setVisibility(View.GONE);

                    transfer_model_branches.add(new Transfer_model_branch(0,"Please Select"));
                    JSONObject Object = new JSONObject(response);
                    JSONArray diag = Object.getJSONArray("response_branch");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        transfer_model_branches.add(new Transfer_model_branch(cusObj.getInt("branch_id"),
                                cusObj.getString("branch")));
                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < transfer_model_branches.size(); i++) {
                        lables.add(transfer_model_branches.get(i).getBranch());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_branch.setAdapter(spinnerAdapter);
                    spinner_branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Transfer_model_branch click = transfer_model_branches.get(position);
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
//                    error_.setVisibility(View.VISIBLE);
                    get_branch(company_id, company_code, "get_branch");
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

    String selectedBuilding="", selectedBuilding_name;
    ArrayList<Transfer_model_building> transfer_model_buildings;
    public void get_building(final String company_id, final String company_code, final String get_type,final String branch_id){
        selectedPen = "";
        bg_building.setBackgroundResource(R.drawable.bg_border_red);
        bg_pen.setBackgroundResource(R.drawable.bg_border_red);
        buildingLoading(true);
        String URL = getString(R.string.URL_online)+"audit_pen/audit_pen_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    buildingLoading(false);

                    transfer_model_buildings = new ArrayList<>();
                    transfer_model_buildings.add(new Transfer_model_building("Please Select","0"));
                    JSONObject Object = new JSONObject(response);
                    JSONArray diag = Object.getJSONArray("response_building");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        transfer_model_buildings.add(new Transfer_model_building(cusObj.getString("building"),
                                cusObj.getString("building_id")));
                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < transfer_model_buildings.size(); i++) {
                        lables.add(transfer_model_buildings.get(i).getBuilding());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_building.setAdapter(spinnerAdapter);
                    spinner_building.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Transfer_model_building click = transfer_model_buildings.get(position);
                            if (!click.getBuilding().equals("Please Select")){
                                selectedBuilding = click.getBuilding_id();
                                selectedBuilding_name = click.getBuilding();
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
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                }
                catch (JSONException e){}
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

    String selectedPen="", selectedPen_name;
    ArrayList<Transfer_model_pen> transfer_model_pens;
    public void get_pen(final String company_id, final String company_code, final String get_type,final String branch_id,final String building_id){
        selectedPen = "";
        penLoading(true);
        bg_pen.setBackgroundResource(R.drawable.bg_border_red);
        String URL = getString(R.string.URL_online)+"audit_pen/audit_pen_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    penLoading(false);

                    transfer_model_pens = new ArrayList<>();
                    transfer_model_pens.add(new Transfer_model_pen("0","Please Select"));
                    JSONObject Object = new JSONObject(response);
                    JSONArray diag = Object.getJSONArray("response_pen");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        transfer_model_pens.add(new Transfer_model_pen(cusObj.getString("pen_assignment_id"),
                                cusObj.getString("pen_name")));
                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < transfer_model_pens.size(); i++) {
                        lables.add(transfer_model_pens.get(i).getPen_name());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_pen.setAdapter(spinnerAdapter);
                    spinner_pen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Transfer_model_pen click = transfer_model_pens.get(position);
                            if (!click.getPen_name().equals("Please Select")){
                                selectedPen = click.getPen_id();
                                selectedPen_name = click.getPen_name();
                                get_pigs(company_id,company_code,"get_swine",selectedBranch,selectedPen);
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
    ArrayList<Transfer_model_pig> transfer_model_pig_views;
    public void get_pigs(final String company_id, final String company_code, final String get_type,final String branch_id,final String pen_code){
        showLoading(loadingScan, "Loading...").show();
        getPigsStatus = "";
        scan_status();
        transfer_model_pig_views = new ArrayList<>();
        String URL = getString(R.string.URL_online)+"audit_pen/audit_pen_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    showLoading(loadingScan, null).dismiss();
                    getPigsStatus = "1";
                    scan_status();

                    if(!response.equals("{\"response_swine\":[]}")){
                        JSONObject Object = new JSONObject(response);

                        JSONArray details = Object.getJSONArray("response_swine");
                        for(int i = 0; i < details.length(); i++){
                            JSONObject r = details.getJSONObject(i);

                            transfer_model_pig_views.add(new Transfer_model_pig(r.getInt("swine_id"),
                                    r.getString("swine_code"),
                                    Integer.valueOf(selectedBranch),
                                    Integer.valueOf(selectedBuilding),
                                    Integer.valueOf(selectedPen),
                                    0));
                        }

                    }else{
                        Toast.makeText(getActivity(), "Pen is empty", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    showLoading(loadingScan, null).dismiss();
                    getPigsStatus = "";
                    scan_status();
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
                hashMap.put("pen_code", pen_code);
                hashMap.put("branch_id", branch_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
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
            loading_list.setVisibility(View.VISIBLE);
            rec_pigs.setVisibility(View.GONE);
        } else {
            loading_list.setVisibility(View.GONE);
            rec_pigs.setVisibility(View.VISIBLE);
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

    //transfer dialogbox
    private void callUploadDialog() {
        if(transfer_pen_model_list_pig.size()>0) {

            Bundle bundle = new Bundle();
            Transfer_dialog fragment = new Transfer_dialog();
            fragment.setTargetFragment(this, 0);
            FragmentManager manager = getFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            fragment.setArguments(bundle);
            fragment.show(ft, "UploadDialogFragment");
            fragment.setCancelable(true);

        }else{
            Toast.makeText(getContext(), "No ear tag saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void senddata(String selectedPen, String remarks, String selectedDate, String selectedLocation,String swine_id) {
        try{
            total_transfer = transfer_pen_model_list_pig.size();
            counter_transfer = 0;

            if(selectedLocation.equals("")){
                //Toast.makeText(getContext(), "local", Toast.LENGTH_SHORT).show();

                if(transfer_pen_model_list_pig.size()>0){

                    JSONArray jsonArray = new JSONArray(new Gson().toJson(transfer_pen_model_list_pig));
                    for(int i = 0;i<jsonArray.length();i++){
                        JSONObject r = jsonArray.getJSONObject(i);
                        int check_status = r.getInt("check_status");
                        int swine_id2 = r.getInt("swine_id");
                        saveTransfer(String.valueOf(swine_id2),selectedPen,remarks,selectedDate);
                    }
                }
            }else{

                if(transfer_pen_model_list_pig.size()>0){
                    showLoading(loadingScan, "Transferring...").show();
                    // dialogBox(selectedLocation);
                    JSONArray jsonArray = new JSONArray(new Gson().toJson(transfer_pen_model_list_pig));
                    for(int i = 0;i<jsonArray.length();i++){
                        JSONObject r = jsonArray.getJSONObject(i);
                        int check_status = r.getInt("check_status");
                        int swine_id2 = r.getInt("swine_id");
                        saveTransfer_other_branch(String.valueOf(swine_id2),selectedPen,remarks,selectedDate,selectedLocation);
                    }
                }
            }
            //get swine ids to transfer pen

        }catch (Exception e){}
    }

    public void saveTransfer(final String swine_id, final String pen_id, final String remarks, final String transfer_date) {
    // dialogBox("swine_id: "+swine_id+" company_id: "+company_id+" selectedBranch: "+selectedBranch+" user_id: "+user_id+" pen_id: "+pen_id);
        String URL = getString(R.string.URL_online)+"transfer_pen/pig_transfer_pen_add2.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if (response.equals("1")){
                        if(transfer_pen_model_list_pig.size()>0) {

                            count_scanned--;
                            count_total_pigs--;
                            String total  = String.valueOf(count_scanned);
                            txt_eartag_saved.setText("Ear tag saved: "+total);

                            JSONArray jsonArray = new JSONArray(new Gson().toJson(transfer_pen_model_list_pig));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject r = jsonArray.getJSONObject(i);
                                int swine_id2 = r.getInt("swine_id");

                                if(swine_id2==Integer.valueOf(swine_id)){
                                    transfer_pen_model_list_pig.remove(i);
                                    adapter_pig.notifyDataSetChanged();

                                    JSONArray jsonArray2 = new JSONArray(new Gson().toJson(transfer_pen_model_list_pig));
                                    for(int x = 0;x<jsonArray2.length();x++){
                                        JSONObject r2 = jsonArray2.getJSONObject(x);
                                        int swine_id3 = r2.getInt("swine_id");
                                        if(swine_id3 == Integer.valueOf(swine_id)){
                                            transfer_pen_model_list_pig.remove(x);
                                        }
                                    }
                                }
                            }
                        }

                    } else if (response.equals("2")){
                        Toast.makeText(getActivity(), "Pen is Full!", Toast.LENGTH_SHORT).show();
                    } else if (response.equals("3")){
                        Toast.makeText(getActivity(), "Unable to transfer Non-weaner to Farrowing Pen", Toast.LENGTH_SHORT).show();
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

    // current used
    boolean isModalTransfer = false;
    public void saveTransfer_other_branch(final String swine_id, final String pen_id, final String remarks, final String transfer_date,final String selectedLocation) {
        // showLoading(loadingScan, "Transferring...").show();
        String URL = getString(R.string.URL_online)+"transfer_pen/pig_transfer_pen_add_other_loc.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if (!isModalTransfer){

                        if (response.equals("1")){

                            counter_transfer++;
                            if(total_transfer==counter_transfer){
                                isModalTransfer = true;
                                showLoading(loadingScan, null).dismiss();
                                set_modal("System Message","Success! All ear tag was transfered!","green");
                                counter_transfer = 0;

                                count_scanned=0;
                                String total = String.valueOf(count_scanned);
                                txt_eartag_saved.setText("Ear tag saved: "+total);
                            }

                            if(transfer_pen_model_list_pig.size()>0) {

                                JSONArray jsonArray = new JSONArray(new Gson().toJson(transfer_pen_model_list_pig));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject r = jsonArray.getJSONObject(i);
                                    int swine_id2 = r.getInt("swine_id");

                                    if(swine_id2==Integer.valueOf(swine_id)){
                                        //delete sqlite
                                        count_scanned--;
                                        sqlite.delete_table_pig(swine_id);
                                        txt_eartag_saved.setText("Ear tag saved: "+String.valueOf(count_scanned));
                                        transfer_pen_model_list_pig.remove(i);
                                        adapter_pig.notifyDataSetChanged();
                                    }
                                }
                            }

                        } else if (response.equals("2")){
                            isModalTransfer = true;
                            showLoading(loadingScan, null).dismiss();
                            set_modal("System Message","Pen is Full!","red");
                        } else if (response.equals("3")){
                            isModalTransfer = true;
                            showLoading(loadingScan, null).dismiss();
                            set_modal("System Message","Unable to transfer Non-weaner to Farrowing Pen","red");
                        }
                    }

                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    set_modal("System Message","Some ear tag failed please transfer again remaining ear tag","red");
                    showLoading(loadingScan, null).dismiss();
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
                hashMap.put("transfer_date", transfer_date);
                hashMap.put("locations_other_branch", selectedLocation);
                return hashMap;
            }
        };
        //AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onEvent(String swine_id) {
        count_scanned--;
        String total = String.valueOf(count_scanned);
        txt_eartag_saved.setText("Ear tag saved: "+total);

        //delete sqlite
        sqlite.delete_table_pig(swine_id);
    }

    @Override
    public void open_delete(boolean yes_no) {
        isModalOpen = yes_no;
    }

    private String saveSQLiteSwine(int eartag){
        transfer_pen_model_list_pig_new = new ArrayList<>();
        if (transfer_model_pig_views.size() != 0){
            for (int i=0; i<transfer_model_pig_views.size(); i++){
                Transfer_model_pig model_pig = transfer_model_pig_views.get(i);

                if (!isSwineExist(eartag)){
                    if (model_pig.getSwine_id() == eartag){

                        //counter
                        count_scanned++;
                        String total = String.valueOf(count_scanned);
                        txt_eartag_saved.setText("Ear tag saved: "+total);

                        //add data to recycler view
                        transfer_pen_model_list_pig.add(0, new Transfer_model_pig(model_pig.getSwine_id(),
                                model_pig.getSwine_code(),
                                model_pig.getBranch_id(),
                                model_pig.getBuilding_id(),
                                model_pig.getPen_id(),
                                0));
                        adapter_pig = new Transfer_adapter(getActivity(), transfer_pen_model_list_pig, TransferPen_main.this);
                        rec_pigs.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rec_pigs.setAdapter(adapter_pig);

                        //sqlite add single array
                        transfer_pen_model_list_pig_new.add(new Transfer_model_pig(model_pig.getSwine_id(),
                                model_pig.getSwine_code(),
                                model_pig.getBranch_id(),
                                model_pig.getBuilding_id(),
                                model_pig.getPen_id(),
                                0));
                        sqlite.add_pigs_sqlite(transfer_pen_model_list_pig_new);

                        return "saved";
                    }

                } else {
                    return "already saved";
                }
            }
        }
        return "null";
    }

    private boolean isSwineExist(int eartag){
        if (transfer_pen_model_list_pig.size() != 0){
            for (int i=0; i<transfer_pen_model_list_pig.size(); i++){
                Transfer_model_pig model_pig = transfer_pen_model_list_pig.get(i);
                if (model_pig.getSwine_id() == eartag){
                    return true;
                }
            }
        }
        return false;
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

    void dialogBox(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage("Are you sure you want to clear all saved tags?");
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        sqlite.delete_all_table_pig();
                        transfer_pen_model_list_pig.clear();
                        rec_pigs.setAdapter(null);
                        count_scanned=0;
                        txt_eartag_saved.setText("Ear tag saved: 0");
                        Toast.makeText(getActivity(), "Successfully deleted", Toast.LENGTH_SHORT).show();
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

    @Override
    public void senddata(int okay) {

       if(okay==1){

           if(sqlite.get_pigs_sqlite().size()!=0){
               count_scanned = sqlite.get_pigs_sqlite().size(); //set counter scanned
               txt_eartag_saved.setText(String.valueOf("Ear tag saved: "+count_scanned));
               transfer_pen_model_list_pig = sqlite.get_pigs_sqlite();
               adapter_pig = new Transfer_adapter(getContext(), transfer_pen_model_list_pig,TransferPen_main.this);
               rec_pigs.setLayoutManager(new LinearLayoutManager(getActivity()));
               rec_pigs.setAdapter(adapter_pig);
           }
       } else {
           isModalTransfer = false;
       }
    }

    boolean isModalOpen = false;
    void dialogBox_msg(String msg){
        isModalOpen = true;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                        isModalOpen = false;
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
